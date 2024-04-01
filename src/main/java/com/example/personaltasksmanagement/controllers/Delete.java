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
                taskData = new TaskData(resultSet.getInt("trash_id"),
                        resultSet.getInt("user_id"),
                        resultSet.getString("task"),
                        resultSet.getString("description"),
                        resultSet.getString("status"),
                        resultSet.getString("priority"),
                        resultSet.getDate("create_at").toLocalDate(),
                        resultSet.getDate("due_date"));
                listData.add(taskData);
            }
        }catch (Exception e){
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



        // Tạo ô chứa nút chỉnh sửa và xóa
        Callback<TableColumn<TaskData, String>, TableCell<TaskData, String>> cellFactory = (TableColumn<TaskData, String> param) -> {
            final TableCell<TaskData, String> cell = new TableCell<TaskData, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        // Tạo icon cho các nút chỉnh sửa và xóa
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

                        // Xử lý sự kiện khi nhấp vào nút chỉnh sửa
                        editIcon.setOnMouseClicked((MouseEvent event) -> {
                            try {
                                // Lấy dữ liệu của task được chọn
                                TaskData selectedTask = getTableView().getItems().get(getIndex());

                                // Hiển thị cửa sổ chỉnh sửa task
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
                                TrashShowData();
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

                                // Cập nhật TableView
                                TaskListData.remove(selectedTask);
                            }
                        });

                        // Tạo HBox chứa các nút chỉnh sửa và xóa
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

        trashAction.setCellFactory(cellFactory);

        table_trashs.setItems(TrashList());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        TrashShowData();
    }
}
