package controller;

import model.Customer;
import util.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SecurityController {

    // In-memory cache for users loaded from database
    private Map<String, String> users = new HashMap<>();
    private Map<String, String> roles = new HashMap<>();

    public SecurityController() {
        loadUsersFromDatabase();
    }

    private void loadUsersFromDatabase() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn != null) {
                PreparedStatement ps = conn.prepareStatement("SELECT username, password, role FROM users");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    String role = rs.getString("role");
                    users.put(username, password);
                    roles.put(username, role);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean login(String username, String password) {
        return users.containsKey(username) && users.get(username).equals(password);
    }

    public String getUserRole(String username) {
        return roles.get(username);
    }

    public void registerUser(String username, String password, String role) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn != null) {
                PreparedStatement ps = conn
                        .prepareStatement("INSERT INTO users (username, password, role) VALUES (?, ?, ?)");
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, role);
                ps.executeUpdate();
                users.put(username, password);
                roles.put(username, role);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean changePassword(String username, String newPassword) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn != null) {
                PreparedStatement ps = conn.prepareStatement("UPDATE users SET password = ? WHERE username = ?");
                ps.setString(1, newPassword);
                ps.setString(2, username);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    users.put(username, newPassword);
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Map<String, String> getAllUsers() {
        return new HashMap<>(users);
    }
}
