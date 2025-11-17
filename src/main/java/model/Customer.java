package model;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    private String id;
    private String firstName;
    private String lastName;
    private String address;
    private String employerName;
    private String employerAddress;

    private List<Account> accounts = new ArrayList<>();

    public Customer(String id, String firstName, String lastName, String address) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
    }

    // Add an account to customer
    public void addAccount(Account account) {
        accounts.add(account);
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    // Getters
    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getAddress() { return address; }

    // Cheque account support methods
    public void setEmployer(String employerName, String employerAddress) {
        this.employerName = employerName;
        this.employerAddress = employerAddress;
    }

    public String getEmployerName() { return employerName; }
    public String getEmployerAddress() { return employerAddress; }
}

