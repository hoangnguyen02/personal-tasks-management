package com.example.personaltasksmanagement.controllers;

import com.example.personaltasksmanagement.database.DBConnection;
import com.example.personaltasksmanagement.models.TaskData;
import com.example.personaltasksmanagement.models.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ResourceBundle;

public class AddNewTask implements Initializable {

    @FXML
    private Button cancelAddNew;

    @FXML
    private DatePicker datePicket;

    @FXML
    private TextField descriptionTextArea;

    @FXML
    private ComboBox<String> priorityComboBox;

    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    private Button submitAddNew;


    @FXML
    private TextField taskTextField;
    Connection connect = DBConnection.connectionDB();
    private TaskData taskData;

    public void submitButton(ActionEvent event){

        try {
            if (taskTextField.getText().isEmpty() || descriptionTextArea.getText().isEmpty()
                    || datePicket.getValue() == null
                    || statusComboBox.getValue() == null
                    || priorityComboBox.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Please fill all blank fields");
                alert.showAndWait();
            } else {
                if (datePicket.getValue().isBefore(LocalDate.now().minusDays(1L))){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("Invalid Date");
                    alert.showAndWait();
                }else {
                    if (taskData == null){
                        if (statusComboBox.getValue().equalsIgnoreCase("Complete!")) {
                            showAlert(Alert.AlertType.ERROR, "Error", "You cannot create a task with 'Complete' status");
                        } else {
                            addTask();
                            ((Node) (event.getSource())).getScene().getWindow().hide();
                        }

                    }else {
                        updateTask();
                        if (statusComboBox.getValue().equalsIgnoreCase("Complete!")) {
                            removeTaskToComplete(taskData);
                        }
                        ((Node) (event.getSource())).getScene().getWindow().hide();
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void addTask(){
       try {
           String insertData = "INSERT INTO tasks (user_id, task, description, status, priority, create_date, due_date) VALUES (?,?,?,?,?,?,?)";
           PreparedStatement preparedStatement = connect.prepareStatement(insertData);
           preparedStatement.setInt(1, UserSession.getInstance().getUserId());
           preparedStatement.setString(2, taskTextField.getText());
           preparedStatement.setString(3, descriptionTextArea.getText());
           preparedStatement.setString(4, statusComboBox.getValue());
           preparedStatement.setString(5, priorityComboBox.getValue());
           preparedStatement.setDate(6, java.sql.Date.valueOf(LocalDate.now()));
           preparedStatement.setDate(7, java.sql.Date.valueOf(datePicket.getValue()));

           preparedStatement.executeUpdate();


           Alert alert = new Alert(Alert.AlertType.INFORMATION);
           alert.setTitle("Success");
           alert.setContentText("Task added successfully");
           alert.showAndWait();
           alert.close();
       }catch (Exception e){
           e.printStackTrace();
       }
    }

    private void updateTask() {
        try {
            String updateQuery = "UPDATE tasks SET description = ?, status = ?, priority = ?, due_date = ? WHERE task = ? AND user_id = ?";
            PreparedStatement statement = connect.prepareStatement(updateQuery);
            statement.setString(1, descriptionTextArea.getText());
            statement.setString(2, statusComboBox.getValue());
            statement.setString(3, priorityComboBox.getValue());
            statement.setDate(4, java.sql.Date.valueOf(datePicket.getValue()));
            statement.setString(5, taskData.getTask());
            statement.setInt(6, UserSession.getInstance().getUserId());
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Task updated successfully");
            }else {
                showAlert(Alert.AlertType.WARNING, "Warning", "No task was updated. Please check the task details.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void removeTaskToComplete(TaskData selected) {
        try {
            int userId = UserSession.getInstance().getUserId();

            String insertQuery = "INSERT INTO complete (user_id, task_id, task, description, status, priority, complete_date) " +
                    "SELECT ?, task_id, task, description, status, priority, ? " +
                    "FROM tasks " +
                    "WHERE task_id = ?";
            PreparedStatement insertStatement = connect.prepareStatement(insertQuery);
            insertStatement.setInt(1, userId);
            insertStatement.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
            insertStatement.setInt(3, selected.getTask_id());
            insertStatement.executeUpdate();

            String deleteQuery = "DELETE FROM tasks WHERE task_id=?";
            PreparedStatement deleteStatement = connect.prepareStatement(deleteQuery);
            deleteStatement.setInt(1, selected.getTask_id());
            deleteStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ObservableList<String> getStatusName() {
        ObservableList<String> statusName = FXCollections.observableArrayList();

        String selectStatusName = "SELECT status_name FROM status";
        Connection connect = DBConnection.connectionDB();

        try {
            PreparedStatement preparedStatement = connect.prepareStatement(selectStatusName);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                statusName.add(resultSet.getString("status_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return statusName;
    }

    private ObservableList<String> getPriorityName() {
        ObservableList<String> priorityName = FXCollections.observableArrayList();

        String selectPriorityName = "SELECT priority_name FROM priority";
        Connection connect = DBConnection.connectionDB();

        try {
            PreparedStatement preparedStatement = connect.prepareStatement(selectPriorityName);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                priorityName.add(resultSet.getString("priority_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return priorityName;
    }
    public void setTaskData(TaskData taskData) {
        this.taskData = taskData;
        if (taskData != null) {
            taskTextField.setText(taskData.getTask());
            descriptionTextArea.setText(taskData.getDescription());
            statusComboBox.setValue(taskData.getStatus());
            priorityComboBox.setValue(taskData.getPriority());
            if (taskData.getDueDate() != null) {
                datePicket.setValue(LocalDate.now());
            }
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        statusComboBox.setItems(getStatusName());
        priorityComboBox.setItems(getPriorityName());

    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
