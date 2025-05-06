package com.bookstore.app.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    private static String URL = "";
    private static String USER = "";
    private static String PASS = "";

    static {
        System.out.println(DBConnection.class.getClassLoader().getResourceAsStream("config/config.properties"));
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("config/config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            URL = prop.getProperty("db.url");
            USER = prop.getProperty("db.user");
            PASS = prop.getProperty("db.password");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
