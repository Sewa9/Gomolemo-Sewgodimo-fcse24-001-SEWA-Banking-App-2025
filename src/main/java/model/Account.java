package model;

public abstract class Account {
    protected String accountNumber;
    protected double balance;
    protected String branch;
    protected Customer customer;   // Composition (Account belongs to Customer)

    public Account(String accountNumber, double balance, String branch, Customer customer) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.branch = branch;
        this.customer = customer;
    }

    // Overloaded constructors
    public Account(String accountNumber, String branch, Customer customer) {
        this(accountNumber, 0.0, branch, customer);
    }

    // Abstract methods → MUST be implemented by subclasses
    public abstract void deposit(double amount);
    public abstract boolean withdraw(double amount);

    // Getters
    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    public String getBranch() { return branch; }
    public Customer getCustomer() { return customer; }

    // Template method for interest → implemented by child classes
    public abstract double calculateMonthlyInterest();
}

