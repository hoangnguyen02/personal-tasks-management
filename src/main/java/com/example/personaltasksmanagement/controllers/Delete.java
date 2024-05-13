package com.example.personaltasksmanagement.controllers;

import com.example.personaltasksmanagement.database.DBConnection;
import com.example.personaltasksmanagement.models.TaskData;
import com.example.personaltasksmanagement.models.UserSession;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class Delete implements Initializable {
    @FXML
    private TableView<TaskData> trash_table;

    @FXML
    private TableColumn<TaskData, String> trashAction;

    @FXML
    private TableColumn<TaskData, String> trashTask;

    @FXML
    private TableColumn<TaskData, String> trashDescription;

    @FXML
    private TableColumn<TaskData, String> trashStatus;

    @FXML
    private TableColumn<TaskData, String> trashPriority;

    @FXML
    private TableColumn<TaskData, LocalDate> trashDelete;

    private final Connection connect = DBConnection.connectionDB();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TrashShowData();
    }

    public ObservableList<TaskData> TrashList() {
        ObservableList<TaskData> listData = FXCollections.observableArrayList();
        String selectData = "SELECT * FROM trash WHERE user_id = ?";

        try {
            PreparedStatement preparedStatement = connect.prepareStatement(selectData);
            preparedStatement.setInt(1, UserSession.getInstance().getUserId());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                TaskData taskData = new TaskData(
                        resultSet.getInt("task_id"),
                        resultSet.getInt("user_id"),
                        resultSet.getString("task"),
                        resultSet.getString("description"),
                        resultSet.getString("status"),
                        resultSet.getString("priority"),
                        null,
                        null,
                        null,
                        resultSet.getDate("delete_date").toLocalDate());
                listData.add(taskData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listData;
    }

    public void TrashShowData() {
        ObservableList<TaskData> taskListData = TrashList();

        trashTask.setCellValueFactory(new PropertyValueFactory<>("task"));
        trashDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        trashStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        trashPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));
        trashDelete.setCellValueFactory(new PropertyValueFactory<>("delete"));

        trashAction.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    TaskData selectedTask = getTableView().getItems().get(getIndex());

                    FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
                    FontAwesomeIconView recoveryIcon = new FontAwesomeIconView(FontAwesomeIcon.UNDO);

                    deleteIcon.setStyle("-fx-cursor: hand ; -glyph-size: 28px; -fx-fill: red;");
                    recoveryIcon.setStyle("-fx-cursor: hand ; -glyph-size: 28px; -fx-fill: green;");

                    deleteIcon.setOnMouseClicked(event -> {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Confirmation");
                        alert.setHeaderText("Delete Task");
                        alert.setContentText("Are you sure you want to delete this task?");

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            boolean success = deleteTask(selectedTask);
                            if (success) {
                                taskListData.remove(selectedTask);
                            }
                        }
                    });

                    recoveryIcon.setOnMouseClicked(event -> {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Confirmation");
                        alert.setHeaderText("Recover Task");
                        alert.setContentText("Are you sure you want to recover this task?");

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            boolean success = recoverTask(selectedTask);
                            if (success) {
                                taskListData.remove(selectedTask);
                               try {
                                   String deleteQuery = "DELETE FROM trash WHERE task_id=?";
                                   PreparedStatement statement = connect.prepareStatement(deleteQuery);
                                   statement.setInt(1, selectedTask.getTask_id());
                               }catch (SQLException e){
                                   e.printStackTrace();
                               }
                            }
                        }
                    });

                    HBox actionBox = new HBox(deleteIcon, recoveryIcon);
                    actionBox.setStyle("-fx-alignment: center;");
                    setGraphic(actionBox);
                    setText(null);
                }
            }
        });

        trash_table.setItems(taskListData);
    }

    private boolean deleteTask(TaskData selectedTask) {
        try {
            String deleteQuery = "DELETE FROM trash WHERE task_id=?";
            PreparedStatement statement = connect.prepareStatement(deleteQuery);
            statement.setInt(1, selectedTask.getTask_id());
            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Task deleted successfully");
                return true;
            } else {
                showAlert(Alert.AlertType.WARNING, "Warning", "Task not found or already deleted");
                return false;
            }


        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete task");
            return false;
        }
    }

    private boolean recoverTask(TaskData selectedTask) {
        try {
            int userId = UserSession.getInstance().getUserId();

            String selectQuery = "SELECT task, description, status, priority, create_date, due_date FROM tmpTrash WHERE task_id = ?";
            PreparedStatement selectStatement = connect.prepareStatement(selectQuery);
            selectStatement.setInt(1, selectedTask.getTask_id());
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                String task = resultSet.getString("task");
                String description = resultSet.getString("description");
                String status = resultSet.getString("status");
                String priority = resultSet.getString("priority");
                LocalDate createDate = resultSet.getDate("create_date").toLocalDate();
                LocalDate dueDate = resultSet.getDate("due_date").toLocalDate();

                String insertQuery = "INSERT INTO tasks (user_id, task, description, status, priority, create_date, due_date) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement insertStatement = connect.prepareStatement(insertQuery);
                insertStatement.setInt(1, userId);
                insertStatement.setString(2, task);
                insertStatement.setString(3, description);
                insertStatement.setString(4, status);
                insertStatement.setString(5, priority);
                insertStatement.setDate(6, Date.valueOf(createDate));
                insertStatement.setDate(7, Date.valueOf(dueDate));
                insertStatement.executeUpdate();

                String deleteQuery = "DELETE FROM tmpTrash WHERE task_id=?";
                PreparedStatement deleteStatement = connect.prepareStatement(deleteQuery);
                deleteStatement.setInt(1, selectedTask.getTask_id());
                deleteStatement.executeUpdate();

                showAlert(Alert.AlertType.INFORMATION, "Success", "Task recovered successfully");
                return true;
            } else {
                showAlert(Alert.AlertType.WARNING, "Warning", "Task not found in tmpDelete");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to recover task");
            return false;
        }
    }







    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
