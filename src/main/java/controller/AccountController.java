package controller;

import model.Account;
import service.AccountService;

import java.util.List;

public class AccountController {

    private AccountService accountService = new AccountService();

    public void openAccount(Account account) {
        accountService.openAccount(account);
    }

    public void deposit(String accountNumber, double amount) {
        accountService.deposit(accountNumber, amount);
    }

    public boolean withdraw(String accountNumber, double amount) {
        return accountService.withdraw(accountNumber, amount);
    }

    public double applyMonthlyInterest(String accountNumber) {
        return accountService.applyMonthlyInterest(accountNumber);
    }

    public List<Account> getCustomerAccounts(String customerId) {
        return accountService.getCustomerAccounts(customerId);
    }

    public Account getAccount(String accountNumber) {
        return accountService.getAccount(accountNumber);
    }

    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    public boolean transfer(String fromAccountNumber, String toAccountNumber, double amount) {
        return accountService.transfer(fromAccountNumber, toAccountNumber, amount);
    }

    public boolean closeAccount(String accountNumber) {
        return accountService.closeAccount(accountNumber);
    }
}
