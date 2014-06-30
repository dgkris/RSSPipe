package com.dgkris.rsspipe.feeds.dao;

import com.mongodb.*;
import com.mongodb.util.JSON;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class to insert, update, fetch documents from/to MongoDB
 */

public class MongoService {

    private static final Logger logger = LoggerFactory.getLogger(MongoService.class);
    private MongoClient mongoClient;
    private DB db;


    public MongoService() {

    }

    public void openConnection(String host, int port, String dbName) {
        logger.info("Connection to MongoDB open");
        try {
            mongoClient = new MongoClient(Arrays.asList(new ServerAddress(
                    host, port)));
            db = mongoClient.getDB(dbName);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        logger.info("Connection to MongoDB closed");
        try {
            db = null;
            mongoClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void finalize() {
        closeConnection();
    }


    public ObjectId insertDocumentToCollection(String collectionName,
                                               String jsonDocument) {
        ObjectId id = null;
        DBCollection collection = db.getCollection(collectionName);
        if (collection != null) {
            BasicDBObject item = (BasicDBObject) JSON.parse(jsonDocument);
            collection.insert(item);
            id = item.getObjectId("_id");
        }
        return id;
    }

    public void replaceDocumentInCollection(String collectionName, DBObject queryObject, DBObject dbObject) {
        DBCollection collection = db.getCollection(collectionName);
        if (collection != null) {
            collection.update(queryObject, dbObject);
        }
    }

    public DBObject fetchDocumentFromCollection(String collectionName, BasicDBObject queryObject) {
        DBCollection collection = db.getCollection(collectionName);
        DBCursor cursor = collection.find(queryObject);
        try {
            if (cursor.hasNext()) {
                return cursor.next();
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    public List<DBObject> fetchDocumentsFromCollection(String collectionName,
                                                       BasicDBObject queryObject) {
        ArrayList<DBObject> documentList = new ArrayList<DBObject>();
        DBCollection collection = db.getCollection(collectionName);
        if (collection != null) {

            DBCursor cursor = collection.find(queryObject);
            try {
                while (cursor.hasNext()) {
                    documentList.add(cursor.next());
                }
            } finally {
                cursor.close();
            }
        }
        return documentList;
    }

}
