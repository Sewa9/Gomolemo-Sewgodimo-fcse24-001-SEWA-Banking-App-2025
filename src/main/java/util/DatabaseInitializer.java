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
            e.printStackTrace();
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        // Drop tables if they exist to ensure schema is up to date
        stmt.execute("DROP TABLE IF EXISTS transactions");
        stmt.execute("DROP TABLE IF EXISTS accounts");
        stmt.execute("DROP TABLE IF EXISTS customers");

        // Customers table
        stmt.execute("""
                    CREATE TABLE customers (
                        id TEXT PRIMARY KEY,
                        first_name TEXT NOT NULL,
                        last_name TEXT NOT NULL,
                        address TEXT NOT NULL,
                        employer_name TEXT,
                        employer_address TEXT
                    );
                """);

        // Accounts table
        stmt.execute("""
                    CREATE TABLE accounts (
                        accountNumber TEXT PRIMARY KEY,
                        type TEXT NOT NULL,
                        balance REAL NOT NULL,
                        branch TEXT NOT NULL,
                        customerId TEXT NOT NULL,
                        FOREIGN KEY(customerId) REFERENCES customers(id)
                    );
                """);

        // Transactions table
        stmt.execute("""
                    CREATE TABLE transactions (
                        transaction_id TEXT PRIMARY KEY,
                        account_number TEXT NOT NULL,
                        type TEXT NOT NULL,
                        amount REAL NOT NULL,
                        timestamp TEXT NOT NULL,
                        FOREIGN KEY(account_number) REFERENCES accounts(account_number)
                    );
                """);

        // Users table for authentication
        stmt.execute("""
                    CREATE TABLE users (
                        username TEXT PRIMARY KEY,
                        password TEXT NOT NULL,
                        role TEXT NOT NULL
                    );
                """);
    }

    private static void insertSampleData(Connection conn) throws SQLException {
        // Insert Customers
        PreparedStatement psCustomer = conn.prepareStatement(
                "INSERT OR IGNORE INTO customers (id, first_name, last_name, address) VALUES (?, ?, ?, ?)");

        psCustomer.setString(1, "C001");
        psCustomer.setString(2, "John");
        psCustomer.setString(3, "Doe");
        psCustomer.setString(4, "Gaborone");
        psCustomer.executeUpdate();

        psCustomer.setString(1, "C002");
        psCustomer.setString(2, "Jane");
        psCustomer.setString(3, "Smith");
        psCustomer.setString(4, "Francistown");
        psCustomer.executeUpdate();

        psCustomer.setString(1, "C003");
        psCustomer.setString(2, "Alice");
        psCustomer.setString(3, "Johnson");
        psCustomer.setString(4, "Maun");
        psCustomer.executeUpdate();

        // Insert Accounts
        PreparedStatement psAccount = conn.prepareStatement(
                "INSERT OR IGNORE INTO accounts (accountNumber, type, balance, branch, customerId) VALUES (?, ?, ?, ?, ?)");

        psAccount.setString(1, "S001");
        psAccount.setString(2, "SavingsAccount");
        psAccount.setDouble(3, 1000.0);
        psAccount.setString(4, "MainBranch");
        psAccount.setString(5, "C001");
        psAccount.executeUpdate();

        psAccount.setString(1, "CH001");
        psAccount.setString(2, "ChequingAccount");
        psAccount.setDouble(3, 500.0);
        psAccount.setString(4, "MainBranch");
        psAccount.setString(5, "C001");
        psAccount.executeUpdate();

        psAccount.setString(1, "I001");
        psAccount.setString(2, "InvestmentAccount");
        psAccount.setDouble(3, 5000.0);
        psAccount.setString(4, "MainBranch");
        psAccount.setString(5, "C002");
        psAccount.executeUpdate();

        psAccount.setString(1, "S002");
        psAccount.setString(2, "SavingsAccount");
        psAccount.setDouble(3, 2000.0);
        psAccount.setString(4, "WestBranch");
        psAccount.setString(5, "C003");
        psAccount.executeUpdate();

        // Insert Transactions
        PreparedStatement psTxn = conn.prepareStatement(
                "INSERT OR IGNORE INTO transactions (transaction_id, account_number, type, amount, timestamp) VALUES (?, ?, ?, ?, ?)");

        psTxn.setString(1, "T001");
        psTxn.setString(2, "S001");
        psTxn.setString(3, "Deposit");
        psTxn.setDouble(4, 1000.0);
        psTxn.setString(5, "2025-11-17 09:00:00");
        psTxn.executeUpdate();

        psTxn.setString(1, "T002");
        psTxn.setString(2, "CH001");
        psTxn.setString(3, "Deposit");
        psTxn.setDouble(4, 500.0);
        psTxn.setString(5, "2025-11-17 09:05:00");
        psTxn.executeUpdate();

        psTxn.setString(1, "T003");
        psTxn.setString(2, "I001");
        psTxn.setString(3, "Deposit");
        psTxn.setDouble(4, 5000.0);
        psTxn.setString(5, "2025-11-17 09:10:00");
        psTxn.executeUpdate();

        psTxn.setString(1, "T004");
        psTxn.setString(2, "S001");
        psTxn.setString(3, "Withdrawal");
        psTxn.setDouble(4, 200.0);
        psTxn.setString(5, "2025-11-17 09:15:00");
        psTxn.executeUpdate();

        // Insert Users
        PreparedStatement psUser = conn.prepareStatement(
                "INSERT OR IGNORE INTO users (username, password, role) VALUES (?, ?, ?)");

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

        // Customer users - one for each sample customer
        psUser.setString(1, "C001");
        psUser.setString(2, "pass123");
        psUser.setString(3, "customer");
        psUser.executeUpdate();

        psUser.setString(1, "C002");
        psUser.setString(2, "pass123");
        psUser.setString(3, "customer");
        psUser.executeUpdate();

        psUser.setString(1, "C003");
        psUser.setString(2, "pass123");
        psUser.setString(3, "customer");
        psUser.executeUpdate();
    }
}
