package com.example;

import controller.BankingSystemController;
import model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.DatabaseInitializer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerIntegrationTest {

    private BankingSystemController controller;

    @BeforeAll
    public static void initDatabase() {
        DatabaseInitializer.initializeDatabase();
    }

    @BeforeEach
    public void setup() {
        controller = new BankingSystemController();
        Customer cust = new Customer("C001", "Alice", "Smith", "Gaborone");
        controller.registerCustomer(cust);

        Account acc = new SavingsAccount("S001", 1000, "MainBranch", cust);
        controller.openAccount(acc);
    }

    @Test
    public void testFullFlowDeposit() {
        controller.depositToAccount("S001", 200);
        Account acc = controller.getAccountByNumber("S001");
        assertEquals(1200, acc.getBalance());

        List<Transaction> txns = controller.getAccountTransactions("S001");
        assertEquals(1, txns.size());
        assertEquals("Deposit", txns.get(0).getType());
    }

    @Test
    public void testFullFlowWithdrawal() {
        controller.withdrawFromAccount("S001", 100);
        Account acc = controller.getAccountByNumber("S001");
        assertEquals(900, acc.getBalance());

        List<Transaction> txns = controller.getAccountTransactions("S001");
        assertEquals(1, txns.size());
        assertEquals("Withdrawal", txns.get(0).getType());
    }
}

