package model;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    private int customerID;
    private String firstName;
    private String lastName;
    private String address;
    private String contact;  // Changed from int to String
    private List<Account> accountList;

    public Customer() {
        this.accountList = new ArrayList<>();
    }

    // Updated constructor
    public Customer(int customerID, String firstName, String lastName, String address, String contact) {
        this();
        this.customerID = customerID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.contact = contact;
    }

    // Getters and Setters
    public int getCustomerID() { return customerID; }
    public void setCustomerID(int customerID) { this.customerID = customerID; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContact() { return contact; }  // Return type changed
    public void setContact(String contact) { this.contact = contact; }  // Parameter type changed

    public List<Account> getAccountList() { return accountList; }
    public void addAccount(Account account) { this.accountList.add(account); }

    @Override
    public String toString() {
        return String.format("Customer ID: %d, Name: %s %s, Contact: %s",
                customerID, firstName, lastName, contact);
    }
}