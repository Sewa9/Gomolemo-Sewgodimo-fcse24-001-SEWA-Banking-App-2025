package dao;

import model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public void addCustomer(Customer customer) {
        String sql = "INSERT INTO customers (id, first_name, last_name, address, employer_name, employer_address) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getId());
            stmt.setString(2, customer.getFirstName());
            stmt.setString(3, customer.getLastName());
            stmt.setString(4, customer.getAddress());
            stmt.setString(5, customer.getEmployerName());
            stmt.setString(6, customer.getEmployerAddress());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Customer getCustomer(String id) {
        String sql = "SELECT * FROM customers WHERE id = ?";
        Customer customer = null;

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                customer = new Customer(
                        rs.getString("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("address"));
                customer.setEmployer(
                        rs.getString("employer_name"),
                        rs.getString("employer_address"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customer;
    }

    public List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers";

        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Customer c = new Customer(
                        rs.getString("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("address"));
                c.setEmployer(
                        rs.getString("employer_name"),
                        rs.getString("employer_address"));

                list.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void deleteCustomer(String id) {
        String sql = "DELETE FROM customers WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
