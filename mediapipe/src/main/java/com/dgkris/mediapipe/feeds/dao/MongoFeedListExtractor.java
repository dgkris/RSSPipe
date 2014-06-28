package com.dgkris.mediapipe.feeds.dao;

import com.dgkris.mediapipe.feeds.models.Feed;
import com.dgkris.mediapipe.feeds.models.FeedListItem;
import com.dgkris.mediapipe.feeds.types.SourceExtractor;
import com.dgkris.mediapipe.utils.Utils;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Retrieves feeds list from mongo collection
 */
public class MongoFeedListExtractor implements SourceExtractor {

    private static final Logger logger = LoggerFactory.getLogger(MongoFeedListExtractor.class);

    @Override
    public List<FeedListItem> getSourceFeeds() {
        logger.info("Fetching feeds from Mongo::Started");
        ArrayList<FeedListItem> feedItems = new ArrayList<FeedListItem>();
        MongoService mongoService = new MongoService();
        mongoService.openConnection("localhost", 27017, "MediapipeDB");
        BasicDBObject basicDBObject = new BasicDBObject();
        List<DBObject> feedSources = mongoService.fetchDocumentsFromCollection("FeedSource", basicDBObject);
        for (DBObject feedSource : feedSources) {
            FeedListItem feedListItem = new FeedListItem();
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
        basicDBObject.put("publisherName", feed.getFeedListItem().getPublisherName());
        DBObject extractionStatus = mongoService.fetchDocumentsFromCollection("ExtractionStatus", basicDBObject).get(0);
        DateTime dateTime = Utils.convertToDateTime((String) extractionStatus.get("lastExtractedTs"));
        mongoService.closeConnection();
        logger.info("Last extraction date for feed::{} => {}", feed.getFeedListItem().getPublisherName(), dateTime.toString());
        return dateTime;
    }

    @Override
    public void setLastExtractedDateTimeForFeed(Feed feed, String currentDateTime, String newDateTime) {
        MongoService mongoService = new MongoService();
        mongoService.openConnection("localhost", 27017, "MediapipeDB");


        BasicDBObject queryObject = new BasicDBObject();
        queryObject.put("publisherName", feed.getFeedListItem().getPublisherName());

        BasicDBObject newDBObject = new BasicDBObject();
        newDBObject.put("publisherName", feed.getFeedListItem().getPublisherName());
        newDBObject.put("lastExtractedTs", newDateTime);

        mongoService.replaceDocumentInCollection("ExtractionStatus", queryObject, newDBObject);
        logger.info("Updated last extraction date for feed::{} to {}", feed.getFeedListItem().getPublisherName(), newDateTime.toString());
    }

}