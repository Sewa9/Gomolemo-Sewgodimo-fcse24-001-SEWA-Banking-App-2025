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

    // Open account
    public void openAccount(Account account) {
        accountDAO.addAccount(account);
        account.getCustomer().addAccount(account); // link account to customer
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

    // List all accounts for a customer
    public List<Account> getCustomerAccounts(String customerId) {
        return accountDAO.getCustomerAccounts(customerId);
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
