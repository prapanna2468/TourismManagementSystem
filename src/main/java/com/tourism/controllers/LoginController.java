package com.tourism.controllers;

import com.tourism.models.*;
import com.tourism.utils.FileHandler;
import com.tourism.utils.LanguageManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.List;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Button languageToggleButton;
    @FXML private Label titleLabel;
    @FXML private Label usernameLabel;
    @FXML private Label passwordLabel;
    
    @FXML
    private void initialize() {
        updateLanguage();
    }
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please fill in all fields!");
            return;
        }
        
        // Check admin login (hardcoded)
        if ("Prapanna".equals(username) && "123".equals(password)) {
            Admin admin = new Admin(username, password, "Prapanna Admin", "admin@tourism.com", "+977-9999999");
            openDashboard(admin);
            return;
        }
        
        // Check tourist login
        List<Tourist> tourists = FileHandler.loadTourists();
        for (Tourist tourist : tourists) {
            if (tourist.getUsername().equals(username) && tourist.getPassword().equals(password)) {
                openDashboard(tourist);
                return;
            }
        }
        
        // Check guide login
        List<Guide> guides = FileHandler.loadGuides();
        for (Guide guide : guides) {
            if (guide.getUsername().equals(username) && guide.getPassword().equals(password)) {
                openDashboard(guide);
                return;
            }
        }
        
        showAlert("Error", "Invalid username or password!");
    }
    
    private void openDashboard(Person user) {
        try {
            String fxmlFile = "";
            String title = "";
            
            // Polymorphism in action - using the same method for different user types
            switch (user.getRole()) {
                case "Tourist":
                    fxmlFile = "/fxml/touristDashboard.fxml";
                    title = "Tourist Dashboard - Nepal Tourism";
                    break;
                case "Guide":
                    fxmlFile = "/fxml/guideDashboard.fxml";
                    title = "Guide Dashboard - Nepal Tourism";
                    break;
                case "Admin":
                    fxmlFile = "/fxml/adminDashboard.fxml";
                    title = "Admin Dashboard - Nepal Tourism";
                    break;
            }
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load(), 1000, 700);
            
            // Pass user data to the controller
            if (user instanceof Tourist) {
                TouristDashboardController controller = loader.getController();
                controller.setCurrentUser((Tourist) user);
            } else if (user instanceof Guide) {
                GuideDashboardController controller = loader.getController();
                controller.setCurrentUser((Guide) user);
            } else if (user instanceof Admin) {
                AdminDashboardController controller = loader.getController();
                controller.setCurrentUser((Admin) user);
            }
            
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(scene);
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open dashboard!");
        }
    }
    
    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
            Scene scene = new Scene(loader.load(), 600, 500);
            
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.setTitle("Register - Nepal Tourism");
            stage.setScene(scene);
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open registration form!");
        }
    }
    
    @FXML
    private void toggleLanguage() {
        LanguageManager.toggleLanguage();
        updateLanguage();
    }
    
    private void updateLanguage() {
        titleLabel.setText(LanguageManager.getText("Nepal Tourism Management System"));
        usernameLabel.setText(LanguageManager.getText("Username"));
        passwordLabel.setText(LanguageManager.getText("Password"));
        loginButton.setText(LanguageManager.getText("Login"));
        registerButton.setText(LanguageManager.getText("Register"));
        languageToggleButton.setText(LanguageManager.getCurrentLanguage());
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(LanguageManager.getText(title));
        alert.setHeaderText(null);
        alert.setContentText(LanguageManager.getText(message));
        alert.showAndWait();
    }
}
