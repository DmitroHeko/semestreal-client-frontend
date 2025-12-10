package collab.controller;

import collab.ApiClient;
import collab.MainApp;
import collab.dto.AuthDto;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label errorLabel;

    private ApiClient api;

    @FXML
    public void initialize() {
        api = MainApp.getApiClient();
    }

    @FXML
    public void onRegister(ActionEvent event) {
        String name = nameField.getText();
        String email = emailField.getText();
        String pwd = passwordField.getText();
        String pwd2 = confirmPasswordField.getText();

        if (name.isBlank() || email.isBlank() || pwd.isBlank()) {
            errorLabel.setText("Všetky polia sú povinné.");
            return;
        }
        if (!pwd.equals(pwd2)) {
            errorLabel.setText("Heslá sa nezhodujú.");
            return;
        }

        try {
            AuthDto.RegisterRequest req = new AuthDto.RegisterRequest(name, email, pwd);
            api.postNoResponse("/api/auth/register", req);
            errorLabel.setText("Registrácia úspešná, môžete sa prihlásiť.");
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Registrácia zlyhala: " + e.getMessage());
        }
    }

    @FXML
    public void onBackToLogin(ActionEvent event) {
        MainApp.showLogin();
    }
}
