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

    private TableView<Account> accountTable;
    private TableView<Transaction> transactionTable;

    public CustomerDashboardGUI(BankingSystemController controller, Stage stage, String customerId) {
        this.controller = controller;
        this.customerId = customerId;

        view = new VBox(15);
        view.setPadding(new Insets(20));

        Label lblTitle = new Label("Customer Dashboard");

        // --- Account Table ---
        accountTable = new TableView<>();
        TableColumn<Account, String> colAccNum = new TableColumn<>("Account Number");
        colAccNum.setCellValueFactory(
                c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getAccountNumber()));
        TableColumn<Account, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(
                c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getClass().getSimpleName()));
        TableColumn<Account, String> colBalance = new TableColumn<>("Balance");
        colBalance.setCellValueFactory(
                c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getBalance())));
        accountTable.getColumns().addAll(colAccNum, colType, colBalance);

        accountTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null)
                refreshTransactionTable(newSelection);
        });

        refreshAccountTable();

        // --- Transaction Table ---
        transactionTable = new TableView<>();
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
        transactionTable.getColumns().addAll(colTxnId, colTxnType, colAmount, colDate);

        // --- Buttons ---
        Button btnRefresh = new Button("Refresh");
        btnRefresh.setOnAction(e -> refreshAccountTable());

        Button btnDeposit = new Button("Deposit");
        btnDeposit.setOnAction(e -> showDepositDialog());

        Button btnWithdraw = new Button("Withdraw");
        btnWithdraw.setOnAction(e -> showWithdrawDialog());

        Button btnTransfer = new Button("Transfer Funds");
        btnTransfer.setOnAction(e -> showTransferDialog());

        Button btnInterest = new Button("Apply Interest");
        btnInterest.setOnAction(e -> applyInterest());

        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> stage.getScene().setRoot(new LoginGUI(stage).getView()));

        HBox buttonBox = new HBox(10, btnRefresh, btnDeposit, btnWithdraw, btnTransfer, btnInterest, btnLogout);

        view.getChildren().addAll(lblTitle, accountTable, buttonBox, new Label("Transaction History:"),
                transactionTable);
    }

    // --- Refresh Account Table ---
    private void refreshAccountTable() {
        Account selectedAccount = accountTable.getSelectionModel().getSelectedItem();
        String selectedAccountNumber = selectedAccount != null ? selectedAccount.getAccountNumber() : null;

        ObservableList<Account> list = FXCollections.observableArrayList(controller.getCustomerAccounts(customerId));
        accountTable.setItems(list);

        if (!list.isEmpty()) {
            if (selectedAccountNumber != null) {
                // Re-select the previously selected account if it still exists
                for (Account acc : list) {
                    if (acc.getAccountNumber().equals(selectedAccountNumber)) {
                        accountTable.getSelectionModel().select(acc);
                        refreshTransactionTable(acc);
                        return;
                    }
                }
            }
            // If no previous selection or not found, select the first account
            refreshTransactionTable(list.get(0));
        }
    }

    // --- Refresh Transaction Table ---
    private void refreshTransactionTable(Account account) {
        ObservableList<Transaction> txnList = FXCollections
                .observableArrayList(controller.getAccountTransactions(account.getAccountNumber()));
        transactionTable.setItems(txnList);
    }

    // --- Deposit Dialog ---
    private void showDepositDialog() {
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
        dialog.setTitle("Deposit");
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
                for (Account acc : accountTable.getItems()) {
                    if (acc.getAccountNumber().equals(selectedAccountNumber)) {
                        accountTable.getSelectionModel().select(acc);
                        refreshTransactionTable(acc);
                        break;
                    }
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

    // --- Withdraw Dialog ---
    private void showWithdrawDialog() {
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
        dialog.setTitle("Withdraw");
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
                    for (Account acc : accountTable.getItems()) {
                        if (acc.getAccountNumber().equals(selectedAccountNumber)) {
                            accountTable.getSelectionModel().select(acc);
                            refreshTransactionTable(acc);
                            break;
                        }
                    }
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

    // --- Transfer Dialog ---
    private void showTransferDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Transfer Funds");
        dialog.setHeaderText("Transfer between your accounts");

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
                if (fromAcc != null && !fromAcc.isEmpty() && toAcc != null && !toAcc.isEmpty() && amountStr != null
                        && !amountStr.isEmpty()) {
                    try {
                        double amount = Double.parseDouble(amountStr);
                        boolean success = controller.transferFunds(fromAcc, toAcc, amount);
                        if (success) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Transfer Successful");
                            alert.setHeaderText(null);
                            alert.setContentText("Transferred " + amount + " from " + fromAcc + " to " + toAcc);
                            alert.showAndWait();
                            refreshAccountTable();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Transfer Failed");
                            alert.setHeaderText(null);
                            alert.setContentText("Transfer failed. Check account numbers and balance.");
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

    // --- Apply Interest ---
    private void applyInterest() {
        Account selected = accountTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select an account to apply interest to.");
            alert.showAndWait();
            return;
        }

        double interest = controller.applyInterest(selected.getAccountNumber());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Interest Applied");
        alert.setHeaderText(null);
        alert.setContentText("Interest of " + interest + " applied to account " + selected.getAccountNumber());
        alert.showAndWait();

        String selectedAccountNumber = selected.getAccountNumber();
        refreshAccountTable();
        // Re-select the account to update transactions
        for (Account acc : accountTable.getItems()) {
            if (acc.getAccountNumber().equals(selectedAccountNumber)) {
                accountTable.getSelectionModel().select(acc);
                refreshTransactionTable(acc);
                break;
            }
        }
    }

    public VBox getView() {
        return view;
    }
}
