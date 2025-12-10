package collab.controller;

import collab.ApiClient;
import collab.MainApp;
import collab.dto.AuthDto;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private ApiClient api;

    @FXML
    public void initialize() {
        api = MainApp.getApiClient();
    }

    @FXML
    public void onLogin(ActionEvent event) {
        try {
            AuthDto.LoginRequest req =
                    new AuthDto.LoginRequest(emailField.getText(), passwordField.getText());

            AuthDto.UserResponse user =
                    api.post("/api/auth/login", req, AuthDto.UserResponse.class);

            MainApp.setCurrentUser(user);
            MainApp.showMain();

        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Login failed: " + e.getMessage());
        }
    }

    @FXML
    public void onRegisterLink(ActionEvent event) {
        MainApp.showRegister();
    }
}
