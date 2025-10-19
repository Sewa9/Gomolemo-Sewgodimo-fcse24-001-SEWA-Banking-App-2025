package dao;

import model.*;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AccountDAO {

    // Method to generate unique account numbers
    private String generateAccountNumber(String accountType) {
        String prefix = "";
        switch (accountType.toUpperCase()) {
            case "SAVINGS": prefix = "SAV"; break;
            case "INVESTMENT": prefix = "INV"; break;
            case "CHEQUING": prefix = "CHQ"; break;
            default: prefix = "ACC";
        }

        Random random = new Random();
        String accountNumber;
        do {
            int number = 100000 + random.nextInt(900000); // Generates 6-digit number
            accountNumber = prefix + number;
        } while (accountExists(accountNumber));

        return accountNumber;
    }

    private boolean accountExists(String accountNumber) {
        String sql = "SELECT COUNT(*) FROM accounts WHERE account_number = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error checking account existence: " + e.getMessage());
            return false;
        }
    }

    public boolean addAccount(Account account) {
        // Generate unique account number if not provided
        if (account.getAccountNumber() == null || account.getAccountNumber().isEmpty()) {
            String accountType = "";
            if (account instanceof SavingsAccount) accountType = "SAVINGS";
            else if (account instanceof InvestmentAccount) accountType = "INVESTMENT";
            else if (account instanceof ChequingAccount) accountType = "CHEQUING";

            String generatedNumber = generateAccountNumber(accountType);
            account.setAccountNumber(generatedNumber);
        }

        String sql = "INSERT INTO accounts (account_number, balance, branch, customer_id, account_type, " +
                "interest_rate, minimum_deposit, company_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, account.getAccountNumber());
            pstmt.setDouble(2, account.getBalance());
            pstmt.setString(3, account.getBranch());
            pstmt.setInt(4, account.getCustomer().getCustomerID());

            // Set account type specific fields
            if (account instanceof SavingsAccount) {
                SavingsAccount sa = (SavingsAccount) account;
                pstmt.setString(5, "SAVINGS");
                pstmt.setDouble(6, sa.getInterestRate());
                pstmt.setNull(7, Types.DECIMAL);
                pstmt.setNull(8, Types.VARCHAR);
            } else if (account instanceof InvestmentAccount) {
                InvestmentAccount ia = (InvestmentAccount) account;
                pstmt.setString(5, "INVESTMENT");
                pstmt.setDouble(6, ia.getInterestRate());
                pstmt.setDouble(7, ia.getMinimumInitialDeposit());
                pstmt.setNull(8, Types.VARCHAR);
            } else if (account instanceof ChequingAccount) {
                ChequingAccount ca = (ChequingAccount) account;
                pstmt.setString(5, "CHEQUING");
                pstmt.setNull(6, Types.DECIMAL);
                pstmt.setNull(7, Types.DECIMAL);
                pstmt.setString(8, ca.getCompanyName());
            } else {
                // Default for generic Account
                pstmt.setString(5, "GENERIC");
                pstmt.setNull(6, Types.DECIMAL);
                pstmt.setNull(7, Types.DECIMAL);
                pstmt.setNull(8, Types.VARCHAR);
            }

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("✅ Account added successfully: " + account.getAccountNumber());
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error adding account: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public Account getAccount(String accountNumber) {
        String sql = "SELECT a.*, c.customer_id, c.first_name, c.last_name, c.address, c.contact " +
                "FROM accounts a JOIN customers c ON a.customer_id = c.customer_id WHERE a.account_number = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return createAccountFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting account: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private Account createAccountFromResultSet(ResultSet rs) throws SQLException {
        // Create customer from result set
        Customer customer = new Customer(
                rs.getInt("customer_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("address"),
                rs.getString("contact")
        );

        String accountType = rs.getString("account_type");
        String accountNumber = rs.getString("account_number");
        double balance = rs.getDouble("balance");
        String branch = rs.getString("branch");

        switch (accountType) {
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
                System.err.println("❌ Unknown account type: " + accountType);
                return null;
        }
    }

    public List<Account> getAccountsByCustomer(int customerId) {
        String sql = "SELECT a.*, c.customer_id, c.first_name, c.last_name, c.address, c.contact " +
                "FROM accounts a JOIN customers c ON a.customer_id = c.customer_id WHERE a.customer_id = ?";
        List<Account> accounts = new ArrayList<>();

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Account account = createAccountFromResultSet(rs);
                if (account != null) {
                    accounts.add(account);
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting customer accounts: " + e.getMessage());
            e.printStackTrace();
        }
        return accounts;
    }

    public boolean updateAccountBalance(String accountNumber, double newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, newBalance);
            pstmt.setString(2, accountNumber);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("✅ Account balance updated successfully for: " + accountNumber);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error updating account balance: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteAccount(String accountNumber) {
        String sql = "DELETE FROM accounts WHERE account_number = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, accountNumber);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("✅ Account deleted successfully: " + accountNumber);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error deleting account: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean accountExistsForCustomer(int customerId, String accountType) {
        String sql = "SELECT COUNT(*) FROM accounts WHERE customer_id = ? AND account_type = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            pstmt.setString(2, accountType.toUpperCase());
            ResultSet rs = pstmt.executeQuery();

            return rs.next() && rs.getInt(1) > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error checking account existence for customer: " + e.getMessage());
            return false;
        }
    }
}