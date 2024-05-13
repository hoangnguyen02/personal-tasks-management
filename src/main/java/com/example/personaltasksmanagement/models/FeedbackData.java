package com.example.personaltasksmanagement.models;

public class FeedbackData {
    private String feedbackName;
    private String feedbackDetails;
    private int userID;

    public FeedbackData(int userID,  String feedbackDetails,  String feedbackName) {
        this.feedbackName = feedbackName;
        this.feedbackDetails = feedbackDetails;
        this.userID = userID;
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


    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    @Override
    public String toString() {
        return "FeedbackData{" +
                "feedbackName='" + feedbackName + '\'' +
                ", feedbackDetails='" + feedbackDetails + '\'' +
                ", userID=" + userID +
                '}';
    }
}
