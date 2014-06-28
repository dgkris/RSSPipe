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

import java.util.ArrayList;
import java.util.List;

/**
 * Serializes the news feed items to write to HBase schema
 */
public class FeedSerializer implements AsyncHbaseEventSerializer {

    private static final Logger logger = LoggerFactory.getLogger(FeedSerializer.class);
    private final List<PutRequest> puts = new ArrayList<PutRequest>();
    private final List<AtomicIncrementRequest> incs = new ArrayList<AtomicIncrementRequest>();
    private final byte[] eventCountCol = "eventCount".getBytes();
    private Gson gson = new Gson();
    private byte[] hTable;
    private byte[] colFamily;
    private Event currentEvent;
    private byte[][] colNames;

    @Override
    public void initialize(byte[] table, byte[] cf) {
        logger.info("FeedSink initiated");
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
        logger.info("Received " + page.toString());

        byte[] rowKey = getHashedRowKey(page);
        String[] cols = {
                page.getFeedItemTitle(), page.getFeedItemAuthor(),
                page.getFeedItemLink(), page.getFeedItemGuid(),page.getFeedItemPubDate(),
                page.getFeedItemDescription(), page.getBestGuessRelevantText(),
                page.getFullText(), page.getFeedCopyRight(), page.getFeedDesc(), page.getFeedLanguage(),
                page.getFeedLink(), page.getFeedTitle(), page.getFeedPubDate()
        };

        puts.clear();
        for (short i = 0; i < cols.length; i++) {
            PutRequest req = new PutRequest(hTable, rowKey, colFamily,
                    colNames[i], cols[i] != null ? cols[i].getBytes() : "".getBytes());
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
        logger.info("Cleanup of FeedSink");
        hTable = null;
        colFamily = null;
        currentEvent = null;
        colNames = null;
    }

    @Override
    public void configure(Context context) {
        String cols = new String(context.getString(MediaPipeConstants.COL_CONF_PARAM_NAME));
        String[] columnNames = cols.split(MediaPipeConstants.COL_CONF_PARAM_DELIM);
        short i = 0;
        colNames = new byte[columnNames.length][];
        for (String name : columnNames) {
            logger.info("NAME :: " + name);
            if (name != null)
                colNames[i++] = (new String(name)).getBytes();
        }
    }

    @Override
    public void configure(ComponentConfiguration conf) {

    }

    private byte[] getHashedRowKey(FeedPage page) {
        page.setTimestampOfStorage(Utils.getMD5HashForString(String.valueOf(new DateTime().getMillis())).toString());
        return page.getTimestampOfStorage().getBytes();
    }

}
