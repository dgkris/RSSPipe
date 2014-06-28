MediaPipe (Beta)
=========

An RSS feed importer using apache flume. Tried and tested on CDH 4.6. 


Source Management:

Create MongoDB DB : MediapipeDB
create the following collections and add entries as below :

db.FeedSource.save({"country" : "India" , "publisherName" : "The Hindu" , "state" : "All India" , "url" : "http://www.thehindu.com/?service=rss"});

db.ExtractionStatus.save({"publisherName" : "The Hindu", "lastExtractedTs" : <DATETIME IN EEE MMM dd HH:mm:ss zzz yyyy> });



Sink :

Writes to HBase table FeedTable into CF FeedDetailCF


Flume configuration :
MediaPipe.sources = Feeds
MediaPipe.channels = MemChannel
MediaPipe.sinks = HBASE
MediaPipe.sources.Feeds.type = com.dgkris.mediapipe.FeedSource
MediaPipe.sources.Feeds.channels = MemChannel
MediaPipe.sources.Feeds.crawlingFrequency = 30000
MediaPipe.sinks.HBASE.channel = MemChannel
MediaPipe.sinks.HBASE.type = org.apache.flume.sink.hbase.AsyncHBaseSink
MediaPipe.sinks.HBASE.serializer = com.dgkris.mediapipe.FeedSerializer
MediaPipe.sinks.HBASE.table = FeedTable
MediaPipe.sinks.HBASE.rowKey = pubTimeRk
MediaPipe.sinks.HBASE.columnFamily = FeedDetailCF
MediaPipe.sinks.HBASE.serializer.columns = itemTitle,itemAuthor,itemLink,itemGuid,itemPubDate,itemDescription,itemText,itemHtml,feedCopyright,feedDescription,feedLanguage,feedLink,feedTitle,feedPubDate
MediaPipe.channels.MemChannel.type = memory
MediaPipe.channels.MemChannel.capacity = 2000
MediaPipe.channels.MemChannel.transactionCapacity = 200




