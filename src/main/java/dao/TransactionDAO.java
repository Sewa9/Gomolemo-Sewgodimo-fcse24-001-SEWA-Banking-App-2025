package dao;

import model.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public void addTransaction(Transaction t) {
        String sql = "INSERT INTO transactions (transaction_id, account_number, type, amount, timestamp) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, t.getTransactionId());
            stmt.setString(2, t.getAccountNumber());
            stmt.setString(3, t.getType());
            stmt.setDouble(4, t.getAmount());
            stmt.setString(5, t.getTimestamp().toString());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Transaction> getAccountTransactions(String accountNumber) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_number = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Transaction t = new Transaction(
                        rs.getString("transaction_id"),
                        rs.getString("account_number"),
                        rs.getDouble("amount"),
                        rs.getString("type"));
                list.add(t);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
