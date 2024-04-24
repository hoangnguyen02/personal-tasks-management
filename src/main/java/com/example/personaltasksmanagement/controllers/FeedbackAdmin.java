package com.example.personaltasksmanagement.controllers;

import com.example.personaltasksmanagement.database.DBConnection;
import com.example.personaltasksmanagement.models.FeedbackData;
import com.example.personaltasksmanagement.models.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class FeedbackAdmin implements Initializable {
    @FXML
    private TextArea AreaFeedback;

    @FXML
    private ListView<FeedbackData> listFeedback;

    @FXML
    private Label username_feedback;
    Connection connect = DBConnection.connectionDB();

    public ObservableList<FeedbackData> FeedbackDataList() {
        ObservableList<FeedbackData> feedbackList = FXCollections.observableArrayList();
        String selectQuery = "SELECT feedback_name, feedback_details, sender_name FROM feedback";
        try {
            PreparedStatement preparedStatement = connect.prepareStatement(selectQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String feedbackName = resultSet.getString("feedback_name");
                String feedbackDetails = resultSet.getString("feedback_details");
                String senderName = resultSet.getString("sender_name");
                FeedbackData feedback = new FeedbackData(feedbackName, feedbackDetails, senderName);
                feedbackList.add(feedback);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return feedbackList;
    }


    private void populateFeedbackListView() {
        ObservableList<FeedbackData> feedbackList = FeedbackDataList();
        listFeedback.setItems(feedbackList);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateFeedbackListView();
    }



}
