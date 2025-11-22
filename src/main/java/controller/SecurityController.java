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
        users.clear();
        roles.clear();
        
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
                    System.out.println("DEBUG: Loaded user - " + username + " with role: " + role + " and password: " + password);
                }
                System.out.println("DEBUG: Total users loaded: " + users.size());
            }
        } catch (SQLException e) {
            System.err.println("ERROR loading users from database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean login(String username, String password) {
        if (username == null || password == null) {
            System.out.println("DEBUG: Login failed - null username or password");
            return false;
        }
        
        // Reload users to ensure we have the latest data
        loadUsersFromDatabase();
        
        boolean userExists = users.containsKey(username);
        boolean passwordMatches = userExists && users.get(username).equals(password);
        
        System.out.println("DEBUG: Login attempt - Username: " + username);
        System.out.println("DEBUG: User exists: " + userExists);
        System.out.println("DEBUG: Password matches: " + passwordMatches);
        System.out.println("DEBUG: Stored password for " + username + ": " + (userExists ? users.get(username) : "N/A"));
        System.out.println("DEBUG: Provided password: " + password);
        
        return passwordMatches;
    }

    public String getUserRole(String username) {
        return roles.get(username);
    }

    public void registerUser(String username, String password, String role) {
        if (username == null || password == null || role == null) {
            System.out.println("ERROR: Cannot register user with null parameters");
            return;
        }
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            if (conn != null) {
                // Use INSERT OR REPLACE to handle duplicates
                PreparedStatement ps = conn
                        .prepareStatement("INSERT OR REPLACE INTO users (username, password, role) VALUES (?, ?, ?)");
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, role);
                int rowsAffected = ps.executeUpdate();
                
                if (rowsAffected > 0) {
                    users.put(username, password);
                    roles.put(username, role);
                    System.out.println("DEBUG: Successfully registered user - " + username + " with role: " + role + " and password: " + password);
                } else {
                    System.out.println("DEBUG: Failed to register user - no rows affected");
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR registering user: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Reload users to ensure cache is updated
        loadUsersFromDatabase();
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
                    System.out.println("DEBUG: Password changed for user: " + username);
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR changing password: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public Map<String, String> getAllUsers() {
        return new HashMap<>(users);
    }
}