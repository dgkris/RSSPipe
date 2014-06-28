package com.dgkris.mediapipe.feeds.parser;

import com.dgkris.mediapipe.feeds.models.Feed;
import com.dgkris.mediapipe.feeds.models.FeedItem;
import com.dgkris.mediapipe.feeds.models.FeedListItem;
import com.dgkris.mediapipe.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class RSSFeedParser {

    static final String TITLE = "title";
    static final String DESCRIPTION = "description";
    static final String CHANNEL = "channel";
    static final String LANGUAGE = "language";
    static final String COPYRIGHT = "copyright";
    static final String LINK = "link";
    static final String AUTHOR = "author";
    static final String ITEM = "item";
    static final String PUB_DATE = "pubDate";
    static final String DC_DATE = "dc:date";
    static final String GUID = "guid";

    private static final Logger logger = LoggerFactory.getLogger(RSSFeedParser.class);


    public RSSFeedParser() {

    }

    public Feed getFeedsFromUrl(FeedListItem feedListItem) {
        logger.info("Extracting rss feed items from feed url : {} initiated", feedListItem.getFeedUrl());
        String feedUrl = feedListItem.getFeedUrl();
        Feed feed = null;
        try {
            URL url = new URL(feedUrl);

            boolean isFeedHeader = true;
            // Set header values intial to the empty string
            String description = "";
            String title = "";
            String link = "";
            String language = "";
            String copyright = "";
            String author = "";
            String pubdate = "";
            String guid = "";

            // First create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            // Setup a new eventReader
            XMLEventReader eventReader = inputFactory.createXMLEventReader(url
                    .openStream());
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.isStartElement()) {
                    String localPart = event.asStartElement().getName()
                            .getLocalPart();
                    if (localPart.equals(ITEM)) {
                        if (isFeedHeader) {
                            isFeedHeader = false;

                            feed = new Feed(title, link, description, language,
                                    copyright, pubdate);

                            feed.setFeedListItem(feedListItem);
                        }
                        event = eventReader.nextEvent();
                    } else if (localPart.equals(TITLE)) {
                        title = getCharacterData(event, eventReader);
                    } else if (localPart.equals(DESCRIPTION)) {
                        description = getCharacterData(event, eventReader);
                    } else if (localPart.equals(LINK)) {
                        link = getCharacterData(event, eventReader);
                    } else if (localPart.equals(GUID)) {
                        guid = getCharacterData(event, eventReader);
                    } else if (localPart.equals(LANGUAGE)) {
                        language = getCharacterData(event, eventReader);
                    } else if (localPart.equals(AUTHOR)) {
                        author = getCharacterData(event, eventReader);
                    } else if (localPart.equals(PUB_DATE)) {
                        pubdate = getCharacterData(event, eventReader);
                    } else if (localPart.equals(DC_DATE)) {
                        pubdate = getCharacterData(event, eventReader);
                    } else if (localPart.equals(COPYRIGHT)) {
                        copyright = getCharacterData(event, eventReader);
                    }
                } else if (event.isEndElement()) {
                    if (event.asEndElement().getName().getLocalPart() == (ITEM)) {

                        FeedItem feedItem = new FeedItem();
                        feedItem.setAuthor(author);
                        feedItem.setDescription(description);
                        feedItem.setGuid(guid);
                        feedItem.setLink(link);
                        feedItem.setTitle(title);
                        feedItem.setPubDate(pubdate);

                        feedItem.setParentFeed(feed);
                        feed.getFeedItems().add(feedItem);

                        event = eventReader.nextEvent();
                        continue;
                    }
                }
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logger.info("Extracting rss feed items from feed url : {} completed", feedListItem.getFeedUrl());

        return feed;
    }

    private String getCharacterData(XMLEvent event, XMLEventReader eventReader)
            throws XMLStreamException {
        String result = "";
        event = eventReader.nextEvent();
        if (event instanceof Characters) {
            result = event.asCharacters().getData();
        }
        if (result.contains("CDATA"))
            result = Utils.stripCDATA(result);
        return result;
    }

}
