package com.example.personaltasksmanagement.controllers;

import com.example.personaltasksmanagement.Main;
import com.example.personaltasksmanagement.database.DBConnection;
import com.example.personaltasksmanagement.models.TaskData;
import com.example.personaltasksmanagement.models.UserSession;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.ResourceBundle;

public class Delete implements Initializable {
    @FXML
    private TableView<TaskData> table_trashs;

    @FXML
    private TableColumn<TaskData, String> trashAction;

    @FXML
    private TableColumn<?, ?> trashCreate;

    @FXML
    private TableColumn<?, ?> trashDescription;

    @FXML
    private TableColumn<?, ?> trashDueDate;

    @FXML
    private TableColumn<?, ?> trashPriority;

    @FXML
    private TableColumn<?, ?> trashStatus;

    @FXML
    private TableColumn<?, ?> trashTask;

    Connection connect = DBConnection.connectionDB();

    public ObservableList<TaskData> TrashList(){
        ObservableList<TaskData> listData = FXCollections.observableArrayList();
        String selectData = "SELECT * FROM trash WHERE user_id = ?";

        try {
            PreparedStatement preparedStatement = connect.prepareStatement(selectData);
            preparedStatement.setInt(1, UserSession.getInstance().getUserId());
            ResultSet resultSet = preparedStatement.executeQuery();

            TaskData taskData;
            while (resultSet.next()){
                taskData = new TaskData(resultSet.getInt("task_id"),
                        resultSet.getInt("user_id"),
                        resultSet.getString("task"),
                        resultSet.getString("description"),
                        resultSet.getString("status"),
                        resultSet.getString("priority"),
                        resultSet.getDate("create_at").toLocalDate(),
                        resultSet.getDate("due_date"));
                listData.add(taskData);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return listData;
    }

    public void TrashShowData() {
        ObservableList<TaskData> TaskListData = TrashList();

        trashTask.setCellValueFactory(new PropertyValueFactory<>("task"));
        trashDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        trashStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        trashPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));
        trashCreate.setCellValueFactory(new PropertyValueFactory<>("createdDate"));
        trashDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));

        // Tạo ô chứa nút xoá và khôi phục
        Callback<TableColumn<TaskData, String>, TableCell<TaskData, String>> cellFactory = (TableColumn<TaskData, String> param) -> {
            final TableCell<TaskData, String> cell = new TableCell<TaskData, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
                        FontAwesomeIconView recoveryIcon = new FontAwesomeIconView(FontAwesomeIcon.UNDO);

                        deleteIcon.setStyle(
                                "-fx-cursor: hand ;"
                                        + "-glyph-size: 28px;"
                                        + "-fx-fill: red;"
                        );
                        recoveryIcon.setStyle(
                                "-fx-cursor: hand ;"
                                        + "-glyph-size: 28px;"
                                        + "-fx-fill: green;"
                        );

                        deleteIcon.setOnMouseClicked((MouseEvent event) -> {
                            TaskData selectedTask = getTableView().getItems().get(getIndex());

                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Confirmation");
                            alert.setHeaderText("Delete Task");
                            alert.setContentText("Are you sure you want to delete this task?");

                            Optional<ButtonType> result = alert.showAndWait();
                            if (result.isPresent() && result.get() == ButtonType.OK) {
                                try {
                                    String deleteQuery = "DELETE FROM trash WHERE task_id = ?";
                                    PreparedStatement statement = connect.prepareStatement(deleteQuery);
                                    statement.setInt(1, selectedTask.getTask_id());
                                    int rowsDeleted = statement.executeUpdate();

                                    if (rowsDeleted > 0) {
                                        TaskListData.remove(selectedTask); // Xóa task khỏi ObservableList
                                        TrashShowData(); // Cập nhật lại TableView sau khi xóa thành công

                                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                                        successAlert.setTitle("Success");
                                        successAlert.setHeaderText(null);
                                        successAlert.setContentText("Task deleted successfully");
                                        successAlert.showAndWait();
                                    } else {
                                        Alert warningAlert = new Alert(Alert.AlertType.WARNING);
                                        warningAlert.setTitle("Warning");
                                        warningAlert.setHeaderText(null);
                                        warningAlert.setContentText("Task not found or already deleted");
                                        warningAlert.showAndWait();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();

                                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                                    errorAlert.setTitle("Error");
                                    errorAlert.setHeaderText(null);
                                    errorAlert.setContentText("Failed to delete task");
                                    errorAlert.showAndWait();
                                }
                            }
                        });
                        recoveryIcon.setOnMouseClicked((MouseEvent event) -> {
                            TaskData selectedTask = getTableView().getItems().get(getIndex());

                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Confirmation");
                            alert.setHeaderText("Recover Task");
                            alert.setContentText("Are you sure you want to recover this task?");

                            Optional<ButtonType> result = alert.showAndWait();
                            if (result.isPresent() && result.get() == ButtonType.OK) {
                                boolean success = removeTask(selectedTask);
                                if (success) {
                                    TrashShowData(); // Cập nhật lại ListView sau khi xóa thành công
                                }
                            }
                        });

                        // Tạo HBox chứa các nút xoá và khôi phục
                        HBox actionBox = new HBox(deleteIcon, recoveryIcon);
                        actionBox.setStyle("-fx-alignment: center;");
                        HBox.setMargin(deleteIcon, new Insets(2, 3, 0, 2));
                        HBox.setMargin(recoveryIcon, new Insets(2, 2, 0, 3));

                        setGraphic(actionBox);
                        setText(null);
                    }
                }
            };

            return cell;
        };

        trashAction.setCellFactory(cellFactory);
        table_trashs.setItems(TrashList());
    }

    public boolean removeTask(TaskData selected) {
        try {
            String insertQuery = "INSERT INTO tasks (user_id,task, description, status, priority, create_at, due_date) " +
                    "SELECT user_id, task, description, status, priority, create_at, due_date " +
                    "FROM trash " +
                    "WHERE task_id = ?";

            PreparedStatement preparedStatement = connect.prepareStatement(insertQuery);
            preparedStatement.setInt(1, selected.getTask_id());
            preparedStatement.executeUpdate();

            String deleteQuery = "DELETE FROM Trash WHERE task_id=?";
            preparedStatement = connect.prepareStatement(deleteQuery);
            preparedStatement.setInt(1, selected.getTask_id());
            preparedStatement.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Success", "Task recovered successfully");
            return true;
        } catch (Exception e) {
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
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        TrashShowData();
    }
}
