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
import model.ChequingAccount;
import model.Customer;
import model.InvestmentAccount;
import model.SavingsAccount;
import model.Transaction;

public class EmployeeDashboardGUI {

        private VBox view;
        private BankingSystemController controller;

        private TableView<Customer> customerTable;
        private TableView<Account> accountTable;
        private TableView<Transaction> transactionTable;

        private FilteredList<Customer> filteredCustomers;
        private FilteredList<Account> filteredAccounts;

        private TextField txtAccountSearch;

        public EmployeeDashboardGUI(BankingSystemController controller, Stage stage) {
                this.controller = controller;

                view = new VBox(15);
                view.setPadding(new Insets(20));

                Label lblTitle = new Label("Employee Dashboard");

                // --- Customer Table & Search ---
                TextField txtCustomerSearch = new TextField();
                txtCustomerSearch.setPromptText("Search customers");

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
                        filteredCustomers.setPredicate(cust -> {
                                if (newVal == null || newVal.isEmpty())
                                        return true;
                                String lower = newVal.toLowerCase();
                                return cust.getId().toLowerCase().contains(lower)
                                                || cust.getFirstName().toLowerCase().contains(lower)
                                                || cust.getLastName().toLowerCase().contains(lower);
                        });
                });

                // --- Account Table & Search ---
                txtAccountSearch = new TextField();
                txtAccountSearch.setPromptText("Search accounts");

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
                        filteredAccounts.setPredicate(acc -> {
                                if (newVal == null || newVal.isEmpty())
                                        return true;
                                String lower = newVal.toLowerCase();
                                return acc.getAccountNumber().toLowerCase().contains(lower)
                                                || acc.getClass().getSimpleName().toLowerCase().contains(lower);
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
                colTxnDate
                                .setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
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
                Button btnCreateCustomer = new Button("Create Customer");
                btnCreateCustomer.setOnAction(e -> showCreateCustomerDialog());

                Button btnCreateAccount = new Button("Create Account for Customer");
                btnCreateAccount.setOnAction(e -> showCreateAccountForCustomerDialog());

                Button btnDepositForCustomer = new Button("Deposit for Customer");
                btnDepositForCustomer.setOnAction(e -> showDepositForCustomerDialog());

                Button btnCloseAccount = new Button("Close Account");
                btnCloseAccount.setOnAction(e -> showCloseAccountDialog());

                Button btnWithdrawForCustomer = new Button("Withdraw for Customer");
                btnWithdrawForCustomer.setOnAction(e -> showWithdrawForCustomerDialog());

                Button btnTransferForCustomer = new Button("Transfer for Customer");
                btnTransferForCustomer.setOnAction(e -> showTransferForCustomerDialog());

                Button btnLogout = new Button("Logout");
                btnLogout.setOnAction(e -> stage.getScene().setRoot(new LoginGUI(stage).getView()));

                HBox buttonBox = new HBox(10, btnCreateCustomer, btnCreateAccount, btnDepositForCustomer,
                                btnCloseAccount,
                                btnWithdrawForCustomer,
                                btnTransferForCustomer, btnLogout);

                view.getChildren().addAll(lblTitle, new HBox(10, txtCustomerSearch, txtAccountSearch),
                                new Label("Customers:"),
                                customerTable,
                                buttonBox,
                                new Label("Accounts:"), accountTable, new Label("Transactions:"), transactionTable);
        }

        public VBox getView() {
                return view;
        }

        private void showCreateCustomerDialog() {
                Dialog<Customer> dialog = new Dialog<>();
                dialog.setTitle("Create Customer");
                dialog.setHeaderText("Enter customer details:");

                ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                TextField txtId = new TextField();
                txtId.setPromptText("Customer ID (This will be login username)");
                TextField txtFirstName = new TextField();
                txtFirstName.setPromptText("First Name");
                TextField txtLastName = new TextField();
                txtLastName.setPromptText("Last Name");
                TextField txtAddress = new TextField();
                txtAddress.setPromptText("Address");
                PasswordField txtPassword = new PasswordField();
                txtPassword.setPromptText("Password (default: pass123)");
                txtPassword.setText("pass123"); // Set default password

                grid.add(new Label("Customer ID/Username:"), 0, 0);
                grid.add(txtId, 1, 0);
                grid.add(new Label("First Name:"), 0, 1);
                grid.add(txtFirstName, 1, 1);
                grid.add(new Label("Last Name:"), 0, 2);
                grid.add(txtLastName, 1, 2);
                grid.add(new Label("Address:"), 0, 3);
                grid.add(txtAddress, 1, 3);
                grid.add(new Label("Password:"), 0, 4);
                grid.add(txtPassword, 1, 4);

                dialog.getDialogPane().setContent(grid);

                dialog.setResultConverter(dialogButton -> {
                        if (dialogButton == createButtonType) {
                                String id = txtId.getText();
                                String firstName = txtFirstName.getText();
                                String lastName = txtLastName.getText();
                                String address = txtAddress.getText();
                                String password = txtPassword.getText();

                                if (id != null && !id.isEmpty() && firstName != null && !firstName.isEmpty() &&
                                                lastName != null && !lastName.isEmpty() && address != null
                                                && !address.isEmpty() &&
                                                password != null && !password.isEmpty()) {

                                        System.out.println("DEBUG: Creating customer with ID: " + id + " and password: "
                                                        + password);

                                        // Create customer
                                        Customer customer = new Customer(id, firstName, lastName, address);
                                        controller.registerCustomer(customer);

                                        // Register user with the SAME ID as username and default password
                                        controller.registerUser(id, password, "customer");

                                        refreshCustomerTable();

                                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                        alert.setTitle("Success");
                                        alert.setHeaderText(null);
                                        alert.setContentText("Customer created successfully!\nUsername: " + id
                                                        + "\nPassword: " + password);
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

        private void refreshCustomerTable() {
                filteredCustomers = new FilteredList<>(FXCollections.observableArrayList(controller.listCustomers()),
                                p -> true);
                customerTable.setItems(filteredCustomers);
        }

        private void showCloseAccountDialog() {
                Account selected = accountTable.getSelectionModel().getSelectedItem();
                if (selected == null) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("No Selection");
                        alert.setHeaderText(null);
                        alert.setContentText("Please select an account to close.");
                        alert.showAndWait();
                        return;
                }

                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Close Account");
                confirmation.setHeaderText(
                                "Are you sure you want to close account " + selected.getAccountNumber() + "?");
                confirmation.setContentText("The account must have a balance of 0 to be closed.");
                confirmation.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                                boolean success = controller.closeAccount(selected.getAccountNumber());
                                if (success) {
                                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                        alert.setTitle("Success");
                                        alert.setHeaderText(null);
                                        alert.setContentText("Account closed successfully!");
                                        alert.showAndWait();
                                        refreshAccountTable();
                                } else {
                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                        alert.setTitle("Error");
                                        alert.setHeaderText(null);
                                        alert.setContentText("Failed to close account. Ensure balance is 0.");
                                        alert.showAndWait();
                                }
                        }
                });
        }

        private void refreshAccountTable() {
                ObservableList<Account> source = FXCollections.observableArrayList(controller.listAllAccounts());
                filteredAccounts = new FilteredList<>(source, p -> true);
                accountTable.setItems(filteredAccounts);
                // Re-apply search predicate if active
                String searchText = txtAccountSearch.getText();
                if (searchText != null && !searchText.isEmpty()) {
                        filteredAccounts.setPredicate(acc -> {
                                String lower = searchText.toLowerCase();
                                return acc.getAccountNumber().toLowerCase().contains(lower)
                                                || acc.getClass().getSimpleName().toLowerCase().contains(lower);
                        });
                }
        }

        private void showWithdrawForCustomerDialog() {
                Account selected = accountTable.getSelectionModel().getSelectedItem();
                if (selected == null) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("No Selection");
                        alert.setHeaderText(null);
                        alert.setContentText("Please select an account to withdraw from.");
                        alert.showAndWait();
                        return;
                }

                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Withdraw for Customer");
                dialog.setHeaderText("Withdraw from account: " + selected.getAccountNumber());
                dialog.setContentText("Amount:");
                dialog.showAndWait().ifPresent(amountStr -> {
                        try {
                                double amount = Double.parseDouble(amountStr);
                                boolean success = controller.withdrawFromAccount(selected.getAccountNumber(), amount);
                                if (success) {
                                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                        alert.setTitle("Success");
                                        alert.setHeaderText(null);
                                        alert.setContentText("Withdrawal successful!");
                                        alert.showAndWait();
                                        String selectedAccountNumber = selected.getAccountNumber();
                                        refreshAccountTable();
                                        // Re-select the account to update transactions
                                        for (Account acc : filteredAccounts) {
                                                if (acc.getAccountNumber().equals(selectedAccountNumber)) {
                                                        accountTable.getSelectionModel().select(acc);
                                                        break;
                                                }
                                        }
                                        // Explicitly refresh transaction table
                                        transactionTable.setItems(FXCollections.observableArrayList(
                                                        controller.getAccountTransactions(selectedAccountNumber)));
                                } else {
                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                        alert.setTitle("Error");
                                        alert.setHeaderText(null);
                                        alert.setContentText("Withdrawal failed. Insufficient funds.");
                                        alert.showAndWait();
                                }
                        } catch (NumberFormatException e) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Invalid Amount");
                                alert.setHeaderText(null);
                                alert.setContentText("Please enter a valid amount.");
                                alert.showAndWait();
                        }
                });
        }

        private void showTransferForCustomerDialog() {
                Dialog<Void> dialog = new Dialog<>();
                dialog.setTitle("Transfer for Customer");
                dialog.setHeaderText("Transfer between accounts");

                ButtonType transferButtonType = new ButtonType("Transfer", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(transferButtonType, ButtonType.CANCEL);

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                TextField txtFromAccount = new TextField();
                txtFromAccount.setPromptText("From Account Number");
                TextField txtToAccount = new TextField();
                txtToAccount.setPromptText("To Account Number");
                TextField txtAmount = new TextField();
                txtAmount.setPromptText("Amount");

                grid.add(new Label("From Account:"), 0, 0);
                grid.add(txtFromAccount, 1, 0);
                grid.add(new Label("To Account:"), 0, 1);
                grid.add(txtToAccount, 1, 1);
                grid.add(new Label("Amount:"), 0, 2);
                grid.add(txtAmount, 1, 2);

                dialog.getDialogPane().setContent(grid);

                dialog.setResultConverter(dialogButton -> {
                        if (dialogButton == transferButtonType) {
                                String fromAcc = txtFromAccount.getText();
                                String toAcc = txtToAccount.getText();
                                String amountStr = txtAmount.getText();
                                if (fromAcc != null && !fromAcc.isEmpty() && toAcc != null && !toAcc.isEmpty()
                                                && amountStr != null && !amountStr.isEmpty()) {
                                        try {
                                                double amount = Double.parseDouble(amountStr);
                                                boolean success = controller.transferFunds(fromAcc, toAcc, amount);
                                                if (success) {
                                                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                                        alert.setTitle("Transfer Successful");
                                                        alert.setHeaderText(null);
                                                        alert.setContentText("Transferred " + amount + " from "
                                                                        + fromAcc + " to " + toAcc);
                                                        alert.showAndWait();
                                                        refreshAccountTable();
                                                        // Re-select the from account to show its transactions
                                                        for (Account acc : filteredAccounts) {
                                                                if (acc.getAccountNumber().equals(fromAcc)) {
                                                                        accountTable.getSelectionModel().select(acc);
                                                                        break;
                                                                }
                                                        }
                                                } else {
                                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                                        alert.setTitle("Transfer Failed");
                                                        alert.setHeaderText(null);
                                                        alert.setContentText(
                                                                        "Transfer failed. Check account numbers and balance.");
                                                        alert.showAndWait();
                                                }
                                        } catch (NumberFormatException e) {
                                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                                alert.setTitle("Invalid Amount");
                                                alert.setHeaderText(null);
                                                alert.setContentText("Please enter a valid amount.");
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

        private String generateAccountNumber(String accountType) {
                // Simple account number generation: prefix + random number
                String prefix = accountType.equals("ChequingAccount") ? "C"
                                : accountType.equals("SavingsAccount") ? "S" : "I";
                int randomNum = (int) (Math.random() * 1000000);
                return prefix + String.format("%06d", randomNum);
        }

        private void showDepositForCustomerDialog() {
                Account selected = accountTable.getSelectionModel().getSelectedItem();
                if (selected == null) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("No Selection");
                        alert.setHeaderText(null);
                        alert.setContentText("Please select an account to deposit to.");
                        alert.showAndWait();
                        return;
                }

                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Deposit for Customer");
                dialog.setHeaderText("Deposit to account: " + selected.getAccountNumber());
                dialog.setContentText("Amount:");
                dialog.showAndWait().ifPresent(amountStr -> {
                        try {
                                double amount = Double.parseDouble(amountStr);
                                controller.depositToAccount(selected.getAccountNumber(), amount);
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Success");
                                alert.setHeaderText(null);
                                alert.setContentText("Deposit successful!");
                                alert.showAndWait();
                                String selectedAccountNumber = selected.getAccountNumber();
                                refreshAccountTable();
                                // Re-select the account to update transactions
                                for (Account acc : filteredAccounts) {
                                        if (acc.getAccountNumber().equals(selectedAccountNumber)) {
                                                accountTable.getSelectionModel().select(acc);
                                                break;
                                        }
                                }
                                // Explicitly refresh transaction table
                                transactionTable.setItems(FXCollections.observableArrayList(
                                                controller.getAccountTransactions(selectedAccountNumber)));
                        } catch (NumberFormatException e) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Invalid Amount");
                                alert.setHeaderText(null);
                                alert.setContentText("Please enter a valid amount.");
                                alert.showAndWait();
                        }
                });
        }

        private void showCreateAccountForCustomerDialog() {
                Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
                if (selectedCustomer == null) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("No Selection");
                        alert.setHeaderText(null);
                        alert.setContentText("Please select a customer to create an account for.");
                        alert.showAndWait();
                        return;
                }

                Dialog<Void> dialog = new Dialog<>();
                dialog.setTitle("Create Account for Customer");
                dialog.setHeaderText("Create a new account for " + selectedCustomer.getFirstName() + " "
                                + selectedCustomer.getLastName() + " (ID: " + selectedCustomer.getId() + ")");

                ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                ComboBox<String> cbAccountType = new ComboBox<>();
                cbAccountType.getItems().addAll("ChequingAccount", "SavingsAccount", "InvestmentAccount");
                cbAccountType.setPromptText("Select Account Type");

                TextField txtInitialDeposit = new TextField();
                txtInitialDeposit.setPromptText("Initial Deposit");
                txtInitialDeposit.setText("0.0");

                grid.add(new Label("Customer ID:"), 0, 0);
                grid.add(new Label(selectedCustomer.getId()), 1, 0);
                grid.add(new Label("Account Type:"), 0, 1);
                grid.add(cbAccountType, 1, 1);
                grid.add(new Label("Initial Deposit:"), 0, 2);
                grid.add(txtInitialDeposit, 1, 2);

                dialog.getDialogPane().setContent(grid);

                dialog.setResultConverter(dialogButton -> {
                        if (dialogButton == createButtonType) {
                                String accountType = cbAccountType.getValue();
                                String depositText = txtInitialDeposit.getText();
                                if (accountType != null && !accountType.isEmpty() && depositText != null && !depositText.isEmpty()) {
                                        try {
                                                double initialDeposit = Double.parseDouble(depositText);
                                                
                                                // Validate minimum balance for InvestmentAccount
                                                if ("InvestmentAccount".equals(accountType) && initialDeposit < 500.0) {
                                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                                        alert.setTitle("Error");
                                                        alert.setHeaderText(null);
                                                        alert.setContentText("Investment account requires minimum BWP500.00");
                                                        alert.showAndWait();
                                                        return null;
                                                }
                                                
                                                Account newAccount = null;
                                                String accountNumber = generateAccountNumber(accountType);
                                                
                                                System.out.println("DEBUG: Creating account " + accountNumber + " for customer " + selectedCustomer.getId());
                                                
                                                if ("ChequingAccount".equals(accountType)) {
                                                        newAccount = new ChequingAccount(accountNumber, initialDeposit, "MainBranch", selectedCustomer);
                                                } else if ("SavingsAccount".equals(accountType)) {
                                                        newAccount = new SavingsAccount(accountNumber, initialDeposit, "MainBranch", selectedCustomer);
                                                } else if ("InvestmentAccount".equals(accountType)) {
                                                        newAccount = new InvestmentAccount(accountNumber, initialDeposit, "MainBranch", selectedCustomer);
                                                }
                                                
                                                if (newAccount != null) {
                                                        System.out.println("DEBUG: Account object created, calling controller.openAccount()");
                                                        controller.openAccount(newAccount);
                                                        refreshAccountTable();
                                                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                                        alert.setTitle("Success");
                                                        alert.setHeaderText(null);
                                                        alert.setContentText("Account " + accountNumber + " created successfully for customer " + selectedCustomer.getId() + "!");
                                                        alert.showAndWait();
                                                        System.out.println("DEBUG: Account creation completed");
                                                }
                                        } catch (NumberFormatException e) {
                                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                                alert.setTitle("Error");
                                                alert.setHeaderText(null);
                                                alert.setContentText("Please enter a valid amount for initial deposit.");
                                                alert.showAndWait();
                                        } catch (Exception e) {
                                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                                alert.setTitle("Error");
                                                alert.setHeaderText(null);
                                                alert.setContentText("Failed to create account: " + e.getMessage());
                                                alert.showAndWait();
                                                e.printStackTrace();
                                        }
                                } else {
                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                        alert.setTitle("Error");
                                        alert.setHeaderText(null);
                                        alert.setContentText("Please select an account type and enter initial deposit.");
                                        alert.showAndWait();
                                }
                        }
                        return null;
                });

                dialog.showAndWait();
        }
}