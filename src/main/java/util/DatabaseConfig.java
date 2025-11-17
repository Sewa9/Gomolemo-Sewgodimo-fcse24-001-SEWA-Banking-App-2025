package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {

    // SQLite database URL
    private static final String DB_URL = "jdbc:sqlite:bank.db";

    // Optional: username/password if you switch to MySQL/PostgreSQL
    private static final String DB_USERNAME = "";
    private static final String DB_PASSWORD = "";

    // Private constructor to prevent instantiation
    private DatabaseConfig() { }

    /**
     * Get a database connection.
     * 
     * @return Connection object
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Load SQLite JDBC driver (optional for modern JDBC)
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found.");
            e.printStackTrace();
        }

        return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    }

    /**
     * Test database connection
     */
    public static void testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("Database connected successfully!");
            } else {
                System.out.println("Failed to connect to database.");
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
    }
}

