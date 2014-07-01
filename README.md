MediaPipe (Beta)
=========
An RSS feed importer using apache flume. Tried and tested on CDH 4.6. 


**Adding new sources**

As of now, the source is kind of hardcoded to a local mongodb instance with hardcoded connection parameters. I'm working on making this dynamic.

1. Create MongoDB DB by name MediapipeDB
2. Create collections FeedSource and ExtractionStatus 

```
db.FeedSource.save({"country" : "India" , "publisherName" : "The Hindu" , "state" : "All India" , "url" : "http://www.thehindu.com/?service=rss"});
db.ExtractionStatus.save({"publisherName" : "The Hindu", "lastExtractedTs" : <DATETIME IN EEE MMM dd HH:mm:ss zzz yyyy> });
```

**Flume Configuration**

Flume configuration is available in conf/flume.conf file

RSSPipe.sinks.HBASE.serializer.columns contains all the available <rss_feed_component>:<hbase_column_name> mapping. You can modify the entries to your case.
