package controller;

import model.Account;
import model.Customer;

import java.util.List;
import java.util.Map;

public class BankingSystemController {

    private CustomerController customerController = new CustomerController();
    private AccountController accountController = new AccountController();
    private TransactionController transactionController = new TransactionController();
    private SecurityController securityController = new SecurityController();
    private AuditController auditController = new AuditController();

    // Customer operations
    public void registerCustomer(Customer customer) {
        customerController.addCustomer(customer);
        auditController.logAction("Customer registered: " + customer.getId());
    }

    public Customer findCustomer(String id) {
        return customerController.getCustomer(id);
    }

    public List<Customer> listCustomers() {
        return customerController.getAllCustomers();
    }

    // Account operations
    public void openAccount(Account account) {
        accountController.openAccount(account);
        auditController.logAction("Account opened: " + account.getAccountNumber());
    }

    public void depositToAccount(String accountNumber, double amount) {
        accountController.deposit(accountNumber, amount);
        auditController.logAction("Deposit of " + amount + " to " + accountNumber);
    }

    public boolean withdrawFromAccount(String accountNumber, double amount) {
        boolean success = accountController.withdraw(accountNumber, amount);
        if (success) {
            auditController.logAction("Withdrawal of " + amount + " from " + accountNumber);
        }
        return success;
    }

    public double applyInterest(String accountNumber) {
        double interest = accountController.applyMonthlyInterest(accountNumber);
        auditController.logAction("Interest applied to " + accountNumber + ": " + interest);
        return interest;
    }

    public List<Account> getCustomerAccounts(String customerId) {
        return accountController.getCustomerAccounts(customerId);
    }

    public List<Account> listAllAccounts() {
        return accountController.getAllAccounts();
    }

    public Account getAccountByNumber(String accountNumber) {
        return accountController.getAccount(accountNumber);
    }

    public List<model.Transaction> getAccountTransactions(String accountNumber) {
        return transactionController.getAccountTransactions(accountNumber);
    }

    // Security operations
    public boolean login(String username, String password) {
        boolean success = securityController.login(username, password);
        auditController.logAction("Login attempt by " + username + " - " + (success ? "SUCCESS" : "FAIL"));
        return success;
    }

    public void addCustomer(Customer customer) {
        customerController.addCustomer(customer);
        auditController.logAction("Customer added: " + customer.getId());
    }

    public void registerUser(String username, String password, String role) {
        securityController.registerUser(username, password, role);
        auditController.logAction("User registered: " + username + " with role " + role);
    }

    public void changeUserPassword(String username, String newPassword) {
        securityController.changePassword(username, newPassword);
        auditController.logAction("Password changed for user: " + username);
    }

    public Map<String, String> getAllUsers() {
        return securityController.getAllUsers();
    }

    // Transfer funds between accounts
    public boolean transferFunds(String fromAccountNumber, String toAccountNumber, double amount) {
        boolean success = accountController.transfer(fromAccountNumber, toAccountNumber, amount);
        if (success) {
            auditController
                    .logAction("Transfer of " + amount + " from " + fromAccountNumber + " to " + toAccountNumber);
        }
        return success;
    }

    // Close account
    public boolean closeAccount(String accountNumber) {
        boolean success = accountController.closeAccount(accountNumber);
        if (success) {
            auditController.logAction("Account closed: " + accountNumber);
        }
        return success;
    }

    // Reset password for user
    public boolean resetPassword(String username, String newPassword) {
        boolean success = securityController.changePassword(username, newPassword);
        if (success) {
            auditController.logAction("Password reset for user: " + username);
        }
        return success;
    }
}
