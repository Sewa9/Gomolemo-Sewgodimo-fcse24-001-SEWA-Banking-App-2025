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

        Label lblUsername = new Label("Username:");
        TextField txtUsername = new TextField();

        Label lblPassword = new Label("Password:");
        PasswordField txtPassword = new PasswordField();

        Button btnLogin = new Button("Login");
        Label lblMessage = new Label();

        btnLogin.setOnAction(e -> {
            String username = txtUsername.getText();
            String password = txtPassword.getText();

            boolean success = controller.login(username, password);
            if (success) {
                lblMessage.setText("Login Successful!");

                // Route to dashboard
                if (username.equalsIgnoreCase("admin")) {
                    AdminDashboardGUI admin = new AdminDashboardGUI(controller, stage);
                    stage.getScene().setRoot(admin.getView());
                } else if (username.toLowerCase().contains("emp")) {
                    EmployeeDashboardGUI emp = new EmployeeDashboardGUI(controller, stage);
                    stage.getScene().setRoot(emp.getView());
                } else {
                    CustomerDashboardGUI customer = new CustomerDashboardGUI(controller, stage, username);
                    stage.getScene().setRoot(customer.getView());
                }

            } else {
                lblMessage.setText("Invalid username or password!");
            }
        });

        view.add(lblUsername, 0, 0);
        view.add(txtUsername, 1, 0);
        view.add(lblPassword, 0, 1);
        view.add(txtPassword, 1, 1);
        view.add(btnLogin, 1, 2);
        view.add(lblMessage, 1, 3);
    }

    public GridPane getView() {
        return view;
    }
}




          


