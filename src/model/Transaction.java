package model;

import java.util.Date;

public class Transaction {
    private int transactionID;
    private Date date;
    private double amount;
    private String type;
    private Account account;

    public Transaction(int transactionID, Date date, double amount, String type, Account account) {
        this.transactionID = transactionID;
        this.date = date;
        this.amount = amount;
        this.type = type;
        this.account = account;
    }

    // Getters and Setters
    public int getTransactionID() { return transactionID; }
    public void setTransactionID(int transactionID) { this.transactionID = transactionID; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }

    // Business methods
    public void recordTransaction() {
        // Implementation for recording transaction
        System.out.println("Transaction recorded: " + type + " - $" + amount);
    }

    public void viewHistory() {
        // Implementation for viewing transaction history
        System.out.println("Transaction History - ID: " + transactionID +
                ", Date: " + date + ", Type: " + type + ", Amount: $" + amount);
    }
}