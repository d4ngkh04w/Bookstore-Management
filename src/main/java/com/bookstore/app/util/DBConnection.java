package com.bookstore.app.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static String URL;
    private static String USER;
    private static String PASS;

    static {
        try {
            // Load properties from config file
            Properties prop = new Properties();
            InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("config/config.properties");
              if (input == null) {
                System.err.println("Unable to find config.properties!");
                // Can't use return in static block, just skip the rest of the initialization
                throw new RuntimeException("Config file not found");
            }
            
            // Load the properties file
            prop.load(input);
            
            // Get the database connection properties
            URL = prop.getProperty("db.url");
            USER = prop.getProperty("db.user");
            PASS = prop.getProperty("db.password");
            
            input.close();
            
            // Register JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error loading config.properties!");
            e.printStackTrace();
        }
    }    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Check if properties were loaded successfully
            if (URL == null || USER == null || PASS == null) {
                System.err.println("Database configuration not loaded properly!");
                return null;
            }
            
            connection = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("Database connection established successfully");
        } catch (SQLException e) {
            System.err.println("Connection Failed! Check output console");
            e.printStackTrace();
        }
        return connection;
    }
}
