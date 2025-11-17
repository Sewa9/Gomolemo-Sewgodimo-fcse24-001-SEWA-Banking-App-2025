package model;

public class InvestmentAccount extends Account {
    private static final double MONTHLY_INTEREST_RATE = 0.05;

    public InvestmentAccount(String accountNumber, double balance,
                             String branch, Customer customer) {
        super(accountNumber, balance, branch, customer);
        
        if (balance < 500.00) {
            throw new IllegalArgumentException("Investment account requires minimum BWP500.");
        }
    }

    @Override
    public void deposit(double amount) {
        if (amount > 0) balance += amount;
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount > 0 && balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }

    @Override
    public double calculateMonthlyInterest() {
        return balance * MONTHLY_INTEREST_RATE;
    }
}

