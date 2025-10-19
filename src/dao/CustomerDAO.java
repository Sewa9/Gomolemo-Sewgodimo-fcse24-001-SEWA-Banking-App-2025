package dao;

import model.Customer;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public boolean addCustomer(Customer customer) {
        String sql = "INSERT INTO customers (first_name, last_name, address, contact) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getLastName());
            pstmt.setString(3, customer.getAddress());
            pstmt.setString(4, customer.getContact());  // Now using String

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        customer.setCustomerID(generatedKeys.getInt(1));
                    }
                }
                System.out.println("✅ Customer added successfully with ID: " + customer.getCustomerID());
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error adding customer: " + e.getMessage());
        }
        return false;
    }

    public Customer getCustomer(int customerID) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        Customer customer = null;

        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                customer = new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("address"),
                        rs.getString("contact")  // Direct String retrieval
                );
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting customer: " + e.getMessage());
        }
        return customer;
    }

    // getAllCustomers, updateCustomer, deleteCustomer methods remain similar but use String contact
}