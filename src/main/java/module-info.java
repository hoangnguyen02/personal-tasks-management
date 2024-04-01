module com.example.personaltasksmanagement {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;
    requires java.mail;
    requires de.jensd.fx.glyphs.fontawesome;


    opens com.example.personaltasksmanagement to javafx.fxml;
    exports com.example.personaltasksmanagement;
    exports com.example.personaltasksmanagement.controllers;
    opens com.example.personaltasksmanagement.controllers to javafx.fxml;
    opens com.example.personaltasksmanagement.models to javafx.base;
}