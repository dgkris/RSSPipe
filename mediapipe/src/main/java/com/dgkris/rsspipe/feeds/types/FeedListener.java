package com.dgkris.rsspipe.feeds.types;

import com.dgkris.rsspipe.feeds.models.FeedPage;

/**
 * Interface that a class has to comply with in order to receive feed updates
 */
public interface FeedListener {

    public void onNewPage(FeedPage page);

}
