package model;

public class InvestmentAccount extends Account {
    private double interestRate = 0.05;
    private double minimumInitialDeposit = 500.0;

    public InvestmentAccount(String accountNumber, double balance, String branch, Customer customer) {
        super(accountNumber, balance, branch, customer);
    }

    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }

    public double getMinimumInitialDeposit() { return minimumInitialDeposit; }
    public void setMinimumInitialDeposit(double amount) { this.minimumInitialDeposit = amount; }

    @Override
    public double calculateInterest() {
        return getBalance() * interestRate;
    }

    @Override
    public void deposit(double amount) {
        super.deposit(amount);
    }

    @Override
    public boolean withdraw(double amount) {
        return super.withdraw(amount);
    }

    @Override
    public boolean transfer(Account toAccount, double amount) {
        return super.transfer(toAccount, amount);
    }
}