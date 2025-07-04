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

public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Button registerButton;
    @FXML private Button backButton;

    @FXML
    private void initialize() {
        roleComboBox.getItems().addAll("Tourist", "Guide");
    }

    @FXML
    private void handleRegister() {
        if (!validateFields()) {
            return;
        }

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String role = roleComboBox.getValue();

        // Check if username already exists
        if (usernameExists(username)) {
            showAlert("Registration Failed", "Username already exists!");
            return;
        }

        User user = new User(username, password, fullName, email, phone, role);
        FileManager.saveUser(user);
        showAlert("Success", "Registration successful! You can now login.");
        goBackToLogin();
    }

    @FXML
    private void goBackToLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean validateFields() {
        if (usernameField.getText().trim().isEmpty() ||
            passwordField.getText().trim().isEmpty() ||
            fullNameField.getText().trim().isEmpty() ||
            emailField.getText().trim().isEmpty() ||
            phoneField.getText().trim().isEmpty() ||
            roleComboBox.getValue() == null) {
            showAlert("Validation Error", "Please fill in all required fields!");
            return false;
        }
        return true;
    }

    private boolean usernameExists(String username) {
        List<User> tourists = FileManager.loadUsers("Tourist");
        List<User> guides = FileManager.loadUsers("Guide");
        
        for (User user : tourists) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        
        for (User user : guides) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        
        return false;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
