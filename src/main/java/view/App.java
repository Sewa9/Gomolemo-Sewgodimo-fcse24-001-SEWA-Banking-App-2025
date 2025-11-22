package view;

import controller.BankingSystemController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private BankingSystemController controller;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize database
            System.out.println("Initializing database...");
            util.DatabaseInitializer.initializeDatabase();

            // Initialize main controller
            controller = new BankingSystemController();

            // Optional: Seed data for testing
            seedData();

            // Create login GUI
            LoginGUI loginGUI = new LoginGUI(primaryStage);

            Scene scene = new Scene(loginGUI.getView(), 400, 300);

            primaryStage.setTitle("Banking System - Login");
            primaryStage.setScene(scene);
            primaryStage.show();
            
            System.out.println("Application started successfully!");

        } catch (Exception e) {
            System.err.println("ERROR starting application: " + e.getMessage());
            e.printStackTrace();
            showErrorDialog("Failed to start application: " + e.getMessage());
        }
    }

    private void seedData() {
        try {
            System.out.println("Seeding sample data...");
            
            // Add sample customers if they don't exist
            if (controller.findCustomer("C001") == null) {
                controller.registerCustomer(new model.Customer("C001", "John", "Doe", "Gaborone"));
            }
            if (controller.findCustomer("C002") == null) {
                controller.registerCustomer(new model.Customer("C002", "Jane", "Smith", "Francistown"));
            }

            // Add accounts for customers if they don't exist
            if (controller.getAccountByNumber("S001") == null) {
                controller.openAccount(new model.SavingsAccount("S001", 1000.0, "MainBranch", controller.findCustomer("C001")));
            }
            if (controller.getAccountByNumber("CH001") == null) {
                controller.openAccount(new model.ChequingAccount("CH001", 500.0, "MainBranch", controller.findCustomer("C001")));
            }
            if (controller.getAccountByNumber("I001") == null) {
                controller.openAccount(new model.InvestmentAccount("I001", 5000.0, "MainBranch", controller.findCustomer("C002")));
            }

            System.out.println("Sample data seeded successfully!");
            
        } catch (Exception e) {
            System.err.println("ERROR seeding data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showErrorDialog(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Application Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            System.err.println("FATAL ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}