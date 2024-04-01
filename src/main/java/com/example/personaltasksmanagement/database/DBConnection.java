package com.example.personaltasksmanagement.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection connectionDB() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(DBConfig.JDBC_URL, DBConfig.JDBC_USER, DBConfig.JDBC_PASSWORD);

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
