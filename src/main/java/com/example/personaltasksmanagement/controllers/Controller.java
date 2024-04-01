package com.example.personaltasksmanagement.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import com.example.personaltasksmanagement.Main;
import com.example.personaltasksmanagement.database.DBConnection;
import com.example.personaltasksmanagement.models.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class Controller implements Initializable {
    @FXML
    private AnchorPane background;

    @FXML
    private Button buttonLogin;

    @FXML
    private Button buttonRegister;

    @FXML
    private TextField emailRegister;

    @FXML
    private Hyperlink forgetPasword;

    @FXML
    private TextField fullnameRegister;

    @FXML
    private Button loginGithub;

    @FXML
    private Button loginGoogle;

    @FXML
    private PasswordField loginPassword;
    @FXML
    private TextField showPasswordField;

    @FXML
    private Hyperlink loginRegister;

    @FXML
    private TextField loginUsername;

    @FXML
    private AnchorPane login_form;

    @FXML
    private Hyperlink login_registerHere;

    @FXML
    private AnchorPane main_form;

    @FXML
    private PasswordField passwordRegister;

    @FXML
    private AnchorPane register_form;

    @FXML
    private PasswordField repeatPasswordRegister;

    @FXML
    private CheckBox showPassword;

    @FXML
    private TextField usernameRegister;


    public void registerAccount(){
        if (fullnameRegister.getText().isEmpty()
                && emailRegister.getText().isEmpty()
                && usernameRegister.getText().isEmpty()
                && passwordRegister.getText().isEmpty()
                && repeatPasswordRegister.getText().isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Please fill all blank fields");
            alert.showAndWait();
        } else if (!isValidGmail(emailRegister.getText())){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Invalid Gmail address");
            alert.showAndWait();

        } else {
            try {
                String checkUsername = "SELECT * FROM users WHERE username = '" + usernameRegister.getText() + "'";

                Connection connect = DBConnection.connectionDB();
                PreparedStatement prepare = connect.prepareStatement(checkUsername);
                ResultSet result = prepare.executeQuery();
                if (result.next()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText( usernameRegister.getText() + " is already exist!");
                    alert.showAndWait();
                } else if (passwordRegister.getText().length() < 2) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("Invalid Password, at least 2 characters needed");
                    alert.showAndWait();
                } else if(!passwordRegister.getText().equals(repeatPasswordRegister.getText())){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("Passwords do not match");
                    alert.showAndWait();
                }
                else {
                    String insertData = "INSERT INTO users (full_name, email, username, password, date) VALUES(?,?,?,?,?)";

                    Date date = new Date();
                    java.sql.Date sqlDate = new java.sql.Date(date.getTime());

                    try {
                        PreparedStatement preparedStatement = connect.prepareStatement(insertData);
                        preparedStatement.setString(1, fullnameRegister.getText());
                        preparedStatement.setString(2, emailRegister.getText());
                        preparedStatement.setString(3, usernameRegister.getText());
                        preparedStatement.setString(4, passwordRegister.getText());

                        preparedStatement.setString(5, String.valueOf(sqlDate));

                        preparedStatement.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Succesful");
                    alert.setContentText("Registered Successfully!");
                    alert.showAndWait();
                    registerClear();

                    login_form.setVisible(true);
                    register_form.setVisible(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isValidGmail(String email) {
        String regex = "^[a-zA-Z0-9_]+(\\.[a-zA-Z0-9_]+)*@gmail\\.com$";
        return Pattern.matches(regex, email);
    }


    public void registerClear() {
        fullnameRegister.clear();
        emailRegister.clear();
        usernameRegister.clear();
        passwordRegister.clear();
    }

    public void loginShowPassword() {
        if (showPassword.isSelected()) {
            showPasswordField.setText(loginPassword.getText());
            showPasswordField.setVisible(true);
            loginPassword.setVisible(false);
        } else {
            loginPassword.setText(showPasswordField.getText());
            showPasswordField.setVisible(false);
            loginPassword.setVisible(true);
        }
    }


    public void loginAccount() throws SQLException {
        if(loginUsername.getText().isEmpty() && loginPassword.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setContentText("Incorrect Username/Password");
            alert.showAndWait();
        }else {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            Connection connect = DBConnection.connectionDB();

            try {
                PreparedStatement preparedStatement = connect.prepareStatement(sql);
                preparedStatement.setString(1, loginUsername.getText());
                preparedStatement.setString(2, loginPassword.getText());

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()){
                    try {
                        int userId = resultSet.getInt("user_id"); ;
                        UserSession.getInstance().setUserId(userId);

                        FXMLLoader loader =new FXMLLoader(Main.class.getResource("views/dashboard-view.fxml"));
                        Parent root =loader.load();
                        Stage stage = (Stage) buttonLogin.getScene().getWindow();
                        stage.setScene(new Scene(root, Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE));
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error!");
                    alert.setContentText("Incorrect Username/ Password");
                    alert.showAndWait();
                }
                connect.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public void initialize(URL url, ResourceBundle resourceBundle){

    }
    @FXML
    public void switchToLogin(ActionEvent event) {
        login_form.setVisible(true);
        register_form.setVisible(false);
    }
    @FXML
    public void switchToRegister(ActionEvent event) {
        login_form.setVisible(false);
        register_form.setVisible(true);
    }

    public void forgetPassword(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/forget-view.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Forget Password");
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }
}
