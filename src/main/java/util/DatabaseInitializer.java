package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initializeDatabase() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn != null) {
                createTables(conn);
                insertSampleData(conn);
                System.out.println("Database initialized successfully!");
            }
        } catch (SQLException e) {
            // If tables already exist, just continue
            System.out.println("Database already initialized: " + e.getMessage());
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        // Use IF NOT EXISTS to avoid errors if tables already exist
        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS customers (
                        id TEXT PRIMARY KEY,
                        first_name TEXT NOT NULL,
                        last_name TEXT NOT NULL,
                        address TEXT NOT NULL,
                        employer_name TEXT,
                        employer_address TEXT
                    );
                """);

        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS accounts (
                        accountNumber TEXT PRIMARY KEY,
                        type TEXT NOT NULL,
                        balance REAL NOT NULL,
                        branch TEXT NOT NULL,
                        customerId TEXT NOT NULL,
                        FOREIGN KEY(customerId) REFERENCES customers(id)
                    );
                """);

        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS transactions (
                        transaction_id TEXT PRIMARY KEY,
                        account_number TEXT NOT NULL,
                        type TEXT NOT NULL,
                        amount REAL NOT NULL,
                        timestamp TEXT NOT NULL,
                        FOREIGN KEY(account_number) REFERENCES accounts(account_number)
                    );
                """);

        stmt.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        username TEXT PRIMARY KEY,
                        password TEXT NOT NULL,
                        role TEXT NOT NULL
                    );
                """);
    }

    private static void insertSampleData(Connection conn) throws SQLException {
        // Insert Customers - use INSERT OR IGNORE to avoid duplicates
        PreparedStatement psCustomer = conn.prepareStatement(
                "INSERT OR IGNORE INTO customers (id, first_name, last_name, address) VALUES (?, ?, ?, ?)");

        // Sample customers
        String[][] sampleCustomers = {
                { "C001", "John", "Doe", "Gaborone" },
                { "C002", "Jane", "Smith", "Francistown" },
                { "C003", "Alice", "Johnson", "Maun" },
                { "gsewa", "Gomolemo", "Sewa", "Gaborone" },
                { "kagos", "Kagiso", "Moloi", "Francistown" },
                { "sdelu", "Sandra", "Delu", "Maun" }
        };

        for (String[] customer : sampleCustomers) {
            psCustomer.setString(1, customer[0]);
            psCustomer.setString(2, customer[1]);
            psCustomer.setString(3, customer[2]);
            psCustomer.setString(4, customer[3]);
            psCustomer.executeUpdate();
        }

        // Insert Accounts - use INSERT OR IGNORE to avoid duplicates
        PreparedStatement psAccount = conn.prepareStatement(
                "INSERT OR IGNORE INTO accounts (accountNumber, type, balance, branch, customerId) VALUES (?, ?, ?, ?, ?)");

        // Sample accounts
        String[][] sampleAccounts = {
                { "S001", "SavingsAccount", "1000.0", "MainBranch", "C001" },
                { "CH001", "ChequingAccount", "500.0", "MainBranch", "C001" },
                { "I001", "InvestmentAccount", "5000.0", "MainBranch", "C002" },
                { "S002", "SavingsAccount", "2000.0", "WestBranch", "C003" },
                { "S378848", "SavingsAccount", "900.0", "MainBranch", "gsewa" },
                { "C123456", "ChequingAccount", "1500.0", "MainBranch", "kagos" },
                { "S789012", "SavingsAccount", "2500.0", "MainBranch", "sdelu" }
        };

        for (String[] account : sampleAccounts) {
            psAccount.setString(1, account[0]);
            psAccount.setString(2, account[1]);
            psAccount.setDouble(3, Double.parseDouble(account[2]));
            psAccount.setString(4, account[3]);
            psAccount.setString(5, account[4]);
            psAccount.executeUpdate();
        }

        // Insert Users - use INSERT OR IGNORE to avoid duplicates
        PreparedStatement psUser = conn.prepareStatement(
                "INSERT OR REPLACE INTO users (username, password, role) VALUES (?, ?, ?)");

        // Admin users
        psUser.setString(1, "admin");
        psUser.setString(2, "admin123");
        psUser.setString(3, "admin");
        psUser.executeUpdate();

        psUser.setString(1, "admin2");
        psUser.setString(2, "admin456");
        psUser.setString(3, "admin");
        psUser.executeUpdate();

        // Employee users
        psUser.setString(1, "employee");
        psUser.setString(2, "emp123");
        psUser.setString(3, "employee");
        psUser.executeUpdate();

        psUser.setString(1, "employee2");
        psUser.setString(2, "emp456");
        psUser.setString(3, "employee");
        psUser.executeUpdate();

        // Customer users
        String[][] customerUsers = {
                { "C001", "pass123", "customer" },
                { "C002", "pass123", "customer" },
                { "C003", "pass123", "customer" },
                { "gsewa", "pass123", "customer" },
                { "kagos", "pass123", "customer" },
                { "sdelu", "pass123", "customer" }
        };

        for (String[] user : customerUsers) {
            psUser.setString(1, user[0]);
            psUser.setString(2, user[1]);
            psUser.setString(3, user[2]);
            psUser.executeUpdate();
        }

        System.out.println("Sample data inserted successfully!");
    }
}