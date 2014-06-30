package com.dgkris.mediatype.feeds;

import com.dgkris.rsspipe.feeds.FeedExtractor;
import com.dgkris.rsspipe.feeds.dao.MongoFeedSourceReader;

/**
 * A basic test for rss feed based module
 */
public class FeedTest {

    public static void main(String args[]) {
        MongoFeedSourceReader feedListExtractor=new MongoFeedSourceReader();
        FeedExtractor feedExtractor=new FeedExtractor(feedListExtractor, 30000);
        feedExtractor.startThread();
    }

}
