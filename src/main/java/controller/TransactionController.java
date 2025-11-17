package controller;

import model.Transaction;
import service.TransactionService;

import java.util.List;

public class TransactionController {

    private TransactionService transactionService = new TransactionService();

    public void addTransaction(Transaction t) {
        transactionService.addTransaction(t);
    }

    public List<Transaction> getAccountTransactions(String accountNumber) {
        return transactionService.getAccountTransactions(accountNumber);
    }
}

