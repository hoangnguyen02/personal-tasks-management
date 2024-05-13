package com.example.personaltasksmanagement.controllers;

import com.example.personaltasksmanagement.database.DBConnection;
import com.example.personaltasksmanagement.models.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;

public class AdminUser implements Initializable {
    @FXML
    private Button delete_admin;

    @FXML
    private TextField emailTextField;

    @FXML
    private TableColumn<UserSession, String> email_admin;

    @FXML
    private TableColumn<UserSession, String> fullname_admin;

    @FXML
    private TableColumn<UserSession, String> id_admin;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private TableColumn<UserSession, String> password_admin;

    @FXML
    private TextField searchField;

    @FXML
    private Button submit_admin;

    @FXML
    private TableView<UserSession> table_admin;

    @FXML
    private TextField usernameTextField;

    @FXML
    private TableColumn<?, ?> username_admin;

    @FXML
    private ComboBox<String> roleComboBox;


    Connection connect = DBConnection.connectionDB();
    private ObservableList<UserSession> getUsersDataList() {
        ObservableList<UserSession> listData = FXCollections.observableArrayList();

        String selectData = "SELECT * FROM users";

        try {
            PreparedStatement preparedStatement = connect.prepareStatement(selectData);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                UserSession userData = new UserSession(
                        resultSet.getInt("user_id"),
                        resultSet.getString("username"),
                        resultSet.getString("email"),
                        resultSet.getString("full_name"),
                        resultSet.getString("password")
                );
                listData.add(userData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listData;
    }

    // Hiển thị danh sách người dùng trong bảng
    public void showUsersData() {
        ObservableList<UserSession> usersListData = getUsersDataList();

        password_admin.setCellValueFactory(new PropertyValueFactory<>("password"));
        username_admin.setCellValueFactory(new PropertyValueFactory<>("username"));
        email_admin.setCellValueFactory(new PropertyValueFactory<>("email"));
        fullname_admin.setCellValueFactory(new PropertyValueFactory<>("fullname"));

        table_admin.setItems(usersListData);
    }


    public void submit_action(ActionEvent event){
        System.out.println("click");
        String username = usernameTextField.getText();
        String email = emailTextField.getText();
        String fullname = nameTextField.getText();
        String password = passwordTextField.getText();
        String selectedRole = roleComboBox.getValue();

        Date date = new Date();
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        if (selectedRole != null) {
            if (selectedRole.equals("admin")) {

                try {
                    String query = "INSERT INTO admin (username, password) VALUES (?, ?)";
                    PreparedStatement statement = connect.prepareStatement(query);
                    statement.setString(1, username);
                    statement.setString(2, password);

                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Admin added successfully!");
                alert.showAndWait();

            } else {
                try {
                    String query = "INSERT INTO users (username, email, full_name, password, date) VALUES (?, ?, ?, ?,?) ";
                    PreparedStatement statement = connect.prepareStatement(query);
                    statement.setString(1, username);
                    statement.setString(2, email);
                    statement.setString(3, fullname);
                    statement.setString(4, password);
                    statement.setString(5, String.valueOf(sqlDate));
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("User added successfully!");
                alert.showAndWait();
            }

            usernameTextField.clear();
            emailTextField.clear();
            nameTextField.clear();
            passwordTextField.clear();
            roleComboBox.setValue(null);
            showUsersData();
        }

    }

    public void delete_action(){
        try {
            UserSession selectedUser = table_admin.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                String query = "DELETE FROM users WHERE user_id = ?";
                PreparedStatement statement = connect.prepareStatement(query);
                statement.setInt(1, selectedUser.getUserId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        showUsersData();
    }
    public void action_search(){
        ObservableList<UserSession> usersListData = getUsersDataList();
        FilteredList<UserSession> filteredData = new FilteredList<>(usersListData, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(user -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (user.getUsername().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (user.getEmail().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (user.getFullname().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }

                return false;
            });
        });

        SortedList<UserSession> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table_admin.comparatorProperty());
        table_admin.setItems(sortedData);

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        action_search();
        delete_action();

        showUsersData();
        ObservableList<String> roleUser = FXCollections.observableArrayList("user", "admin");

        roleComboBox.setItems(roleUser);

    }
}
