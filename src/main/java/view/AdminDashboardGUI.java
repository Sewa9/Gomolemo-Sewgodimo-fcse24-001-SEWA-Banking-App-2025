package view;

import controller.BankingSystemController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Account;
import model.Customer;
import model.Transaction;

public class AdminDashboardGUI {

        private VBox view;
        private BankingSystemController controller;

        private TableView<Customer> customerTable;
        private TableView<Account> accountTable;
        private TableView<Transaction> transactionTable;

        private FilteredList<Customer> filteredCustomers;
        private FilteredList<Account> filteredAccounts;

        public AdminDashboardGUI(BankingSystemController controller, Stage stage) {
                this.controller = controller;
                view = new VBox(15);
                view.setPadding(new Insets(20));

                Label lblTitle = new Label("Admin Dashboard");

                // --- Customer Table & Search ---
                TextField txtCustomerSearch = new TextField();
                txtCustomerSearch.setPromptText("Search customers by name or ID");

                customerTable = new TableView<>();
                TableColumn<Customer, String> colCustId = new TableColumn<>("ID");
                colCustId.setCellValueFactory(
                                c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getId()));
                TableColumn<Customer, String> colCustName = new TableColumn<>("Name");
                colCustName.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                                c.getValue().getFirstName() + " " + c.getValue().getLastName()));
                TableColumn<Customer, String> colCustAddress = new TableColumn<>("Address");
                colCustAddress
                                .setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                                                c.getValue().getAddress()));
                customerTable.getColumns().addAll(colCustId, colCustName, colCustAddress);

                filteredCustomers = new FilteredList<>(FXCollections.observableArrayList(controller.listCustomers()),
                                p -> true);
                customerTable.setItems(filteredCustomers);

                txtCustomerSearch.textProperty().addListener((obs, oldVal, newVal) -> {
                        filteredCustomers.setPredicate(customer -> {
                                if (newVal == null || newVal.isEmpty())
                                        return true;
                                String lower = newVal.toLowerCase();
                                return customer.getId().toLowerCase().contains(lower) ||
                                                customer.getFirstName().toLowerCase().contains(lower) ||
                                                customer.getLastName().toLowerCase().contains(lower);
                        });
                });

                // --- Account Table & Search ---
                TextField txtAccountSearch = new TextField();
                txtAccountSearch.setPromptText("Search accounts by number or type");

                accountTable = new TableView<>();
                TableColumn<Account, String> colAccNum = new TableColumn<>("Account Number");
                colAccNum.setCellValueFactory(
                                c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getAccountNumber()));
                TableColumn<Account, String> colAccType = new TableColumn<>("Type");
                colAccType.setCellValueFactory(
                                c -> new javafx.beans.property.SimpleStringProperty(
                                                c.getValue().getClass().getSimpleName()));
                TableColumn<Account, String> colAccBalance = new TableColumn<>("Balance");
                colAccBalance.setCellValueFactory(
                                c -> new javafx.beans.property.SimpleStringProperty(
                                                String.valueOf(c.getValue().getBalance())));
                accountTable.getColumns().addAll(colAccNum, colAccType, colAccBalance);

                filteredAccounts = new FilteredList<>(FXCollections.observableArrayList(controller.listAllAccounts()),
                                p -> true);
                accountTable.setItems(filteredAccounts);

                txtAccountSearch.textProperty().addListener((obs, oldVal, newVal) -> {
                        filteredAccounts.setPredicate(account -> {
                                if (newVal == null || newVal.isEmpty())
                                        return true;
                                String lower = newVal.toLowerCase();
                                return account.getAccountNumber().toLowerCase().contains(lower) ||
                                                account.getClass().getSimpleName().toLowerCase().contains(lower);
                        });
                });

                // --- Transaction Table ---
                transactionTable = new TableView<>();
                TableColumn<Transaction, String> colTxnId = new TableColumn<>("Transaction ID");
                colTxnId.setCellValueFactory(
                                c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTransactionId()));
                TableColumn<Transaction, String> colTxnAcc = new TableColumn<>("Account");
                colTxnAcc.setCellValueFactory(
                                c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getAccountNumber()));
                TableColumn<Transaction, String> colTxnType = new TableColumn<>("Type");
                colTxnType.setCellValueFactory(
                                c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getType()));
                TableColumn<Transaction, String> colTxnAmount = new TableColumn<>("Amount");
                colTxnAmount.setCellValueFactory(
                                c -> new javafx.beans.property.SimpleStringProperty(
                                                String.valueOf(c.getValue().getAmount())));
                TableColumn<Transaction, String> colTxnDate = new TableColumn<>("Timestamp");
                colTxnDate.setCellValueFactory(
                                c -> new javafx.beans.property.SimpleStringProperty(
                                                c.getValue().getTimestamp().toString()));
                transactionTable.getColumns().addAll(colTxnId, colTxnAcc, colTxnType, colTxnAmount, colTxnDate);

                accountTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
                        if (newSel != null) {
                                transactionTable.setItems(FXCollections
                                                .observableArrayList(controller
                                                                .getAccountTransactions(newSel.getAccountNumber())));
                        }
                });

                // --- Buttons ---
                Button btnAddCustomer = new Button("Add New Customer");
                btnAddCustomer.setOnAction(e -> showAddCustomerDialog());

                Button btnAddUser = new Button("Add New User");
                btnAddUser.setOnAction(e -> showAddUserDialog());

                Button btnResetPassword = new Button("Reset Password");
                btnResetPassword.setOnAction(e -> showResetPasswordDialog());

                Button btnLogout = new Button("Logout");
                btnLogout.setOnAction(e -> stage.getScene().setRoot(new LoginGUI(stage).getView()));

                HBox buttonBox = new HBox(10, btnAddCustomer, btnAddUser, btnResetPassword, btnLogout);
                HBox searchBox = new HBox(10, txtCustomerSearch, txtAccountSearch);
                view.getChildren().addAll(lblTitle, searchBox, new Label("Customers:"), customerTable,
                                new Label("Accounts:"),
                                accountTable, new Label("Transactions:"), transactionTable, buttonBox);
        }

        public VBox getView() {
                return view;
        }

        private void showAddCustomerDialog() {
                Dialog<Customer> dialog = new Dialog<>();
                dialog.setTitle("Add New Customer");
                dialog.setHeaderText("Enter customer details:");

                ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                TextField txtId = new TextField();
                txtId.setPromptText("Customer ID");
                TextField txtFirstName = new TextField();
                txtFirstName.setPromptText("First Name");
                TextField txtLastName = new TextField();
                txtLastName.setPromptText("Last Name");
                TextField txtAddress = new TextField();
                txtAddress.setPromptText("Address");

                grid.add(new Label("ID:"), 0, 0);
                grid.add(txtId, 1, 0);
                grid.add(new Label("First Name:"), 0, 1);
                grid.add(txtFirstName, 1, 1);
                grid.add(new Label("Last Name:"), 0, 2);
                grid.add(txtLastName, 1, 2);
                grid.add(new Label("Address:"), 0, 3);
                grid.add(txtAddress, 1, 3);

                dialog.getDialogPane().setContent(grid);

                dialog.setResultConverter(dialogButton -> {
                        if (dialogButton == addButtonType) {
                                return new Customer(txtId.getText(), txtFirstName.getText(), txtLastName.getText(),
                                                txtAddress.getText());
                        }
                        return null;
                });

                dialog.showAndWait().ifPresent(customer -> {
                        if (customer != null) {
                                controller.addCustomer(customer);
                                refreshCustomerTable();
                        }
                });
        }

        private void refreshCustomerTable() {
                filteredCustomers = new FilteredList<>(FXCollections.observableArrayList(controller.listCustomers()),
                                p -> true);
                customerTable.setItems(filteredCustomers);
        }

        private void showAddUserDialog() {
                Dialog<Void> dialog = new Dialog<>();
                dialog.setTitle("Add New User");
                dialog.setHeaderText("Enter user details:");

                ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                TextField txtUsername = new TextField();
                txtUsername.setPromptText("Username");
                PasswordField txtPassword = new PasswordField();
                txtPassword.setPromptText("Password");
                ComboBox<String> cbRole = new ComboBox<>();
                cbRole.getItems().addAll("employee");
                cbRole.setPromptText("Select Role");

                grid.add(new Label("Username:"), 0, 0);
                grid.add(txtUsername, 1, 0);
                grid.add(new Label("Password:"), 0, 1);
                grid.add(txtPassword, 1, 1);
                grid.add(new Label("Role:"), 0, 2);
                grid.add(cbRole, 1, 2);

                dialog.getDialogPane().setContent(grid);

                dialog.setResultConverter(dialogButton -> {
                        if (dialogButton == addButtonType) {
                                String username = txtUsername.getText();
                                String password = txtPassword.getText();
                                String role = cbRole.getValue();
                                if (username != null && !username.isEmpty() && password != null && !password.isEmpty()
                                                && role != null) {
                                        controller.registerUser(username, password, role);
                                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                        alert.setTitle("Success");
                                        alert.setHeaderText(null);
                                        alert.setContentText("User added successfully!");
                                        alert.showAndWait();
                                } else {
                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                        alert.setTitle("Error");
                                        alert.setHeaderText(null);
                                        alert.setContentText("Please fill in all fields.");
                                        alert.showAndWait();
                                }
                        }
                        return null;
                });

                dialog.showAndWait();
        }

        private void showResetPasswordDialog() {
                Dialog<Void> dialog = new Dialog<>();
                dialog.setTitle("Reset Password");
                dialog.setHeaderText("Enter user details to reset password:");

                ButtonType resetButtonType = new ButtonType("Reset", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(resetButtonType, ButtonType.CANCEL);

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                TextField txtUsername = new TextField();
                txtUsername.setPromptText("Username");
                PasswordField txtNewPassword = new PasswordField();
                txtNewPassword.setPromptText("New Password");

                grid.add(new Label("Username:"), 0, 0);
                grid.add(txtUsername, 1, 0);
                grid.add(new Label("New Password:"), 0, 1);
                grid.add(txtNewPassword, 1, 1);

                dialog.getDialogPane().setContent(grid);

                dialog.setResultConverter(dialogButton -> {
                        if (dialogButton == resetButtonType) {
                                String username = txtUsername.getText();
                                String newPassword = txtNewPassword.getText();
                                if (username != null && !username.isEmpty() && newPassword != null
                                                && !newPassword.isEmpty()) {
                                        boolean success = controller.resetPassword(username, newPassword);
                                        if (success) {
                                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                                alert.setTitle("Success");
                                                alert.setHeaderText(null);
                                                alert.setContentText("Password reset successfully!");
                                                alert.showAndWait();
                                        } else {
                                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                                alert.setTitle("Error");
                                                alert.setHeaderText(null);
                                                alert.setContentText("Failed to reset password. User not found.");
                                                alert.showAndWait();
                                        }
                                } else {
                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                        alert.setTitle("Error");
                                        alert.setHeaderText(null);
                                        alert.setContentText("Please fill in all fields.");
                                        alert.showAndWait();
                                }
                        }
                        return null;
                });

                dialog.showAndWait();
        }
}
