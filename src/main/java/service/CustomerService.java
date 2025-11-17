package service;

import dao.CustomerDAO;
import model.Customer;

import java.util.List;

public class CustomerService {

    private CustomerDAO customerDAO = new CustomerDAO();

    // Add a new customer
    public void addCustomer(Customer customer) {
        customerDAO.addCustomer(customer);
    }

    // Get customer by ID
    public Customer getCustomer(String id) {
        return customerDAO.getCustomer(id);
    }

    // List all customers
    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }

    // Delete customer
    public void deleteCustomer(String id) {
        customerDAO.deleteCustomer(id);
    }
}
