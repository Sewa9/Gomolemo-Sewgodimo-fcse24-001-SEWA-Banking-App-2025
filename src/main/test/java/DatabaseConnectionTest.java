package com.example;

import dao.AccountDAO;
import dao.CustomerDAO;
import model.Account;
import model.Customer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.DatabaseConfig;
import util.DatabaseInitializer;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseConnectionTest {

    private Connection conn;
    private CustomerDAO customerDAO;
    private AccountDAO accountDAO;

    @BeforeAll
    public static void initDatabase() {
        DatabaseInitializer.initializeDatabase();
    }

    @BeforeEach
    public void setup() throws SQLException {
        conn = DatabaseConfig.getConnection();
        assertNotNull(conn);

        customerDAO = new CustomerDAO();
        accountDAO = new AccountDAO();
    }

    @Test
    public void testConnection() throws SQLException {
        assertFalse(conn.isClosed(), "Connection should be open");
    }

    @Test
    public void testCRUDCustomer() throws SQLException {
        Customer cust = new Customer("C100", "Test", "User", "Gaborone");
        customerDAO.createCustomer(cust);

        Customer fetched = customerDAO.getCustomerById("C100");
        assertNotNull(fetched);
        assertEquals("Test", fetched.getFirstName());

        customerDAO.deleteCustomer("C100");
        Customer deleted = customerDAO.getCustomerById("C100");
        assertNull(deleted);
    }

    @Test
    public void testCRUDAccount() throws SQLException {
        Customer cust = new Customer("C101", "Alice", "Tester", "Gaborone");
        customerDAO.createCustomer(cust);

        Account acc = new model.SavingsAccount("A101", 1000, "MainBranch", cust);
        accountDAO.createAccount(acc);

        Account fetched = accountDAO.getAccountByNumber("A101");
        assertNotNull(fetched);
        assertEquals(1000, fetched.getBalance());

        accountDAO.deleteAccount("A101");
        Account deleted = accountDAO.getAccountByNumber("A101");
        assertNull(deleted);
    }
}
