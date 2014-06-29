package com.dgkris.mediapipe.feeds;

import com.dgkris.mediapipe.feeds.models.Feed;
import com.dgkris.mediapipe.feeds.models.FeedItem;
import com.dgkris.mediapipe.feeds.models.FeedSource;
import com.dgkris.mediapipe.feeds.models.FeedPage;
import com.dgkris.mediapipe.feeds.parser.HTMLParser;
import com.dgkris.mediapipe.feeds.parser.RSSFeedParser;
import com.dgkris.mediapipe.feeds.types.FeedListener;
import com.dgkris.mediapipe.feeds.types.FeedSourceReader;
import com.dgkris.mediapipe.utils.Utils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * Extracts all the feeditems given a list of rss feeds
 */
public class FeedExtractor extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(FeedExtractor.class);
    private List<FeedSource> feedSources;
    private FeedSourceReader feedSourceReader;
    private ArrayList<FeedListener> listeners = new ArrayList<FeedListener>();
    private boolean keepRunning = false;
    private boolean threadEnded = false;
    private int crawlingFrequency = 20000;


    public FeedExtractor(FeedSourceReader feedSourceReader, int crawlingFrequency) {
        this.feedSourceReader = feedSourceReader;
        this.crawlingFrequency = crawlingFrequency;
        initialize();
    }

    public void registerListener(FeedListener listener) {
        listeners.add(listener);
    }

    private void initialize() {
        feedSources = feedSourceReader.getSourceFeeds();
    }

    @Override
    public void run() {
        keepRunning = true;
        try {
            while (keepRunning) {
                extract();
                Thread.sleep(crawlingFrequency);
            }
        } catch (Exception ex) {
            threadEnded = true;
            ex.printStackTrace();
        }
        keepRunning = false;
        threadEnded = true;
    }

    private void extract() {
        logger.info("Feed extraction started");
        RSSFeedParser rssFeedParser = new RSSFeedParser();
        HTMLParser htmlParser = new HTMLParser();
        DateTime latestPubDateForFeed = null;
        DateTime pubDate = null;
        DateTime lastExtractedDateTime = null;
        for (FeedSource feedSource : feedSources) {
            Feed feed = rssFeedParser.getFeedsFromUrl(feedSource);
            lastExtractedDateTime = feedSourceReader.getLastExtractedDateTimeForFeed(feed);
            for (FeedItem feedItem : feed.getFeedItems()) {
                pubDate = Utils.convertToDateTime(feedItem.getPubDate());
                if (pubDate.isAfter(lastExtractedDateTime)) {
                    latestPubDateForFeed = pubDate;
                    notifyAllListeners(htmlParser.getFeedPageFromUrl(feedItem.getLink(), feedItem));
                    if (lastExtractedDateTime.isAfter(latestPubDateForFeed)) {
                        latestPubDateForFeed = lastExtractedDateTime;
                    }
                }
            }
            if (latestPubDateForFeed != null) {
                feedSourceReader.setLastExtractedDateTimeForFeed(feed,
                        lastExtractedDateTime.toString(), latestPubDateForFeed.toString());
            }
        }
        logger.info("Feed extraction completed");
    }

    private void notifyAllListeners(FeedPage page) {
        logger.info("New feed page received :: {}", page.getFeedItemLink());
        for (FeedListener listener : listeners) {
            listener.onNewPage(page);
        }
    }

    public void startThread() {
        logger.info("Started feed extractor thread");
        this.run();
    }

    public void shutdownThread() {
        logger.info("Shutdown feed extractor thread");
        listeners.clear();
        if (keepRunning == false) {
            return;
        }
        threadEnded = false;
        keepRunning = false;
        while (!threadEnded) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
    }
}
