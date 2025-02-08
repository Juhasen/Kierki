package pl.juhas.kierki;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label welcomeText;

    private Client client;

    public void setClient(Client client) {
        this.client = client;
    }

    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        boolean areCredentialsCorrect = client.login(username, password);
        if (areCredentialsCorrect) {
            loadLobbyScene(username);
        } else {
            welcomeText.setText("Invalid credentials, please try again.");
        }
    }

    private void loadLobbyScene(String username) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("lobby.fxml"));
            Parent lobbyRoot = fxmlLoader.load();

            LobbyController lobbyController = fxmlLoader.getController();
            lobbyController.setClient(client);
            lobbyController.setUsername(username);

            Scene lobbyScene = new Scene(lobbyRoot);

            Stage stage = (Stage) welcomeText.getScene().getWindow();
            stage.setScene(lobbyScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onRegisterButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        boolean isRegistrationSuccessful = client.register(username, password);
        if (isRegistrationSuccessful) {
            welcomeText.setText("Registration successful, please log in.");
            usernameField.clear();
            passwordField.clear();
        } else {
            welcomeText.setText("Registration failed, please try again.");
        }
    }
}