package com.example.personaltasksmanagement.controllers;

import com.example.personaltasksmanagement.database.DBConfig;
import com.example.personaltasksmanagement.email.EmailSender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.*;
import java.util.Random;

public class ForgetPassword {
    @FXML
    private Button submitButton;
    @FXML
    private Button sendCodeButton;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField codeTextField;
    private String registeredEmail;
    private String generatedOTP;


    public void handleSubmitButton(ActionEvent event) {

        String enteredCode = codeTextField.getText();
        if (enteredCode.equals(generatedOTP)) {
            try {
                Connection conn = DriverManager.getConnection(DBConfig.JDBC_URL, DBConfig.JDBC_USER, DBConfig.JDBC_PASSWORD);
                PreparedStatement statement = conn.prepareStatement("UPDATE users SET password = ? WHERE email = ?");
                statement.setString(1, "1");
                statement.setString(2, registeredEmail);
                statement.executeUpdate();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Successfully");
            alert.setContentText("Your password is 1. Please login and change your password");
            alert.showAndWait();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        } else {
            System.out.println("Invalid OTP!");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erorr!");
            alert.setContentText("Invalid OTP! Please try again!");
            alert.showAndWait();
        }
    }
    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
    private String getEmailFromDatabase(String email) {
        String dbEmail = null;
        try {
            Connection conn = DriverManager.getConnection(DBConfig.JDBC_URL, DBConfig.JDBC_USER, DBConfig.JDBC_PASSWORD);
            PreparedStatement statement = conn.prepareStatement("SELECT email FROM users WHERE email = ?");
            statement.setString(1, email);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                dbEmail = result.getString("email");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dbEmail;
    }


    public void handleSendCodeButton(ActionEvent event) {
        String email = emailTextField.getText();
        registeredEmail = getEmailFromDatabase(email);
        if (registeredEmail != null) {
            generatedOTP = generateOTP();
            String recipient = registeredEmail;
            String subject = "Your OTP Code";
            String content = "Your OTP code is: " + generatedOTP + "\n Use this code to reset your application user password";
            EmailSender.sendEmail(recipient, subject, content);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("OTP Code Sent");
            alert.setHeaderText(null);
            alert.setContentText("An OTP code has been sent to your email: " + registeredEmail);
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erorr!");
            alert.setContentText("Email not found in database!");
            alert.showAndWait();
        }
    }
}
