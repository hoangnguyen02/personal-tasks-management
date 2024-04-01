package com.example.personaltasksmanagement.controllers;

import com.example.personaltasksmanagement.Main;
import com.example.personaltasksmanagement.models.UserSession;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

public class Dashboard implements Initializable {
    @FXML
    private Label dateTime;
    @FXML
    private Button backToHome;
    @FXML
    private StackPane contentArea;

    @FXML
    private Button logoutButton;

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

            // Hiển thị Stage
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
        runTime();
    }
}
