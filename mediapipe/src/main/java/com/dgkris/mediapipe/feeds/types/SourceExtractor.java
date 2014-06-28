package com.dgkris.mediapipe.feeds.types;

import com.dgkris.mediapipe.feeds.models.Feed;
import com.dgkris.mediapipe.feeds.models.FeedListItem;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Each type of source should comply by this interface to retrieve the list of feed items
 */
public interface SourceExtractor {

    public List<FeedListItem> getSourceFeeds();

    public DateTime getLastExtractedDateTimeForFeed(Feed feed);

    public void setLastExtractedDateTimeForFeed(Feed feed, String currentDateTime, String newDateTime);

}
