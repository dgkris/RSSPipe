package com.dgkris.mediatype.feeds;

import com.dgkris.mediapipe.feeds.FeedExtractor;
import com.dgkris.mediapipe.feeds.dao.MongoFeedListExtractor;
import org.joda.time.DateTime;

/**
 * A basic test for rss feed based module
 */
public class FeedTest {

    public static void main(String args[]) {
        MongoFeedListExtractor feedListExtractor=new MongoFeedListExtractor();
        FeedExtractor feedExtractor=new FeedExtractor(feedListExtractor, 30000);
        feedExtractor.startThread();
    }

}
