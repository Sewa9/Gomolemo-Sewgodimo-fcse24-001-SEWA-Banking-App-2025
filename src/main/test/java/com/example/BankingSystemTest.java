package com.example;

import controller.BankingSystemController;
import model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.DatabaseInitializer;

import static org.junit.jupiter.api.Assertions.*;

public class BankingSystemTest {

    private BankingSystemController controller;
    private Customer customer;
    private SavingsAccount savings;
    private ChequingAccount chequing;
    private InvestmentAccount investment;

    @BeforeAll
    public static void initDatabase() {
        DatabaseInitializer.initializeDatabase();
    }

    @BeforeEach
    public void setup() {
        controller = new BankingSystemController();
        customer = new Customer("C001", "John", "Doe", "Gaborone");
        controller.registerCustomer(customer);

        savings = new SavingsAccount("S001", 1000, "MainBranch", customer);
        chequing = new ChequingAccount("C001", 500, "MainBranch", customer);
        investment = new InvestmentAccount("I001", 5000, "MainBranch", customer);

        controller.openAccount(savings);
        controller.openAccount(chequing);
        controller.openAccount(investment);
    }

    @Test
    public void testDeposit() {
        controller.depositToAccount("S001", 500);
        assertEquals(1500, savings.getBalance());
    }

    @Test
    public void testWithdraw() {
        controller.withdrawFromAccount("C001", 200);
        assertEquals(300, chequing.getBalance());
    }

    @Test
    public void testInterestApplication() {
        double interest = controller.applyInterest("S001");
        assertTrue(interest > 0);
        assertEquals(savings.getBalance(), 1000 + interest);
    }

    @Test
    public void testPolymorphism() {
        Account acc1 = savings;
        Account acc2 = chequing;
        Account acc3 = investment;

        assertTrue(acc1 instanceof Account);
        assertTrue(acc2 instanceof Account);
        assertTrue(acc3 instanceof Account);
    }
}

