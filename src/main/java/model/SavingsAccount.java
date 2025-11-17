package model;

public class SavingsAccount extends Account {
    private static final double MONTHLY_INTEREST_RATE = 0.0005;

    public SavingsAccount(String accountNumber, double balance, 
                          String branch, Customer customer) {
        super(accountNumber, balance, branch, customer);
    }

    @Override
    public void deposit(double amount) {
        if (amount > 0) balance += amount;
    }

    @Override
    public boolean withdraw(double amount) {
        return false; // Savings accounts do NOT allow withdrawals
    }

    @Override
    public double calculateMonthlyInterest() {
        return balance * MONTHLY_INTEREST_RATE;
    }
}

