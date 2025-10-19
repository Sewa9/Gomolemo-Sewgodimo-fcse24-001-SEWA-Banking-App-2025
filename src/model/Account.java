package model;

public abstract class Account {
    private String accountNumber;
    private double balance;
    private String branch;
    private Customer customer;

    public Account(String accountNumber, double balance, String branch, Customer customer) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.branch = branch;
        this.customer = customer;
    }

    // Getters and Setters
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    // Abstract methods
    public abstract double calculateInterest();

    // Concrete methods
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }

    public boolean transfer(Account toAccount, double amount) {
        if (withdraw(amount)) {
            toAccount.deposit(amount);
            return true;
        }
        return false;
    }
}