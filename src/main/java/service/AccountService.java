package service;

import dao.AccountDAO;
import model.Account;
import model.Transaction;
import dao.TransactionDAO;

import java.util.List;
import java.util.UUID;

public class AccountService {

    private AccountDAO accountDAO = new AccountDAO();
    private TransactionDAO transactionDAO = new TransactionDAO();

    // Open account - FIXED to ensure proper customer linking
    public void openAccount(Account account) {
        System.out.println("DEBUG: AccountService.openAccount() called for account: " + account.getAccountNumber());
        System.out.println("DEBUG: Customer ID: " + account.getCustomer().getId());
        
        accountDAO.addAccount(account);
        
        // FIX: Ensure customer account list is updated
        if (account.getCustomer() != null) {
            account.getCustomer().addAccount(account);
            System.out.println("DEBUG: Account added to customer's account list");
        } else {
            System.out.println("DEBUG: WARNING - Customer is null in account!");
        }
        
        System.out.println("DEBUG: Account opened successfully");
    }

    // Deposit funds
    public void deposit(String accountNumber, double amount) {
        Account account = accountDAO.getAccount(accountNumber);
        if (account != null) {
            account.deposit(amount);
            accountDAO.updateBalance(accountNumber, account.getBalance());

            // Record transaction
            Transaction t = new Transaction(
                    UUID.randomUUID().toString(),
                    accountNumber,
                    amount,
                    "Deposit");
            transactionDAO.addTransaction(t);
        }
    }

    // Withdraw funds
    public boolean withdraw(String accountNumber, double amount) {
        Account account = accountDAO.getAccount(accountNumber);
        if (account != null && account.withdraw(amount)) {
            accountDAO.updateBalance(accountNumber, account.getBalance());

            // Record transaction
            Transaction t = new Transaction(
                    UUID.randomUUID().toString(),
                    accountNumber,
                    amount,
                    "Withdrawal");
            transactionDAO.addTransaction(t);
            return true;
        }
        return false;
    }

    // List all accounts for a customer - FIXED to ensure proper retrieval
    public List<Account> getCustomerAccounts(String customerId) {
        System.out.println("DEBUG: AccountService.getCustomerAccounts() called for customer: " + customerId);
        List<Account> accounts = accountDAO.getCustomerAccounts(customerId);
        System.out.println("DEBUG: Retrieved " + accounts.size() + " accounts for customer " + customerId);
        for (Account acc : accounts) {
            System.out.println("DEBUG: Account " + acc.getAccountNumber() + " - Balance: " + acc.getBalance() + " - Customer: " + acc.getCustomer().getId());
        }
        return accounts;
    }

    // Calculate monthly interest for an account
    public double applyMonthlyInterest(String accountNumber) {
        Account account = accountDAO.getAccount(accountNumber);
        if (account != null) {
            double interest = account.calculateMonthlyInterest();
            account.deposit(interest); // add interest
            accountDAO.updateBalance(accountNumber, account.getBalance());

            // Record transaction for interest
            Transaction t = new Transaction(
                    UUID.randomUUID().toString(),
                    accountNumber,
                    interest,
                    "Interest");
            transactionDAO.addTransaction(t);

            return interest;
        }
        return 0;
    }

    // Get account by number
    public Account getAccount(String accountNumber) {
        return accountDAO.getAccount(accountNumber);
    }

    // Get all accounts
    public List<Account> getAllAccounts() {
        return accountDAO.getAllAccounts();
    }

    // Transfer funds between accounts
    public boolean transfer(String fromAccountNumber, String toAccountNumber, double amount) {
        Account fromAccount = accountDAO.getAccount(fromAccountNumber);
        Account toAccount = accountDAO.getAccount(toAccountNumber);
        if (fromAccount != null && toAccount != null && fromAccount.withdraw(amount)) {
            toAccount.deposit(amount);
            accountDAO.updateBalance(fromAccountNumber, fromAccount.getBalance());
            accountDAO.updateBalance(toAccountNumber, toAccount.getBalance());

            // Record transactions
            Transaction t1 = new Transaction(
                    UUID.randomUUID().toString(),
                    fromAccountNumber,
                    amount,
                    "Transfer Out");
            transactionDAO.addTransaction(t1);

            Transaction t2 = new Transaction(
                    UUID.randomUUID().toString(),
                    toAccountNumber,
                    amount,
                    "Transfer In");
            transactionDAO.addTransaction(t2);
            return true;
        }
        return false;
    }

    // Close account
    public boolean closeAccount(String accountNumber) {
        Account account = accountDAO.getAccount(accountNumber);
        if (account != null && account.getBalance() == 0) {
            accountDAO.deleteAccount(accountNumber);
            return true;
        }
        return false;
    }
}