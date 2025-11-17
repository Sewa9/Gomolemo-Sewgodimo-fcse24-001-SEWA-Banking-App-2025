package com.example;

import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class TestRunner {
    public static void main(String[] args) {
        // Initialize database for all tests
        util.DatabaseInitializer.initializeDatabase();

        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(
                        selectClass(AppTest.class),
                        selectClass(BankingSystemTest.class),
                        selectClass(ControllerIntegrationTest.class),
                        selectClass(DatabaseConnectionTest.class)
                )
                .build();

        LauncherFactory.create().execute(request);
        System.out.println("All tests executed successfully on initialized database!");
    }
}

