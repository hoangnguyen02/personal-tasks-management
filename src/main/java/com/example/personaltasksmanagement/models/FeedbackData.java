package com.example.personaltasksmanagement.models;



public class FeedbackData {
    private String feedbackName;
    private String feedbackDetails;
    private String senderName;

    public FeedbackData(String feedbackName, String feedbackDetails, String senderName) {
        this.feedbackName = feedbackName;
        this.feedbackDetails = feedbackDetails;
        this.senderName = senderName;
    }

    public String getFeedbackName() {
        return feedbackName;
    }

    public void setFeedbackName(String feedbackName) {
        this.feedbackName = feedbackName;
    }

    public String getFeedbackDetails() {
        return feedbackDetails;
    }

    public void setFeedbackDetails(String feedbackDetails) {
        this.feedbackDetails = feedbackDetails;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}
