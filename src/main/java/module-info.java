module com.example.personaltasksmanagement {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;
    requires java.mail;
    requires de.jensd.fx.glyphs.fontawesome;
    requires com.google.api.client.auth;
    requires google.api.client;
    requires com.google.api.client.json.gson;
    requires com.google.api.client;
    requires spring.beans;
    requires spring.context;
    requires spring.web;


    opens com.example.personaltasksmanagement to javafx.fxml;
    exports com.example.personaltasksmanagement;
    exports com.example.personaltasksmanagement.controllers;
    opens com.example.personaltasksmanagement.controllers to javafx.fxml;
    opens com.example.personaltasksmanagement.models to javafx.base;
}