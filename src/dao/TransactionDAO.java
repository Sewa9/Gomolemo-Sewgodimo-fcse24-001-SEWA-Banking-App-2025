package dao;

import model.Transaction;
import model.Account;
import model.Customer;
import model.SavingsAccount;
import model.InvestmentAccount;
import model.ChequingAccount;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public boolean addTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (amount, type, account_number, description, transaction_date) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setDouble(1, transaction.getAmount());
            pstmt.setString(2, transaction.getType());
            pstmt.setString(3, transaction.getAccount().getAccountNumber());
            pstmt.setString(4, "Transaction: " + transaction.getType());

            // Handle transaction date - use current time if not provided
            if (transaction.getDate() != null) {
                pstmt.setTimestamp(5, new Timestamp(transaction.getDate().getTime()));
            } else {
                pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            }

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        transaction.setTransactionID(generatedKeys.getInt(1));
                    }
                }
                System.out.println("✅ Transaction recorded successfully with ID: " + transaction.getTransactionID());
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error adding transaction: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public Transaction getTransaction(int transactionID) {
        String sql = "SELECT t.*, a.account_number, a.balance, a.branch, a.account_type, " +
                "a.interest_rate, a.minimum_deposit, a.company_name, " +
                "c.customer_id, c.first_name, c.last_name, c.address, c.contact " +
                "FROM transactions t " +
                "JOIN accounts a ON t.account_number = a.account_number " +
                "JOIN customers c ON a.customer_id = c.customer_id " +
                "WHERE t.transaction_id = ?";

        Transaction transaction = null;

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, transactionID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Create Customer with complete information
                Customer customer = new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("address"),
                        rs.getString("contact")  // Fixed: using String contact
                );

                // Create proper Account object based on account type
                Account account = createAccountFromResultSet(rs, customer);

                transaction = new Transaction(
                        rs.getInt("transaction_id"),
                        rs.getTimestamp("transaction_date"),
                        rs.getDouble("amount"),
                        rs.getString("type"),
                        account
                );
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting transaction: " + e.getMessage());
            e.printStackTrace();
        }

        return transaction;
    }

    private Account createAccountFromResultSet(ResultSet rs, Customer customer) throws SQLException {
        String accountType = rs.getString("account_type");
        String accountNumber = rs.getString("account_number");
        double balance = rs.getDouble("balance");
        String branch = rs.getString("branch");

        switch (accountType != null ? accountType : "GENERIC") {
            case "SAVINGS":
                SavingsAccount savings = new SavingsAccount(accountNumber, balance, branch, customer);
                savings.setInterestRate(rs.getDouble("interest_rate"));
                return savings;

            case "INVESTMENT":
                InvestmentAccount investment = new InvestmentAccount(accountNumber, balance, branch, customer);
                investment.setInterestRate(rs.getDouble("interest_rate"));
                investment.setMinimumInitialDeposit(rs.getDouble("minimum_deposit"));
                return investment;

            case "CHEQUING":
                ChequingAccount chequing = new ChequingAccount(accountNumber, balance, branch, customer,
                        rs.getString("company_name"));
                return chequing;

            default:
                // Return a generic account for transactions if type is unknown
                return new Account(accountNumber, balance, branch, customer) {
                    @Override
                    public double calculateInterest() {
                        return 0;
                    }
                };
        }
    }

    public List<Transaction> getTransactionsByAccount(String accountNumber) {
        String sql = "SELECT t.*, a.balance, a.branch, a.account_type, " +
                "a.interest_rate, a.minimum_deposit, a.company_name, " +
                "c.customer_id, c.first_name, c.last_name, c.address, c.contact " +
                "FROM transactions t " +
                "JOIN accounts a ON t.account_number = a.account_number " +
                "JOIN customers c ON a.customer_id = c.customer_id " +
                "WHERE t.account_number = ? ORDER BY t.transaction_date DESC";

        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Create Customer with complete information
                Customer customer = new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("address"),
                        rs.getString("contact")
                );

                // Create proper Account object
                Account account = createAccountFromResultSet(rs, customer);

                Transaction transaction = new Transaction(
                        rs.getInt("transaction_id"),
                        rs.getTimestamp("transaction_date"),
                        rs.getDouble("amount"),
                        rs.getString("type"),
                        account
                );
                transactions.add(transaction);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting account transactions: " + e.getMessage());
            e.printStackTrace();
        }

        return transactions;
    }

    public List<Transaction> getTransactionsByCustomer(int customerId) {
        String sql = "SELECT t.*, a.account_number, a.balance, a.branch, a.account_type, " +
                "a.interest_rate, a.minimum_deposit, a.company_name, " +
                "c.customer_id, c.first_name, c.last_name, c.address, c.contact " +
                "FROM transactions t " +
                "JOIN accounts a ON t.account_number = a.account_number " +
                "JOIN customers c ON a.customer_id = c.customer_id " +
                "WHERE c.customer_id = ? ORDER BY t.transaction_date DESC";

        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Create Customer
                Customer customer = new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("address"),
                        rs.getString("contact")
                );

                // Create proper Account object
                Account account = createAccountFromResultSet(rs, customer);

                Transaction transaction = new Transaction(
                        rs.getInt("transaction_id"),
                        rs.getTimestamp("transaction_date"),
                        rs.getDouble("amount"),
                        rs.getString("type"),
                        account
                );
                transactions.add(transaction);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting customer transactions: " + e.getMessage());
            e.printStackTrace();
        }

        return transactions;
    }

    public boolean deleteTransaction(int transactionID) {
        String sql = "DELETE FROM transactions WHERE transaction_id = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, transactionID);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("✅ Transaction deleted successfully");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error deleting transaction: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Utility methods for common transaction operations
    public boolean recordDeposit(String accountNumber, double amount, String description) {
        return recordTransaction(accountNumber, amount, "DEPOSIT", description);
    }

    public boolean recordWithdrawal(String accountNumber, double amount, String description) {
        return recordTransaction(accountNumber, amount, "WITHDRAWAL", description);
    }

    public boolean recordTransfer(String fromAccount, String toAccount, double amount, String description) {
        // Record withdrawal from source account
        boolean success1 = recordTransaction(fromAccount, amount, "TRANSFER_OUT",
                description + " (To: " + toAccount + ")");
        // Record deposit to target account
        boolean success2 = recordTransaction(toAccount, amount, "TRANSFER_IN",
                description + " (From: " + fromAccount + ")");
        return success1 && success2;
    }

    private boolean recordTransaction(String accountNumber, double amount, String type, String description) {
        String sql = "INSERT INTO transactions (amount, type, account_number, description) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, amount);
            pstmt.setString(2, type);
            pstmt.setString(3, accountNumber);
            pstmt.setString(4, description);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("✅ " + type + " transaction recorded for account: " + accountNumber);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error recording " + type + " transaction: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public double getTotalDepositsByAccount(String accountNumber) {
        String sql = "SELECT COALESCE(SUM(amount), 0) as total_deposits " +
                "FROM transactions WHERE account_number = ? AND type = 'DEPOSIT'";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total_deposits");
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting total deposits: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }

    public double getTotalWithdrawalsByAccount(String accountNumber) {
        String sql = "SELECT COALESCE(SUM(amount), 0) as total_withdrawals " +
                "FROM transactions WHERE account_number = ? AND type = 'WITHDRAWAL'";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total_withdrawals");
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting total withdrawals: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }
}