RSS Pipe
=========
An RSS feed importer using Apache Flume that imports RSS feed elements filtered by date into HBase. Tried and tested on CDH 4.6. 


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

RSSPipe.sinks.HBASE.serializer.columns stores all the mapping between components of the rss feed and the hbase column name. You can choose to leave out any of the mapping entry if you do not want it to be stored onto the HTable. Also, if you want to change the name of the column, you may change the same in the mapping. The general represenation of the mapping is 
rss_feed_component>:hbase_column_name.

Below is the list of all available info extracted from a feed item :
```
timestampOfStorage : Time when the feed was extracted by flume
feedTitle : Title of the parent RSS feed
feedLink : Link of the parent RSS feed
feedDesc : Description of the parent RSS feed
feedLanguage : Language of the parent RSS feed
feedCopyRight : Copyright of the parent RSS feed
feedPubDate : Published date of the parent RSS feed
feedItemTitle : Title of the feed item
feedItemDescription : Description of the feed item feedItemLink:Link to the feed item
feedItemAuthor : Author of the feed item
feedItemGuid : GUID of the feed item
feedItemPubDate : Published date of the feed item
fullText : Full HTML of the feed item
decodedPubTime : A decoded representation of pubdate of the feed item
bestGuessRelevantText:Text info that is held by the div element with the highest density. Extracted using Readability algorithm. This is supposed to hold the most relevant piece of text within the page.
```
