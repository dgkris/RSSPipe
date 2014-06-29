package com.dgkris.mediapipe;

import com.dgkris.mediapipe.feeds.models.FeedPage;
import com.dgkris.mediapipe.utils.Utils;
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
    private byte[][] columnMapEntry;
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
        // Split the event body and get the values for the columns
        String eventStr = new String(currentEvent.getBody());

        FeedPage page = (FeedPage) gson.fromJson(eventStr, FeedPage.class);
        page.setTimestampOfStorage(String.valueOf(new DateTime().getMillis()));

        logger.info("Received " + page.toString());
        byte[] rowKey = null;
        try {
            String fieldValue = (String) rowKeyField.get(page);
            if (shouldHashRowKey) {
                rowKey = getHashedRowKey(fieldValue);
            } else {
                rowKey = fieldValue.getBytes();
            }
        } catch (IllegalAccessException e) {
            logger.info("Something went wrong in the serializer " + e.getMessage());
        }

        String[] cols = {
                page.getFeedItemTitle(), page.getFeedItemAuthor(),
                page.getFeedItemLink(), page.getFeedItemGuid(), page.getFeedItemPubDate(),
                page.getFeedItemDescription(), page.getBestGuessRelevantText(),
                page.getFullText(), page.getFeedCopyRight(), page.getFeedDesc(), page.getFeedLanguage(),
                page.getFeedLink(), page.getFeedTitle(), page.getFeedPubDate()
        };

        puts.clear();
        for (short i = 0; i < cols.length; i++) {
            PutRequest req = new PutRequest(hTable, rowKey, colFamily,
                    columnMapEntry[i], cols[i] != null ? cols[i].getBytes() : "".getBytes());
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
        columnMapEntry = null;
    }

    @Override
    public void configure(Context context) {
        Class feedPageClass = FeedPage.class;
        try {
            rowKeyField = feedPageClass.getField(context.getString(RSSPipeConstants.ROW_KEY_PARAM_NAME));
            shouldHashRowKey = context.getBoolean(RSSPipeConstants.SHOULD_HASH_ROW_KEY_PARAM_NAME);

            String columnMapping = new String(context.getString(RSSPipeConstants.COL_CONF_PARAM_NAME));
            String[] columnMappingUnits = columnMapping.split(RSSPipeConstants.COL_CONF_PARAM_DELIM);
            short i = 0;
            columnMapEntry = new byte[columnMappingUnits.length][];
            for (String name : columnMappingUnits) {
                logger.info("NAME :: " + name);
                name.split(":");
                if (name != null)
                    columnMapEntry[i++] = (new String(name)).getBytes();
            }
        } catch (NoSuchFieldException e) {
            logger.info("Error while fetching parameters for serializer");
        }
    }

    @Override
    public void configure(ComponentConfiguration conf) {

    }

    private byte[] getHashedRowKey(String key) {
        return Utils.getMD5HashForString(key);
    }


}
