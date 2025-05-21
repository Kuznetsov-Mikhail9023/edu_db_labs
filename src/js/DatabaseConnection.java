package com.example.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Клас для отримання з'єднання з базою даних MySQL.
 * Забезпечує єдине повторно використовуване підключення.
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/course?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root"; // Замінити на свій логін
    private static final String PASSWORD = "your_password_here"; // Замінити на свій пароль

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }
}
