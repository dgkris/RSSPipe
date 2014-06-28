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




