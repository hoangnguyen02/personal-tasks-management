package com.example.personaltasksmanagement.controllers;

import com.example.personaltasksmanagement.database.DBConfig;
import com.example.personaltasksmanagement.database.DBConnection;
import com.example.personaltasksmanagement.models.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class Setting implements Initializable {
    @FXML
    private Button backToProfile;

    @FXML
    private Button changePass;

    @FXML
    private AnchorPane changePass_form;

    @FXML
    private Label createdLabel;

    @FXML
    private Button deleteAccount;

    @FXML
    private TextField emailTextField;

    @FXML
    private Button importAvatar;

    @FXML
    private TextField mobilePhoneTextField;

    @FXML
    private Label nameLabel;

    @FXML
    private TextField nameTextField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField oldPasswordField;

    @FXML
    private PasswordField passswordField;

    @FXML
    private AnchorPane profile_form;

    @FXML
    private PasswordField repeatNewPasswordField;

    @FXML
    private Button submitChangePass;

    @FXML
    private Button submitProfile;

    @FXML
    private Label userNameLabel;

    @FXML
    private TextField userNameTextField;

    @FXML
    private ImageView user_imageView;

    public void importAvt() {
        FileChooser open = new FileChooser();
        open.getExtensionFilters().add(new FileChooser.ExtensionFilter("Open Image", "*jpg", "*png", "*jpeg"));
        File file = open.showOpenDialog(importAvatar.getScene().getWindow());

        if (file != null) {
            String relativePath = file.toURI().toString();
            UserSession.getInstance().setAvatarPath(relativePath);

            try (Connection connection = DriverManager.getConnection(DBConfig.JDBC_URL, DBConfig.JDBC_USER, DBConfig.JDBC_PASSWORD);
                 PreparedStatement statement = connection.prepareStatement("UPDATE users SET avatar_path = ? WHERE user_id = ?")) {

                statement.setString(1, relativePath);
                statement.setInt(2, UserSession.getInstance().getUserId());
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Import Avatar", "Success", "Avatar updated successfully.");
                    UserSession.getInstance().setAvatarPath(relativePath);
                    // Load lại dữ liệu người dùng để hiển thị thông tin cập nhật
                    loadUserData();
                } else {

                    showAlert(Alert.AlertType.ERROR, "Import Avatar", "Error", "Failed to update avatar.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Import Avatar", "Error", "Database error occurred.");
            }
        }
    }
    private void loadUserData() {
        try {
            int userId = UserSession.getInstance().getUserId();
            String selectQuery = "SELECT * FROM users WHERE user_id = ?";

            Connection connect = DBConnection.connectionDB();
            PreparedStatement preparedStatement = connect.prepareStatement(selectQuery);
            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                nameLabel.setText(resultSet.getString("full_name"));
                userNameLabel.setText(resultSet.getString("username"));
                createdLabel.setText(resultSet.getDate("date").toString());
                // Lấy đường dẫn avatar từ cơ sở dữ liệu
                String avatarPath = resultSet.getString("avatar_path");
                if (avatarPath != null && !avatarPath.isEmpty()) {
                    // Tạo đối tượng Image từ đường dẫn avatar
                    Image image = new Image(avatarPath);
                    user_imageView.setImage(image);
                } else {
                    // Nếu không có đường dẫn avatar, hiển thị một lỗi
                    showAlert(Alert.AlertType.ERROR, "Error", "Avatar not found", "Avatar path is null.");
                }
                nameTextField.setText(resultSet.getString("full_name"));
                userNameTextField.setText(resultSet.getString("username"));
                mobilePhoneTextField.setText(resultSet.getString("mobile_phone"));
                emailTextField.setText(resultSet.getString("email"));
                passswordField.setText(resultSet.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void submit_update() {
        try {
            // Lấy thông tin từ các trường nhập liệu
            String fullName = nameTextField.getText();
            String userName = userNameTextField.getText();
            String mobilePhone = mobilePhoneTextField.getText();
            String email = emailTextField.getText();
            String password = passswordField.getText();

            int userId = UserSession.getInstance().getUserId();

            String updateQuery = "UPDATE users SET full_name = ?, username = ?, mobile_phone = ?, email = ?, password = ? WHERE user_id = ?";

            Connection connect = DBConnection.connectionDB();
            PreparedStatement preparedStatement = connect.prepareStatement(updateQuery);
            preparedStatement.setString(1, fullName);
            preparedStatement.setString(2, userName);
            preparedStatement.setString(3, mobilePhone);
            preparedStatement.setString(4, email);
            preparedStatement.setString(5, password);
            preparedStatement.setInt(6, userId);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated", "Your profile has been updated successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Profile update failed", "Failed to update your profile. Please try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Database error", "An error occurred while updating your profile in the database.");
        }
    }



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
                            profile_form.setVisible(false);
                            changePass_form.setVisible(true);
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
    public void switchToChangPass() {
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
        loadUserData();

    }
    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}
