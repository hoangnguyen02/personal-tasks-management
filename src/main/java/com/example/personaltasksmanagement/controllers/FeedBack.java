package com.example.personaltasksmanagement.controllers;

import com.example.personaltasksmanagement.database.DBConnection;
import com.example.personaltasksmanagement.models.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class FeedBack implements Initializable {
    @FXML
    private Button cancelButton;

    @FXML
    private TextArea feedBack_details;

    @FXML
    private TextField feedBack_name;

    @FXML
    private Button sendButton;

    Connection connect = DBConnection.connectionDB();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void sendFeedback_action(ActionEvent event) {
        String feedbackName = feedBack_name.getText();
        String feedbackDetails = feedBack_details.getText();

        if (!feedbackName.isEmpty() && !feedbackDetails.isEmpty()) {
            saveFeedback(feedbackName, feedbackDetails);


            feedBack_name.clear();
            feedBack_details.clear();

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setHeaderText(null);
            successAlert.setContentText("Phản hồi đã được gửi và lưu trữ thành công!");
            successAlert.showAndWait();
            ((Node) (event.getSource())).getScene().getWindow().hide();
        } else {
            Alert warningAlert = new Alert(Alert.AlertType.WARNING);
            warningAlert.setHeaderText(null);
            warningAlert.setContentText("Vui lòng điền đầy đủ thông tin phản hồi!");
            warningAlert.showAndWait();
        }
    }

    private String getUsername(int userId) {
        String userName = null;
        try {
            String query = "SELECT username FROM users WHERE user_id = ?";
            PreparedStatement statement = connect.prepareStatement(query);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                userName = resultSet.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userName;
    }

    private void saveFeedback(String feedbackName, String feedbackDetails) {
        try {
            // Lấy ID người dùng hiện tại
            int userId = UserSession.getInstance().getUserId();

            String senderName = getUsername(userId);

            if (senderName == null) {
                senderName = getUsername(1);
            }
            String insertQuery = "INSERT INTO feedback (feedback_name, feedback_details, user_id) VALUES (?, ?, ?)";
            PreparedStatement insertStatement = connect.prepareStatement(insertQuery);
            insertStatement.setString(1, feedbackName);
            insertStatement.setString(2, feedbackDetails);
            insertStatement.setInt(3, userId);
            insertStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void cancel_action(ActionEvent event){
        ((Node) event.getSource()).getScene().getWindow().hide();
    }
}
