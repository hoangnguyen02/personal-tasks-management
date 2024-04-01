package com.example.personaltasksmanagement.controllers;

import com.example.personaltasksmanagement.database.DBConfig;
import com.example.personaltasksmanagement.models.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class Setting implements Initializable {
    @FXML
    private PasswordField repeatNewPasswordField;

    @FXML
    private Button deleteAccount;

    @FXML
    private Button importAvatar;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField oldPasswordField;

    @FXML
    private Button submitProfile;
    @FXML
    private AnchorPane profile_form;

    @FXML
    private Button sentCode;
    @FXML
    private AnchorPane changePass_form;

    @FXML
    private Button backToProfile;
    @FXML
    private Button submitChangePass;

    public void changePass_action(){
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String repeatNewPassword = repeatNewPasswordField.getText();


        int loggedInUserId = UserSession.getInstance().getUserId();

        if (!newPassword.equals(repeatNewPassword)) {
            showAlert(Alert.AlertType.ERROR, "Change Password", "Error", "New passwords do not match.");
            return;
        }
        try (Connection connection = DriverManager.getConnection(DBConfig.JDBC_URL, DBConfig.JDBC_USER, DBConfig.JDBC_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT password FROM users WHERE user_id = ?")) {

            statement.setInt(1,  loggedInUserId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String currentPassword = resultSet.getString("password");
                    if (!currentPassword.equals(oldPassword)) {
                        showAlert(Alert.AlertType.ERROR, "Change Password", "Error", "Incorrect old password.");
                        return;
                    }

                    try (PreparedStatement updateStatement = connection.prepareStatement("UPDATE users SET password = ? WHERE user_id = ?")) {
                        updateStatement.setString(1, newPassword);
                        updateStatement.setInt(2, loggedInUserId);
                        int rowsAffected = updateStatement.executeUpdate();
                        if (rowsAffected > 0) {
                            showAlert(Alert.AlertType.INFORMATION, "Change Password", "Success", "Password changed successfully.");
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Change Password", "Error", "Failed to change password.");
                        }
                    }
                } else {

                    showAlert(Alert.AlertType.ERROR, "Change Password", "Error", "User not found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Change Password", "Error", "Database error occurred.");
        }
    }

    @FXML
    public void switchToChangPass(ActionEvent event) {
        profile_form.setVisible(false);
        changePass_form.setVisible(true);
    }
    @FXML
    public void switchToProfile(ActionEvent event) {
        profile_form.setVisible(true);
        changePass_form.setVisible(false);
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){

    }
    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}
