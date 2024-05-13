package com.example.personaltasksmanagement.controllers;

import com.example.personaltasksmanagement.database.DBConnection;
import com.example.personaltasksmanagement.models.NoteData;
import com.example.personaltasksmanagement.models.UserSession;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Note implements Initializable {

    @FXML
    private ListView<NoteData> listNote;
    @FXML
    private TextArea AreaContent;

    @FXML
    private Button clearButton;

    @FXML
    private Button deleteButton;


    @FXML
    private Button saveButton;



    Connection connect = DBConnection.connectionDB();

    public void clear_action() {
        AreaContent.clear();
    }
    public void delete_action(){
        NoteData selectedItem = listNote.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            String deleteQuery = "DELETE FROM notes WHERE title = ?";
            try {
                PreparedStatement preparedStatement = connect.prepareStatement(deleteQuery);
                preparedStatement.setString(1, selectedItem.getTitle());
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    listNote.getItems().remove(selectedItem);
                    AreaContent.clear();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Note Delete successfully");
                }else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete note");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public void save_action(ActionEvent event){
        NoteData selectedItem = listNote.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            String updateQuery = "UPDATE notes SET content = ? WHERE title = ?";
            try {
                PreparedStatement preparedStatement = connect.prepareStatement(updateQuery);
                preparedStatement.setString(1, AreaContent.getText());
                preparedStatement.setString(2, selectedItem.getTitle());
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    selectedItem.setContent(AreaContent.getText());
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Note updated successfully");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to update note");
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save note");
                e.printStackTrace();
            }
        }
    }

    public ObservableList<NoteData> NoteDataList() {
        ObservableList<NoteData> listDataNote = FXCollections.observableArrayList();
        String selectQuery = "SELECT title, content FROM notes WHERE user_id = ?";
        try {
            PreparedStatement preparedStatement = connect.prepareStatement(selectQuery);
            preparedStatement.setInt(1, UserSession.getInstance().getUserId());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String content = resultSet.getString("content");
                NoteData note = new NoteData(title, content);
                listDataNote.add(note);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listDataNote;
    }
    public void populateListView(ObservableList<NoteData> listDataNote) {
        listNote.getItems().addAll(listDataNote);
    }

    public void setupListViewListener() {
        listNote.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                displayNoteContent(newValue.getContent());
            }
        });
    }

    public void displayNoteContent(String content) {
        AreaContent.setText(content);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<NoteData> notes = NoteDataList();
        populateListView(notes);
        setupListViewListener();
        listNote.setEditable(true);

        listNote.setItems(notes);
        listNote.setCellFactory(param -> new ListCell<NoteData>() {
            private final TextField textField = new TextField();

            {
                textField.setEditable(false);

                textField.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2) {
                        textField.setEditable(true);
                        textField.requestFocus();
                    }
                });

                textField.setOnAction(event -> {
                    NoteData item = getItem();
                    if (item != null) {
                        item.setTitle(textField.getText());
                        commitEdit(item);
                        textField.setEditable(false);
                    }
                });

                textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue) {
                        NoteData item = getItem();
                        if (item != null) {
                            item.setTitle(textField.getText());
                            commitEdit(item);
                            textField.setEditable(false);
                        }
                    }
                });

                textField.setStyle(
                        "-fx-cursor: hand;"
                                + "-fx-font-size: 14px;"
                                + "-fx-text-fill: #000000;"
                                + "-fx-background-color: transparent;"
                                + "-fx-border-color: transparent;"
                                + "-fx-max-width: 120px;"
                                + "-fx-max-height: 50px;"
                );
            }

            @Override
            public void updateItem(NoteData item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    textField.setText(item.getTitle());
                    setGraphic(textField);
                }
            }
        });
    }
        private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
