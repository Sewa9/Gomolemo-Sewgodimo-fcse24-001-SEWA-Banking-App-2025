package model;

public class ChequingAccount extends Account {
    private String companyName;

    public ChequingAccount(String accountNumber, double balance, String branch, Customer customer, String companyName) {
        super(accountNumber, balance, branch, customer);
        this.companyName = companyName;
    }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    @Override
    public double calculateInterest() {
        // Chequing accounts typically don't earn interest
        return 0;
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