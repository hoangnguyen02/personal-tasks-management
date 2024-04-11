package com.example.personaltasksmanagement.controllers;

import com.example.personaltasksmanagement.Main;
import com.example.personaltasksmanagement.database.DBConnection;
import com.example.personaltasksmanagement.models.Task;
import com.example.personaltasksmanagement.models.UserSession;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Dashboard implements Initializable {
    @FXML
    private Label dateTime;
    @FXML
    private Button backToHome;
    @FXML
    private StackPane contentArea;

    @FXML
    private Button logoutButton;
    @FXML
    private Label labelEmail;

    @FXML
    private Label labelName;

    @FXML
    private Label lableUsername;

    @FXML
    private TableView<Task> table_recent;

    @FXML
    private TableColumn<Task, String> title_recent;

    @FXML
    private TableColumn<Task, String> status_recent;


    Connection connect = DBConnection.connectionDB();
    private void loadUserData() {
        try {
            int userId = UserSession.getInstance().getUserId();
            String selectQuery = "SELECT * FROM users WHERE user_id = ?";

            Connection connect = DBConnection.connectionDB();
            PreparedStatement preparedStatement = connect.prepareStatement(selectQuery);
            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                labelName.setText(resultSet.getString("full_name"));
                lableUsername.setText(resultSet.getString("username"));
                labelEmail.setText(resultSet.getString("email"));

                labelName.setText(resultSet.getString("full_name"));
                lableUsername.setText(resultSet.getString("username"));
                labelEmail.setText(resultSet.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ObservableList<Task> getRecentTaskStatus() {
        ObservableList<Task> recentTaskStatus = FXCollections.observableArrayList();
        try {
            String query =  "SELECT task, status FROM tasks ORDER BY create_at DESC LIMIT 5";
            PreparedStatement statement = connect.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String task = resultSet.getString("task");
                String status = resultSet.getString("status");
                recentTaskStatus.add(new Task(task, status));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recentTaskStatus;
    }

    //final
    public void runTime() {
        new Thread(() -> {
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (dateTime != null) { // Kiểm tra xem biến dateTime đã được khởi tạo chưa trước khi sử dụng
                    Platform.runLater(() -> {
                        dateTime.setText(format.format(new Date()));
                    });
                }
            }
        }).start();

    }
    //final
    public void backToHome(ActionEvent event){
        System.out.println("click");
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/dashboard-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //final
    public void logoutAccount(){
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Message");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to logout?");
            Optional<ButtonType> option = alert.showAndWait();

            if (option.get().equals(ButtonType.OK)) {
                Stage stage = (Stage) logoutButton.getScene().getWindow();
                stage.close();
                UserSession.getInstance().clearSession();

                Parent root = FXMLLoader.load(Main.class.getResource("views/controller-view.fxml"));
                Stage newStage = new Stage();
                Scene scene = new Scene(root);
                newStage.setScene(scene);
                newStage.show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadTasksPage(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/tasks-view.fxml"));
        Parent root = loader.load();
        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(root);
    }

    public void loadSettingPage(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/setting-view.fxml"));
        Parent root = loader.load();
        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(root);
    }

    public void loadDeletePage(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/delete-view.fxml"));
        Parent root = loader.load();
        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(root);
    }
    public void loadCompletePage(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/complete-view.fxml"));
        Parent root = loader.load();
        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(root);
   }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        loadUserData();
        ObservableList<Task> taskStatusList = getRecentTaskStatus();
        title_recent.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTask()));
        status_recent.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        table_recent.setItems(taskStatusList);

        runTime();
    }
}
