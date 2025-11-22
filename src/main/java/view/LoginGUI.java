package view;

import controller.BankingSystemController;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class LoginGUI {

    private GridPane view;
    private BankingSystemController controller = new BankingSystemController();
    private Stage stage;

    public LoginGUI(Stage stage) {
        this.stage = stage;
        view = new GridPane();
        view.setPadding(new Insets(20));
        view.setHgap(10);
        view.setVgap(10);

        Label lblTitle = new Label("Banking System Login");
        lblTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Label lblUsername = new Label("Username:");
        TextField txtUsername = new TextField();
        txtUsername.setPromptText("Enter username");

        Label lblPassword = new Label("Password:");
        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Enter password");

        Button btnLogin = new Button("Login");
        Label lblMessage = new Label();
        
        // Test credentials helper
        Label lblTestAccounts = new Label("Test Accounts:\nAdmin: admin/admin123\nEmployee: employee/emp123\nCustomer: gsewa/pass123");
        lblTestAccounts.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

        btnLogin.setOnAction(e -> {
            String username = txtUsername.getText().trim();
            String password = txtPassword.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                lblMessage.setText("Please enter both username and password!");
                lblMessage.setStyle("-fx-text-fill: red;");
                return;
            }

            System.out.println("DEBUG: Login attempt for username: " + username);
            
            boolean success = controller.login(username, password);
            if (success) {
                lblMessage.setText("Login Successful!");
                lblMessage.setStyle("-fx-text-fill: green;");
                System.out.println("DEBUG: Login successful for user: " + username);

                // Clear the login form
                txtUsername.clear();
                txtPassword.clear();

                // Route to appropriate dashboard based on user role
                try {
                    if (username.equalsIgnoreCase("admin")) {
                        System.out.println("DEBUG: Routing to Admin Dashboard");
                        AdminDashboardGUI admin = new AdminDashboardGUI(controller, stage);
                        stage.getScene().setRoot(admin.getView());
                    } else if (username.toLowerCase().contains("emp") || username.equalsIgnoreCase("employee") || username.equalsIgnoreCase("employee2")) {
                        System.out.println("DEBUG: Routing to Employee Dashboard");
                        EmployeeDashboardGUI emp = new EmployeeDashboardGUI(controller, stage);
                        stage.getScene().setRoot(emp.getView());
                    } else {
                        // For all other users, assume they are customers
                        System.out.println("DEBUG: Routing to Customer Dashboard for user: " + username);
                        CustomerDashboardGUI customer = new CustomerDashboardGUI(controller, stage, username);
                        stage.getScene().setRoot(customer.getView());
                    }
                    
                    // Resize stage to fit new content
                    stage.sizeToScene();
                    
                } catch (Exception ex) {
                    System.err.println("ERROR: Failed to load dashboard: " + ex.getMessage());
                    ex.printStackTrace();
                    lblMessage.setText("Error loading dashboard. Please try again.");
                    lblMessage.setStyle("-fx-text-fill: red;");
                }

            } else {
                lblMessage.setText("Invalid username or password!");
                lblMessage.setStyle("-fx-text-fill: red;");
                System.out.println("DEBUG: Login failed for user: " + username);
            }
        });

        // Add Enter key support for login
        txtPassword.setOnAction(btnLogin.getOnAction());

        view.add(lblTitle, 0, 0, 2, 1);
        view.add(lblUsername, 0, 1);
        view.add(txtUsername, 1, 1);
        view.add(lblPassword, 0, 2);
        view.add(txtPassword, 1, 2);
        view.add(btnLogin, 1, 3);
        view.add(lblMessage, 1, 4);
        view.add(lblTestAccounts, 0, 5, 2, 1);
    }

    public GridPane getView() {
        return view;
    }
}