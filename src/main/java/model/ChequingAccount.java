package model;

public class ChequingAccount extends Account {

    public ChequingAccount(String accountNumber, double balance, 
                           String branch, Customer customer) {
        super(accountNumber, balance, branch, customer);
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
        return 0; // Chequing accounts earn no interest
    }
}

