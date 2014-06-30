package com.dgkris.rsspipe.feeds.types;

import com.dgkris.rsspipe.feeds.models.Feed;
import com.dgkris.rsspipe.feeds.models.FeedSource;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Each type of source should comply by this interface to retrieve the list of feed items
 */
public interface FeedSourceReader {

    public List<FeedSource> getSourceFeeds();

    public DateTime getLastExtractedDateTimeForFeed(Feed feed);

    public void setLastExtractedDateTimeForFeed(Feed feed, String currentDateTime, String newDateTime);

}
