package view;

import controller.BankingSystemController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Account;
import model.Transaction;

public class CustomerDashboardGUI {

    private VBox view;
    private BankingSystemController controller;
    private String customerId;
    private Stage stage;

    private ComboBox<Account> accountComboBox;
    private TableView<Transaction> transactionTable;
    private Label balanceLabel;
    private Label lblTitle;
    private Label lblSelectAccount;
    private Label lblTransactionHistory;

    public CustomerDashboardGUI(BankingSystemController controller, Stage stage, String customerId) {
        this.controller = controller;
        this.stage = stage;
        this.customerId = customerId;

        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        // Load accounts after all components are initialized
        refreshAccountComboBox();
    }

    private void initializeComponents() {
        view = new VBox(15);
        view.setPadding(new Insets(20));

        lblTitle = new Label("Customer Dashboard - Welcome " + customerId);
        
        // Account Selection
        lblSelectAccount = new Label("Select Account:");
        accountComboBox = new ComboBox<>();
        accountComboBox.setPromptText("Choose an account");
        accountComboBox.setPrefWidth(300);
        
        // Balance Display - MUST be initialized before refreshAccountComboBox is called
        balanceLabel = new Label("Balance: BWP 0.00");
        balanceLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Transaction Table
        transactionTable = new TableView<>();
        setupTransactionTable();
        
        lblTransactionHistory = new Label("Transaction History:");
    }

    private void setupTransactionTable() {
        TableColumn<Transaction, String> colTxnId = new TableColumn<>("Transaction ID");
        colTxnId.setCellValueFactory(
                c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTransactionId()));
        
        TableColumn<Transaction, String> colTxnType = new TableColumn<>("Type");
        colTxnType.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getType()));
        
        TableColumn<Transaction, String> colAmount = new TableColumn<>("Amount");
        colAmount.setCellValueFactory(
                c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getAmount())));
        
        TableColumn<Transaction, String> colDate = new TableColumn<>("Date/Time");
        colDate.setCellValueFactory(
                c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTimestamp().toString()));
        
        // Add columns individually to avoid generic array warning
        transactionTable.getColumns().add(colTxnId);
        transactionTable.getColumns().add(colTxnType);
        transactionTable.getColumns().add(colAmount);
        transactionTable.getColumns().add(colDate);
    }

    private void setupLayout() {
        // Set cell factory for account combo box
        accountComboBox.setCellFactory(param -> new ListCell<Account>() {
            @Override
            protected void updateItem(Account account, boolean empty) {
                super.updateItem(account, empty);
                if (empty || account == null) {
                    setText(null);
                } else {
                    setText(account.getAccountNumber() + " - " + 
                           account.getClass().getSimpleName() + " - BWP" + 
                           String.format("%.2f", account.getBalance()));
                }
            }
        });
        
        accountComboBox.setButtonCell(new ListCell<Account>() {
            @Override
            protected void updateItem(Account account, boolean empty) {
                super.updateItem(account, empty);
                if (empty || account == null) {
                    setText("Select an account");
                } else {
                    setText(account.getAccountNumber() + " - " + 
                           account.getClass().getSimpleName() + " - BWP" + 
                           String.format("%.2f", account.getBalance()));
                }
            }
        });

        // Buttons
        Button btnRefresh = new Button("Refresh");
        btnRefresh.setOnAction(e -> refreshAccountComboBox());

        Button btnDeposit = new Button("Deposit");
        btnDeposit.setOnAction(e -> showDepositDialog());

        Button btnWithdraw = new Button("Withdraw");
        btnWithdraw.setOnAction(e -> showWithdrawDialog());

        Button btnTransfer = new Button("Transfer Funds");
        btnTransfer.setOnAction(e -> showTransferDialog());

        Button btnInterest = new Button("Apply Interest");
        btnInterest.setOnAction(e -> applyInterest());

        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> {
            LoginGUI loginGUI = new LoginGUI(stage);
            stage.getScene().setRoot(loginGUI.getView());
        });

        HBox buttonBox = new HBox(10, btnRefresh, btnDeposit, btnWithdraw, btnTransfer, btnInterest, btnLogout);

        // Add all components to view
        view.getChildren().addAll(
            lblTitle,
            new HBox(10, lblSelectAccount, accountComboBox),
            balanceLabel,
            buttonBox,
            lblTransactionHistory,
            transactionTable
        );
    }

    private void setupEventHandlers() {
        // Update display when account selection changes
        accountComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateAccountDisplay(newVal);
            } else {
                // Clear display when no account is selected
                balanceLabel.setText("Balance: BWP 0.00");
                transactionTable.setItems(FXCollections.observableArrayList());
            }
        });
    }

    // --- Refresh Account ComboBox ---
    private void refreshAccountComboBox() {
        System.out.println("DEBUG: Loading accounts for customer: " + customerId);
        ObservableList<Account> accounts = FXCollections.observableArrayList(
            controller.getCustomerAccounts(customerId)
        );
        
        System.out.println("DEBUG: Found " + accounts.size() + " accounts for customer " + customerId);
        for (Account acc : accounts) {
            System.out.println("DEBUG: Account " + acc.getAccountNumber() + " - Balance: " + acc.getBalance());
        }
        
        accountComboBox.setItems(accounts);
        
        if (!accounts.isEmpty()) {
            Account currentSelection = accountComboBox.getValue();
            if (currentSelection == null) {
                accountComboBox.getSelectionModel().selectFirst();
            } else {
                // Refresh the current selection data
                updateAccountDisplay(currentSelection);
            }
        } else {
            // Safe check - ensure balanceLabel is initialized
            if (balanceLabel != null) {
                balanceLabel.setText("No accounts found");
            }
            transactionTable.setItems(FXCollections.observableArrayList());
        }
    }

    // --- Update Display for Selected Account ---
    private void updateAccountDisplay(Account account) {
        if (account != null && balanceLabel != null) {
            balanceLabel.setText("Balance: BWP " + String.format("%.2f", account.getBalance()));
            
            // Load transactions for this account
            ObservableList<Transaction> txnList = FXCollections
                .observableArrayList(controller.getAccountTransactions(account.getAccountNumber()));
            transactionTable.setItems(txnList);
        }
    }

    // --- Deposit Dialog ---
    private void showDepositDialog() {
        Account selected = accountComboBox.getValue();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an account to deposit to.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Deposit");
        dialog.setHeaderText("Deposit to account: " + selected.getAccountNumber());
        dialog.setContentText("Amount:");
        dialog.showAndWait().ifPresent(amountStr -> {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Please enter a positive amount.");
                    return;
                }
                
                controller.depositToAccount(selected.getAccountNumber(), amount);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Deposit successful!");
                refreshAccountComboBox();
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Please enter a valid amount.");
            }
        });
    }

    // --- Withdraw Dialog ---
    private void showWithdrawDialog() {
        Account selected = accountComboBox.getValue();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an account to withdraw from.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Withdraw");
        dialog.setHeaderText("Withdraw from account: " + selected.getAccountNumber());
        dialog.setContentText("Amount:");
        dialog.showAndWait().ifPresent(amountStr -> {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Please enter a positive amount.");
                    return;
                }
                
                boolean success = controller.withdrawFromAccount(selected.getAccountNumber(), amount);
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Withdrawal successful!");
                    refreshAccountComboBox();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Withdrawal failed. Insufficient funds or account type doesn't allow withdrawals.");
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Please enter a valid amount.");
            }
        });
    }

    // --- Transfer Dialog ---
    private void showTransferDialog() {
        Account selectedFrom = accountComboBox.getValue();
        if (selectedFrom == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an account to transfer from.");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Transfer Funds");
        dialog.setHeaderText("Transfer from: " + selectedFrom.getAccountNumber());

        ButtonType transferButtonType = new ButtonType("Transfer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(transferButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField txtToAccount = new TextField();
        txtToAccount.setPromptText("To Account Number");
        TextField txtAmount = new TextField();
        txtAmount.setPromptText("Amount");

        grid.add(new Label("From Account:"), 0, 0);
        grid.add(new Label(selectedFrom.getAccountNumber()), 1, 0);
        grid.add(new Label("To Account:"), 0, 1);
        grid.add(txtToAccount, 1, 1);
        grid.add(new Label("Amount:"), 0, 2);
        grid.add(txtAmount, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == transferButtonType) {
                String toAcc = txtToAccount.getText();
                String amountStr = txtAmount.getText();
                if (toAcc != null && !toAcc.isEmpty() && amountStr != null && !amountStr.isEmpty()) {
                    try {
                        double amount = Double.parseDouble(amountStr);
                        if (amount <= 0) {
                            showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Please enter a positive amount.");
                            return null;
                        }
                        
                        boolean success = controller.transferFunds(selectedFrom.getAccountNumber(), toAcc, amount);
                        if (success) {
                            showAlert(Alert.AlertType.INFORMATION, "Transfer Successful", 
                                "Transferred BWP" + amount + " from " + selectedFrom.getAccountNumber() + " to " + toAcc);
                            refreshAccountComboBox();
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Transfer Failed", 
                                "Transfer failed. Check account numbers and balance.");
                        }
                    } catch (NumberFormatException e) {
                        showAlert(Alert.AlertType.ERROR, "Invalid Amount", "Please enter a valid amount.");
                    }
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    // --- Apply Interest ---
    private void applyInterest() {
        Account selected = accountComboBox.getValue();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an account to apply interest to.");
            return;
        }

        double interest = controller.applyInterest(selected.getAccountNumber());
        showAlert(Alert.AlertType.INFORMATION, "Interest Applied", 
            "Interest of BWP" + String.format("%.2f", interest) + " applied to account " + selected.getAccountNumber());
        refreshAccountComboBox();
    }

    // --- Helper method for alerts ---
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public VBox getView() {
        return view;
    }
}