package org.hamium.hammer.utils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteDataSource {
    private static Connection connection;

    private SQLiteDataSource() {} // Private constructor to prevent instantiation

    public static Connection getConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return connection;
        }

        File dataFolder = new File("plugins/Hammer");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File databaseFile = new File(dataFolder, "data.db");

        String jdbcUrl = "jdbc:sqlite:" + databaseFile.getAbsolutePath();

        connection = DriverManager.getConnection(jdbcUrl);

        createTables(); // Create tables if they don't exist

        return connection;
    }

    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private static void createTables() {
        try (Statement statement = connection.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS homes ("
                    + "uuid TEXT PRIMARY KEY,"
                    + "player TEXT,"
                    + "world TEXT,"
                    + "x REAL,"
                    + "y REAL,"
                    + "z REAL"
                    + ")";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
