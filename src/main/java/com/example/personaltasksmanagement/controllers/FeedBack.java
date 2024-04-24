package com.example.personaltasksmanagement.controllers;

import com.example.personaltasksmanagement.database.DBConnection;
import com.example.personaltasksmanagement.models.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
        // Thiết lập hành động cho nút "send"
        sendButton.setOnAction(event -> {
            // Lấy tên người gửi từ thông tin người dùng hiện tại
            String senderName = UserSession.getInstance().getUsername();
            // Lấy thông tin phản hồi từ các trường nhập liệu
            String feedbackName = feedBack_name.getText();
            String feedbackDetails = feedBack_details.getText();

            // Gọi phương thức để lưu phản hồi vào cơ sở dữ liệu
            saveFeedbackToDatabase(senderName, feedbackName, feedbackDetails);
        });
    }

    // Phương thức để lưu phản hồi vào cơ sở dữ liệu
    private void saveFeedbackToDatabase(String senderName, String feedbackName, String feedbackDetails) {
        String insertQuery = "INSERT INTO feedback (sender_name, feedback_name, feedback_details) VALUES (?, ?, ?)";
        try {
            PreparedStatement preparedStatement = connect.prepareStatement(insertQuery);
            preparedStatement.setString(1, senderName);
            preparedStatement.setString(2, feedbackName);
            preparedStatement.setString(3, feedbackDetails);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Feedback saved to database.");
                // Gửi thông báo hoặc thực hiện các hành động khác ở đây nếu cần
            } else {
                System.out.println("Failed to save feedback to database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
