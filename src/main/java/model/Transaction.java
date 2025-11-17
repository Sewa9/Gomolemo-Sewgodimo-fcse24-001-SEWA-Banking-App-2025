package model;

import java.time.LocalDateTime;

public class Transaction {
    private String transactionId;
    private String accountNumber;
    private double amount;
    private LocalDateTime timestamp;
    private String type; // Deposit, Withdrawal

    public Transaction(String transactionId, String accountNumber, 
                       double amount, String type) {
        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }

    public String getTransactionId() { return transactionId; }
    public String getAccountNumber() { return accountNumber; }
    public double getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getType() { return type; }
}

