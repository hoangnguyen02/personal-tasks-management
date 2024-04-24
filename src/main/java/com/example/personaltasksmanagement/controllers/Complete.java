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
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class Complete implements Initializable {
    @FXML
    private TableColumn<TaskData, String> completeAction;

    @FXML
    private TableColumn<TaskData, String> completeDescription;

    @FXML
    private TableColumn<TaskData, String> completePriority;

    @FXML
    private TableColumn<TaskData, String> completeStatus;

    @FXML
    private TableColumn<TaskData, String> completeTask;

    @FXML
    private TableView<TaskData> complete_table;

    @FXML
    private TableColumn<TaskData, LocalDate> completedAt;
    Connection connect = DBConnection.connectionDB();

    public ObservableList<TaskData> CompleteList(){
        ObservableList<TaskData> listData = FXCollections.observableArrayList();
        String selectData = "SELECT * FROM complete WHERE user_id = ?";

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
                        null,
                        null,
                        resultSet.getDate("complete_date").toLocalDate(),
                        null);
                listData.add(taskData);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return listData;
    }

    public void CompleteShowData() {
        ObservableList<TaskData> TaskListData = CompleteList();

        completeTask.setCellValueFactory(new PropertyValueFactory<>("task"));
        completeDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        completeStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        completePriority.setCellValueFactory(new PropertyValueFactory<>("priority"));
        completedAt.setCellValueFactory(new PropertyValueFactory<>("complete"));

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

                        deleteIcon.setStyle(
                                "-fx-cursor: hand ;"
                                        + "-glyph-size: 28px;"
                                        + "-fx-fill: red;"
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
                                    String deleteQuery = "DELETE FROM complete WHERE task_id = ?";
                                    PreparedStatement statement = connect.prepareStatement(deleteQuery);
                                    statement.setInt(1, selectedTask.getTask_id());
                                    int rowsDeleted = statement.executeUpdate();

                                    if (rowsDeleted > 0) {
                                        TaskListData.remove(selectedTask);

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
                        // Tạo HBox chứa các nút xoá và khôi phục
                        HBox actionBox = new HBox(deleteIcon);
                        actionBox.setStyle("-fx-alignment: center;");
                        HBox.setMargin(deleteIcon, new Insets(2, 3, 0, 2));

                        setGraphic(actionBox);
                        setText(null);
                    }
                }
            };

            return cell;
        };

        completeAction.setCellFactory(cellFactory);
        complete_table.setItems(TaskListData);
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        CompleteShowData();

    }
}
