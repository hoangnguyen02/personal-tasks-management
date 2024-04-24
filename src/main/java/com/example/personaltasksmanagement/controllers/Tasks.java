package com.example.personaltasksmanagement.controllers;

import com.example.personaltasksmanagement.models.Task;
import com.example.personaltasksmanagement.models.UserSession;
import com.example.personaltasksmanagement.Main;
import com.example.personaltasksmanagement.database.DBConnection;
import com.example.personaltasksmanagement.models.TaskData;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
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

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class Tasks implements Initializable {
    @FXML
    private TableColumn<TaskData, String> actionColumn;

    @FXML
    private Button addTaskButton;

    @FXML
    private TableColumn<TaskData, String> createColumn;

    @FXML
    private TableColumn<TaskData, String> descriptionColumn;

    @FXML
    private TableColumn<TaskData, String> dueDateColumn;

    @FXML
    private TableColumn<TaskData, Integer> priorityColumn;

    @FXML
    private TextField searchTextField;

    @FXML
    private TableColumn<TaskData, Integer> statusColumn;

    @FXML
    private TableColumn<TaskData, String> taskColumn;

    @FXML
    private TableView<TaskData> table_tasks;

    Connection connect = DBConnection.connectionDB();

    public ObservableList<TaskData> TasksDataList() {
        ObservableList<TaskData> listData = FXCollections.observableArrayList();

        String selectData = "SELECT * FROM tasks WHERE user_id = ?";

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
                        resultSet.getDate("create_date").toLocalDate(),
                        resultSet.getDate("due_date").toLocalDate(),
                        null,
                        null);
                listData.add(taskData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listData;
    }

    private static Comparator<TaskData> PriorityComparator = Comparator.comparing(TaskData::getPriority);

    public void TaskShowData() {
        ObservableList<TaskData> TaskListData = TasksDataList();
        TaskListData.sort(PriorityComparator);

        taskColumn.setCellValueFactory(new PropertyValueFactory<>("task"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        createColumn.setCellValueFactory(new PropertyValueFactory<>("createdDate"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));

        Callback<TableColumn<TaskData, String>, TableCell<TaskData, String>> cellFactory = (TableColumn<TaskData, String> param) -> {
            final TableCell<TaskData, String> cell = new TableCell<TaskData, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        FontAwesomeIconView editIcon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL_SQUARE);
                        FontAwesomeIconView deleteIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);

                        editIcon.setStyle(
                                "-fx-cursor: hand ;"
                                        + "-glyph-size: 28px;"
                                        + "-fx-fill: #00E676;"
                        );
                        deleteIcon.setStyle(
                                "-fx-cursor: hand ;"
                                        + "-glyph-size: 28px;"
                                        + "-fx-fill: #ff1744;"
                        );

                        editIcon.setOnMouseClicked((MouseEvent event) -> {
                            try {
                                TaskData selectedTask = getTableView().getItems().get(getIndex());

                                FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/add-new.fxml"));
                                Parent root = loader.load();
                                AddNewTask controller = loader.getController();
                                if (controller == null) {
                                    controller = new AddNewTask();
                                    loader.setController(controller);
                                }
                                controller.setTaskData(selectedTask);
                                Stage stage = new Stage();
                                stage.setScene(new Scene(root));
                                stage.showAndWait();
                                TaskShowData();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        deleteIcon.setOnMouseClicked((MouseEvent event) -> {
                            TaskData selectedTask = getTableView().getItems().get(getIndex());

                            // Xác nhận xóa task
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Confirmation");
                            alert.setHeaderText("Delete Task");
                            alert.setContentText("Are you sure you want to delete this task?");

                            Optional<ButtonType> result = alert.showAndWait();
                            if (result.isPresent() && result.get() == ButtonType.OK) {
                                // Xóa task từ cơ sở dữ liệu
                                deleteTask(selectedTask);

                                // Cập nhật TableView
                                TaskListData.remove(selectedTask);
                            }
                        });
                        HBox actionBox = new HBox(editIcon, deleteIcon);
                        actionBox.setStyle("-fx-alignment: center;");
                        HBox.setMargin(editIcon, new Insets(2, 3, 0, 2));
                        HBox.setMargin(deleteIcon, new Insets(2, 2, 0, 3));

                        setGraphic(actionBox);
                        setText(null);
                    }
                }
            };
            return cell;
        };

        actionColumn.setCellFactory(cellFactory);

        table_tasks.setItems(TaskListData);
    }

    private void deleteTask(TaskData selectedTask) {
        try {
            int userId = UserSession.getInstance().getUserId();
            // Insert necessary data into trash table
            String trashInsertQuery = "INSERT INTO trash (user_id, task_id, task, description, status, priority, delete_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement trashInsertStatement = connect.prepareStatement(trashInsertQuery);
            trashInsertStatement.setInt(1, userId);
            trashInsertStatement.setInt(2, selectedTask.getTask_id());
            trashInsertStatement.setString(3, selectedTask.getTask());
            trashInsertStatement.setString(4, selectedTask.getDescription());
            trashInsertStatement.setString(5, selectedTask.getStatus());
            trashInsertStatement.setString(6, selectedTask.getPriority());
            trashInsertStatement.setDate(7, java.sql.Date.valueOf(LocalDate.now()));
            trashInsertStatement.executeUpdate();

            String tmpDeleteInsertQuery = "INSERT INTO tmpTrash (user_id, task_id, task, description, status, priority, create_date, due_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?,?)";
            PreparedStatement tmpDeleteInsertStatement = connect.prepareStatement(tmpDeleteInsertQuery);
            tmpDeleteInsertStatement.setInt(1, userId);
            tmpDeleteInsertStatement.setInt(2, selectedTask.getTask_id());
            tmpDeleteInsertStatement.setString(3, selectedTask.getTask());
            tmpDeleteInsertStatement.setString(4, selectedTask.getDescription());
            tmpDeleteInsertStatement.setString(5, selectedTask.getStatus());
            tmpDeleteInsertStatement.setString(6, selectedTask.getPriority());
            tmpDeleteInsertStatement.setDate(7, java.sql.Date.valueOf(selectedTask.getCreatedDate()));
            tmpDeleteInsertStatement.setDate(8, java.sql.Date.valueOf(selectedTask.getDueDate()));
            tmpDeleteInsertStatement.executeUpdate();

            String deleteQuery = "DELETE FROM tasks WHERE task_id = ?";
            PreparedStatement deleteStatement = connect.prepareStatement(deleteQuery);
            deleteStatement.setInt(1, selectedTask.getTask_id());
            deleteStatement.executeUpdate();

            TaskShowData();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred: " + e.getMessage());
        }
    }

    public void action_search() {

        FilteredList<TaskData> filter = new FilteredList<>(TasksDataList(), e -> true);

        searchTextField.textProperty().addListener((Observable, oldValue, newValue) -> {

            filter.setPredicate(PrediateFlowerData -> {

                if (newValue.isEmpty() || newValue == null) {
                    return true;
                }

                String searchKey = newValue.toLowerCase();

                if (PrediateFlowerData.getTask_id().toString().contains(searchKey)) {
                    return true;
                } else if (PrediateFlowerData.getTask().toString().toLowerCase().contains(searchKey)) {
                    return true;
                } else {
                    return false;
                }
            });

        });

        SortedList<TaskData> sortList = new SortedList<>(filter);

        sortList.comparatorProperty().bind(table_tasks.comparatorProperty());

        table_tasks.setItems(sortList);

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TaskShowData();
        action_search();
    }

    public void addTask(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/add-new.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();

        stage.setScene(new Scene(root));
        stage.setTitle("Add New Tasks");
        stage.showAndWait();

        TaskShowData();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
