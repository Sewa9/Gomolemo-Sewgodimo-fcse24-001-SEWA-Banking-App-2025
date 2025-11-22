package dao;

import model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    public void addAccount(Account account) {
        String sql = "INSERT OR REPLACE INTO accounts (accountNumber, balance, branch, customerId, type) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, account.getAccountNumber());
            stmt.setDouble(2, account.getBalance());
            stmt.setString(3, account.getBranch());
            stmt.setString(4, account.getCustomer().getId());
            stmt.setString(5, account.getClass().getSimpleName());

            stmt.executeUpdate();
            System.out.println("DEBUG: Account " + account.getAccountNumber() + " added for customer " + account.getCustomer().getId());

        } catch (SQLException e) {
            System.err.println("ERROR adding account: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Account getAccount(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE accountNumber = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return createAccountFromResult(rs);
            }

        } catch (SQLException e) {
            System.err.println("ERROR getting account: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private Account createAccountFromResult(ResultSet rs) throws SQLException {
        String type = rs.getString("type");
        String accNum = rs.getString("accountNumber");
        double balance = rs.getDouble("balance");
        String branch = rs.getString("branch");
        String customerId = rs.getString("customerId");

        System.out.println("DEBUG: Creating account " + accNum + " for customer " + customerId + " type: " + type);

        CustomerDAO custDAO = new CustomerDAO();
        Customer customer = custDAO.getCustomer(customerId);

        // FIX: Create a minimal customer if not found in database
        if (customer == null) {
            System.out.println("DEBUG: Customer " + customerId + " not found, creating minimal customer");
            customer = new Customer(customerId, "Unknown", "Customer", "Address not set");
        }

        switch (type) {
            case "SavingsAccount":
                return new SavingsAccount(accNum, balance, branch, customer);

            case "ChequingAccount":
                return new ChequingAccount(accNum, balance, branch, customer);

            case "InvestmentAccount":
                return new InvestmentAccount(accNum, balance, branch, customer);

            default:
                System.out.println("DEBUG: Unknown account type: " + type);
                return null;
        }
    }

    public List<Account> getCustomerAccounts(String customerId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customerId = ?";

        System.out.println("DEBUG: Getting accounts for customer: " + customerId);

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Account account = createAccountFromResult(rs);
                if (account != null) {
                    accounts.add(account);
                    System.out.println("DEBUG: Added account " + account.getAccountNumber() + " to customer " + customerId);
                }
            }

        } catch (SQLException e) {
            System.err.println("ERROR getting customer accounts: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("DEBUG: Total accounts found for customer " + customerId + ": " + accounts.size());
        return accounts;
    }

    public void updateBalance(String accountNumber, double newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE accountNumber = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, newBalance);
            stmt.setString(2, accountNumber);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("ERROR updating balance: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Account account = createAccountFromResult(rs);
                if (account != null) {
                    accounts.add(account);
                }
            }

        } catch (SQLException e) {
            System.err.println("ERROR getting all accounts: " + e.getMessage());
            e.printStackTrace();
        }
        return accounts;
    }

    public void deleteAccount(String accountNumber) {
        String sql = "DELETE FROM accounts WHERE accountNumber = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("ERROR deleting account: " + e.getMessage());
            e.printStackTrace();
        }
    }
}