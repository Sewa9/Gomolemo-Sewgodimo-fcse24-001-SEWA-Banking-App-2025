package controller;

import model.Customer;
import service.CustomerService;

import java.util.List;

public class CustomerController {

    private CustomerService customerService = new CustomerService();

    public void addCustomer(Customer customer) {
        customerService.addCustomer(customer);
    }

    public Customer getCustomer(String id) {
        return customerService.getCustomer(id);
    }

    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    public void deleteCustomer(String id) {
        customerService.deleteCustomer(id);
    }
}

