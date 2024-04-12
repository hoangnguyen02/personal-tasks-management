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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
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
    @FXML
    private Button updateNote;
    @FXML
    private TextArea noteView_Dashboard;

    @FXML
    private Button buttonNote;

    @FXML
    private Button saveNote;
    @FXML
    private Button clearNote;
    @FXML
    private Label number_complete;

    @FXML
    private Label number_note;

    @FXML
    private Label number_total;
    private File openedFile;
    @FXML
    private TextField title_note;


    Connection connect = DBConnection.connectionDB();
    private void countCompleteTasks() {
        try {
            String query = "SELECT COUNT(*) FROM complete";
            PreparedStatement statement = connect.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                number_complete.setText(String.valueOf(count));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void countNotes() {
        try {
            String query = "SELECT COUNT(*) FROM notes";
            PreparedStatement statement = connect.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                number_note.setText(String.valueOf(count));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void countTotalTasks() {
        try {
            String query = "SELECT COUNT(*) FROM tasks";
            PreparedStatement statement = connect.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                number_total.setText(String.valueOf(count));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void save_action(ActionEvent event){
        if (openedFile != null) {
            try {
                FileWriter fileWriter = new FileWriter(openedFile);
                fileWriter.write(noteView_Dashboard.getText());
                fileWriter.close();
                showAlert(Alert.AlertType.INFORMATION, "Success", "File saved successfully");
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save file: " + e.getMessage());
            }
        } else {
            int userId = UserSession.getInstance().getUserId();
            String titleContent = title_note.getText();
            String noteContent = noteView_Dashboard.getText();

            try {
                String insertQuery = "INSERT INTO notes (user_id,title, content) VALUES (?, ?, ?)";
                PreparedStatement insertStatement = connect.prepareStatement(insertQuery);
                insertStatement.setInt(1, userId);
                insertStatement.setString(2, titleContent);
                insertStatement.setString(3, noteContent);
                insertStatement.executeUpdate();

                showAlert(Alert.AlertType.INFORMATION, "Success", "Note saved to database successfully");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save note to database: " + e.getMessage());
            }
        }
    }

    public void clear_action(){
        title_note.clear();
        noteView_Dashboard.clear();
    }
    public void import_action(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Text File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                String fileName = selectedFile.getName();
                title_note.setText(fileName);
                BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
                String line;
                StringBuilder text = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    text.append(line).append("\n");
                }
                reader.close();
                noteView_Dashboard.setText(text.toString());
                openedFile = selectedFile;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
//    public void update_action(ActionEvent event){
//        if (openedFile != null) {
//            String newText = noteView_Dashboard.getText();
//
//            try {
//                FileWriter fileWriter = new FileWriter(openedFile);
//                fileWriter.write(newText);
//                fileWriter.close();
//                showAlert(Alert.AlertType.INFORMATION, "Success", "File updated successfully");
//            } catch (IOException e) {
//                e.printStackTrace();
//                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update file: " + e.getMessage());
//            }
//        } else {
//            showAlert(Alert.AlertType.WARNING, "Warning", "No file opened");
//        }
//    }
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
                if (dateTime != null) {
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
    public void loadNotePage(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/note-view.fxml"));
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
        countCompleteTasks();
        countNotes();
        countTotalTasks();
        loadUserData();
        clear_action();

        ObservableList<Task> taskStatusList = getRecentTaskStatus();
        title_recent.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTask()));
        status_recent.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        table_recent.setItems(taskStatusList);

        runTime();
    }
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
