package com.example.personaltasksmanagement.controllers;

import com.example.personaltasksmanagement.database.DBConnection;
import com.example.personaltasksmanagement.email.EmailSender;
import com.example.personaltasksmanagement.models.FeedbackData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class FeedbackAdmin implements Initializable {

    @FXML
    private ListView<FeedbackData> listFeedback;

    @FXML
    private TextArea AreaFeedback;

    @FXML
    private Button clearButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button saveButton;
    @FXML
    private Label username_feedback;


    Connection connect = DBConnection.connectionDB();

    @FXML
    public void clearAction() {
        AreaFeedback.clear();
    }

    @FXML
    public void delete_action(ActionEvent event) {
        System.out.println("clcik");
        FeedbackData selectedItem = listFeedback.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            String deleteQuery = "DELETE FROM feedback WHERE feedback_name = ?";
            try {
                PreparedStatement preparedStatement = connect.prepareStatement(deleteQuery);
                preparedStatement.setString(1, selectedItem.getFeedbackName());
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    listFeedback.getItems().remove(selectedItem);
                    AreaFeedback.clear();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Feedback deleted successfully");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete feedback");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void reponse_action(ActionEvent event) {
        FeedbackData selectedItem = listFeedback.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            String recipientEmail = getUserEmail(selectedItem.getUserID());
            if (recipientEmail != null) {
                String subject = "Thông báo về phản hồi của bạn";
                String content = "Xin chào,\n\nChúng tôi xin trân trọng thông báo rằng phản hồi của bạn đã được tiếp nhận và xử lý.\n\nTrân trọng,\nNhóm hỗ trợ";
                EmailSender.sendEmail(recipientEmail, subject, content);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Email sent successfully!");
            } else {
                showAlert(Alert.AlertType.WARNING, "Warning", "The email of the sender of the response could not be found");
            }
        }
    }
    private String getUserEmail(int userID) {
        String email = null;
        String selectQuery = "SELECT email FROM users WHERE user_id = ?";
        try {
            PreparedStatement preparedStatement = connect.prepareStatement(selectQuery);
            preparedStatement.setInt(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                email = resultSet.getString("email");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return email;
    }

    public ObservableList<FeedbackData> feedbackDataList() {
        ObservableList<FeedbackData> listDataFeedback = FXCollections.observableArrayList();
        String selectQuery = "SELECT user_id, feedback_name, feedback_details FROM feedback";
        try {
            PreparedStatement preparedStatement = connect.prepareStatement(selectQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int userID = resultSet.getInt("user_id");
                String feedbackName = resultSet.getString("feedback_name");
                String feedbackDetails = resultSet.getString("feedback_details");

                FeedbackData feedback = new FeedbackData(userID, feedbackDetails, feedbackName);
                listDataFeedback.add(feedback);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listDataFeedback;
    }

    public void populateListView(ObservableList<FeedbackData> listDataFeedback) {
        listFeedback.getItems().addAll(listDataFeedback);
    }

    public void setupListViewListener() {
        listFeedback.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                displayFeedbackContent(newValue.getFeedbackDetails());
                displayName(newValue.getUserID());
            }
        });
    }
    private String getUserFullName(int userID) {
        String fullName = "";
        String selectQuery = "SELECT full_name FROM users WHERE user_id = ?";
        try {
            PreparedStatement preparedStatement = connect.prepareStatement(selectQuery);
            preparedStatement.setInt(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                fullName = resultSet.getString("full_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fullName;
    }

    public void displayFeedbackContent(String content) {
        AreaFeedback.setText(content);
    }
    public void displayName(int userID) {
        String userName = getUserFullName(userID);
        username_feedback.setText(userName);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<FeedbackData> feedback = feedbackDataList();
        populateListView(feedback);
        setupListViewListener();

        listFeedback.setItems(feedback);
        listFeedback.setCellFactory(param -> new ListCell<FeedbackData>() {
            @Override
            protected void updateItem(FeedbackData item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getFeedbackName());
                }
            }
        });

    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
