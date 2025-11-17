package view;

import controller.BankingSystemController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private BankingSystemController controller;

    @Override
    public void start(Stage primaryStage) {

        // Initialize database
        util.DatabaseInitializer.initializeDatabase();

        // Initialize main controller
        controller = new BankingSystemController();

        // Optional: Seed data for testing
        seedData();

        // Create login GUI
        LoginGUI loginGUI = new LoginGUI(primaryStage);

        Scene scene = new Scene(loginGUI.getView(), 900, 600);

        primaryStage.setTitle("Banking System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void seedData() {
        // Add sample customers
        controller.registerCustomer(new model.Customer("C001", "John", "Doe", "Gaborone"));
        controller.registerCustomer(new model.Customer("C002", "Jane", "Smith", "Francistown"));

        // Add accounts for customers
        controller.openAccount(new model.SavingsAccount("S001", 1000.0, "MainBranch", controller.findCustomer("C001")));
        controller
                .openAccount(new model.ChequingAccount("CH001", 500.0, "MainBranch", controller.findCustomer("C001")));
        controller.openAccount(
                new model.InvestmentAccount("I001", 5000.0, "MainBranch", controller.findCustomer("C002")));

        // Add sample transactions
        controller.depositToAccount("S001", 200.0);
        controller.withdrawFromAccount("CH001", 100.0);
        controller.depositToAccount("I001", 500.0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
