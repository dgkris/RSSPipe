package com.dgkris.rsspipe;

import com.dgkris.rsspipe.feeds.models.FeedPage;
import com.dgkris.rsspipe.utils.Utils;
import com.google.gson.Gson;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.conf.ComponentConfiguration;
import org.apache.flume.sink.hbase.AsyncHbaseEventSerializer;
import org.hbase.async.AtomicIncrementRequest;
import org.hbase.async.PutRequest;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Serializes the news feed items to write to HBase schema
 */
public class RSSPipeSerializer implements AsyncHbaseEventSerializer {

    private static final Logger logger = LoggerFactory.getLogger(RSSPipeSerializer.class);
    private final List<PutRequest> puts = new ArrayList<PutRequest>();
    private final List<AtomicIncrementRequest> incs = new ArrayList<AtomicIncrementRequest>();
    private final byte[] eventCountCol = "eventCount".getBytes();
    private Gson gson = new Gson();
    private byte[] hTable;
    private byte[] colFamily;
    private Event currentEvent;
    private HashMap<Field, byte[]> columnMapping;
    private boolean shouldHashRowKey = false;
    private Field rowKeyField;


    @Override
    public void initialize(byte[] table, byte[] cf) {
        logger.info("RSSPipeSerializer initiated");
        this.hTable = table;
        this.colFamily = cf;
    }

    @Override
    public void setEvent(Event event) {
        logger.info("Event received");
        this.currentEvent = event;
    }

    @Override
    public List<PutRequest> getActions() {
        String eventStr = new String(currentEvent.getBody());

        FeedPage page = (FeedPage) gson.fromJson(eventStr, FeedPage.class);
        page.setTimestampOfStorage(String.valueOf(new DateTime().getMillis()));

        logger.info("Received " + page.toString());
        byte[] rowKeyAsBytes = null;

        String rowkeyValue = (String) Utils.getFieldValueInInstance(page, rowKeyField);
        if (shouldHashRowKey) {
            rowKeyAsBytes = getHashedRowKey(rowkeyValue);
        } else {
            rowKeyAsBytes = rowkeyValue.getBytes();
        }
        puts.clear();

        Iterator<Field> columnMapIterator = columnMapping.keySet().iterator();
        while (columnMapIterator.hasNext()) {
            Field field = columnMapIterator.next();
            String fieldValue = (String) Utils.getFieldValueInInstance(page, field);
            byte columnNameAsBytes[] = columnMapping.get(field);
            PutRequest req = new PutRequest(hTable, rowKeyAsBytes, colFamily,
                    columnNameAsBytes, fieldValue != null ? fieldValue.getBytes() : "".getBytes());
            puts.add(req);
        }

        return puts;
    }

    @Override
    public List<AtomicIncrementRequest> getIncrements() {
        incs.clear();
        incs.add(new AtomicIncrementRequest(hTable, "totalEvents".getBytes(), colFamily, eventCountCol));
        return incs;
    }

    @Override
    public void cleanUp() {
        logger.info("Cleanup of RSSPipeSerializer");
        hTable = null;
        colFamily = null;
        currentEvent = null;
        columnMapping = null;
    }

    @Override
    public void configure(Context context) {
        rowKeyField = Utils.getFieldByName(context.getString(RSSPipeConstants.ROW_KEY_PARAM_NAME), FeedPage.class);
        shouldHashRowKey = context.getBoolean(RSSPipeConstants.SHOULD_HASH_ROW_KEY_PARAM_NAME);
        columnMapping = new HashMap<Field, byte[]>();

        String mappingBlob = new String(context.getString(RSSPipeConstants.COL_CONF_PARAM_NAME));
        String[] mappingEntries = mappingBlob.split(RSSPipeConstants.COL_CONF_PARAM_DELIM);
        short i = 0;
        for (String mappingEntry : mappingEntries) {
            String mappingEntryElements[] = mappingEntry.split(":");
            Field mappedField = Utils.getFieldByName(mappingEntryElements[0], FeedPage.class);
            if (mappedField != null) {
                columnMapping.put(mappedField, new String(mappingEntryElements[1]).getBytes());
            }
        }

    }

    @Override
    public void configure(ComponentConfiguration conf) {

    }

    private byte[] getHashedRowKey(String key) {
        return Utils.getMD5HashForString(key);
    }

}
