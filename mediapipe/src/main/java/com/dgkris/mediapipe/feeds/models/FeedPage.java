package com.dgkris.mediapipe.feeds.models;

/**
 * Represents a single html page that appeared on the feed
 */
public class FeedPage {

    //Meta data
    private String timestampOfStorage;

    //Feed Representations
    private String feedTitle;
    private String feedLink;
    private String feedDesc;
    private String feedLanguage;
    private String feedCopyRight;
    private String feedPubDate;


    //FeedItem representations
    private String feedItemTitle;
    private String feedItemDescription;
    private String feedItemLink;
    private String feedItemAuthor;
    private String feedItemGuid;
    private String feedItemPubDate;

    private String fullText;
    private String decodedPubTime;
    private String bestGuessRelevantText;

    public String getFeedTitle() {
        return feedTitle;
    }

    public void setFeedTitle(String feedTitle) {
        this.feedTitle = feedTitle;
    }

    public String getFeedLink() {
        return feedLink;
    }

    public void setFeedLink(String feedLink) {
        this.feedLink = feedLink;
    }

    public String getFeedDesc() {
        return feedDesc;
    }

    public void setFeedDesc(String feedDesc) {
        this.feedDesc = feedDesc;
    }

    public String getFeedLanguage() {
        return feedLanguage;
    }

    public void setFeedLanguage(String feedLanguage) {
        this.feedLanguage = feedLanguage;
    }

    public String getFeedCopyRight() {
        return feedCopyRight;
    }

    public void setFeedCopyRight(String feedCopyRight) {
        this.feedCopyRight = feedCopyRight;
    }

    public String getFeedPubDate() {
        return feedPubDate;
    }

    public void setFeedPubDate(String feedPubDate) {
        this.feedPubDate = feedPubDate;
    }

    public String getFeedItemTitle() {
        return feedItemTitle;
    }

    public void setFeedItemTitle(String feedItemTitle) {
        this.feedItemTitle = feedItemTitle;
    }

    public String getFeedItemDescription() {
        return feedItemDescription;
    }

    public void setFeedItemDescription(String feedItemDescription) {
        this.feedItemDescription = feedItemDescription;
    }

    public String getFeedItemLink() {
        return feedItemLink;
    }

    public void setFeedItemLink(String feedItemLink) {
        this.feedItemLink = feedItemLink;
    }

    public String getFeedItemAuthor() {
        return feedItemAuthor;
    }

    public void setFeedItemAuthor(String feedItemAuthor) {
        this.feedItemAuthor = feedItemAuthor;
    }

    public String getFeedItemGuid() {
        return feedItemGuid;
    }

    public void setFeedItemGuid(String feedItemGuid) {
        this.feedItemGuid = feedItemGuid;
    }

    public String getFeedItemPubDate() {
        return feedItemPubDate;
    }

    public void setFeedItemPubDate(String feedItemPubDate) {
        this.feedItemPubDate = feedItemPubDate;
    }

    public String getBestGuessRelevantText() {
        return bestGuessRelevantText;
    }

    public void setBestGuessRelevantText(String bestGuessRelevantText) {
        this.bestGuessRelevantText = bestGuessRelevantText;
    }

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public String getDecodedPubTime() {
        return decodedPubTime;
    }

    public void setDecodedPubTime(String decodedPubTime) {
        this.decodedPubTime = decodedPubTime;
    }

    public String getTimestampOfStorage() {
        return timestampOfStorage;
    }

    public void setTimestampOfStorage(String timestampOfStorage) {
        this.timestampOfStorage = timestampOfStorage;
    }

}
