package com.example.personaltasksmanagement.controllers;

import com.example.personaltasksmanagement.Main;
import com.example.personaltasksmanagement.models.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import java.util.Optional;

public class Admin {
    @FXML
    private Button buttonFeedback;

    @FXML
    private Button buttonHistory;

    @FXML
    private Button buttonHome;

    @FXML
    private Button buttonUser;

    @FXML
    private StackPane contentArea;

    @FXML
    private Label dateTime;

    @FXML
    private Label labelName;

    @FXML
    private Label lableUsername;

    @FXML
    private Button logoutButton;

    @FXML
    private Label number_feedback;

    @FXML
    private Label number_note;

    @FXML
    private Label number_user;
    public void home_action(ActionEvent event) {
        System.out.println("click");
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/admin-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void users_action(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/admin-user-view.fxml"));
        Parent root = loader.load();
        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(root);
    }

    public void history_action(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/admin-history-view.fxml"));
        Parent root = loader.load();
        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(root);
    }

    public void feedBack_action(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/feedback-admin-view.fxml"));
        Parent root = loader.load();
        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(root);
    }
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



}
