package service;

import dao.TransactionDAO;
import model.Transaction;

import java.util.List;

public class TransactionService {

    private TransactionDAO transactionDAO = new TransactionDAO();

    // Add transaction (optional, usually handled in AccountService)
    public void addTransaction(Transaction t) {
        transactionDAO.addTransaction(t);
    }

    // Get transactions for a specific account
    public List<Transaction> getAccountTransactions(String accountNumber) {
        return transactionDAO.getAccountTransactions(accountNumber);
    }
}

