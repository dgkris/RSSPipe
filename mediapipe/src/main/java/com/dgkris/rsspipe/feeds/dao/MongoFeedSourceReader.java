package com.dgkris.rsspipe.feeds.dao;

import com.dgkris.rsspipe.feeds.models.Feed;
import com.dgkris.rsspipe.feeds.models.FeedSource;
import com.dgkris.rsspipe.feeds.types.FeedSourceReader;
import com.dgkris.rsspipe.utils.Utils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Retrieves feeds list from mongo collection
 */
public class MongoFeedSourceReader implements FeedSourceReader {

    private static final Logger logger = LoggerFactory.getLogger(MongoFeedSourceReader.class);

    @Override
    public List<FeedSource> getSourceFeeds() {
        logger.info("Fetching feeds from Mongo::Started");
        ArrayList<FeedSource> feedItems = new ArrayList<FeedSource>();
        MongoService mongoService = new MongoService();
        mongoService.openConnection("localhost", 27017, "MediapipeDB");
        BasicDBObject basicDBObject = new BasicDBObject();
        List<DBObject> feedSources = mongoService.fetchDocumentsFromCollection("FeedSource", basicDBObject);
        for (DBObject feedSource : feedSources) {
            FeedSource feedListItem = new FeedSource();
            feedListItem.setPublisherName((String) feedSource.get("publisherName"));
            feedListItem.setFeedUrl((String) feedSource.get("url"));
            feedListItem.setCountry((String) feedSource.get("country"));
            feedListItem.setState((String) feedSource.get("state"));
            feedItems.add(feedListItem);
        }
        mongoService.closeConnection();
        logger.info("Fetching feeds from Mongo::Completed");
        return feedItems;
    }

    @Override
    public DateTime getLastExtractedDateTimeForFeed(Feed feed) {
        MongoService mongoService = new MongoService();
        mongoService.openConnection("localhost", 27017, "MediapipeDB");
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.put("publisherName", feed.getFeedSource().getPublisherName());
        DBObject extractionStatus = mongoService.fetchDocumentsFromCollection("ExtractionStatus", basicDBObject).get(0);
        DateTime dateTime = Utils.convertToDateTime((String) extractionStatus.get("lastExtractedTs"));
        mongoService.closeConnection();
        logger.info("Last extraction date for feed::{} => {}", feed.getFeedSource().getPublisherName(), dateTime.toString());
        return dateTime;
    }

    @Override
    public void setLastExtractedDateTimeForFeed(Feed feed, String currentDateTime, String newDateTime) {
        MongoService mongoService = new MongoService();
        mongoService.openConnection("localhost", 27017, "MediapipeDB");


        BasicDBObject queryObject = new BasicDBObject();
        queryObject.put("publisherName", feed.getFeedSource().getPublisherName());

        BasicDBObject newDBObject = new BasicDBObject();
        newDBObject.put("publisherName", feed.getFeedSource().getPublisherName());
        newDBObject.put("lastExtractedTs", newDateTime);

        mongoService.replaceDocumentInCollection("ExtractionStatus", queryObject, newDBObject);
        logger.info("Updated last extraction date for feed::{} to {}", feed.getFeedSource().getPublisherName(), newDateTime.toString());
    }

}