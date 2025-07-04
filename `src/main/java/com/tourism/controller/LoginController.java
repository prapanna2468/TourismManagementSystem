package com.tourism.controller;

import com.tourism.model.User;
import com.tourism.util.FileManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please fill in all fields!");
            return;
        }

        // Check admin credentials
        if (username.equals("Prapanna") && password.equals("123")) {
            openDashboard("admin", username);
            return;
        }

        // Check tourist credentials
        List<User> tourists = FileManager.loadUsers("Tourist");
        for (User tourist : tourists) {
            if (tourist.getUsername().equals(username) && 
                tourist.getPassword().equals(User.encryptPassword(password))) {
                openDashboard("tourist", username);
                return;
            }
        }

        // Check guide credentials
        List<User> guides = FileManager.loadUsers("Guide");
        for (User guide : guides) {
            if (guide.getUsername().equals(username) && 
                guide.getPassword().equals(User.encryptPassword(password))) {
                openDashboard("guide", username);
                return;
            }
        }

        showAlert("Login Failed", "Invalid username or password!");
    }

    @FXML
    private void handleRegister() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/register.fxml"));
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openDashboard(String role, String username) {
        try {
            String fxmlFile = "/fxml/" + role + "-dashboard.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Pass username to dashboard controller
            if (role.equals("tourist")) {
                TouristDashboardController controller = loader.getController();
                controller.setCurrentUser(username);
            } else if (role.equals("guide")) {
                GuideDashboardController controller = loader.getController();
                controller.setCurrentUser(username);
            }

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root, 1000, 700));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
