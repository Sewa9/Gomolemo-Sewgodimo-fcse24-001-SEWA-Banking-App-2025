package model;

public interface AccountOperation {
    void depositAmount(double amount);
    void withdrawAmount(double amount);
    double checkBalance();
    void transferAmount(Account target, double amount);
}

