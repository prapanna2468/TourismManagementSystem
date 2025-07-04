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
                default:
                    showAlert("Error", "Unknown user role: " + user.getRole());
                    return;
            }
        
            System.out.println("Loading dashboard for " + user.getRole() + ": " + fxmlFile);
        
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            if (loader.getLocation() == null) {
                showAlert("Error", "Dashboard file not found: " + fxmlFile);
                return;
            }
        
            Scene scene = new Scene(loader.load(), 1200, 800);
        
            // Get the controller and set user data
            Object controller = loader.getController();
            if (controller != null) {
                System.out.println("Controller loaded: " + controller.getClass().getSimpleName());
            
                if (user instanceof Tourist && controller instanceof TouristDashboardController) {
                    ((TouristDashboardController) controller).setCurrentUser((Tourist) user);
                } else if (user instanceof Guide && controller instanceof GuideDashboardController) {
                    ((GuideDashboardController) controller).setCurrentUser((Guide) user);
                } else if (user instanceof Admin && controller instanceof AdminDashboardController) {
                    ((AdminDashboardController) controller).setCurrentUser((Admin) user);
                } else {
                    System.err.println("Controller type mismatch: " + controller.getClass() + " for user " + user.getClass());
                }
            } else {
                System.err.println("Controller is null for " + fxmlFile);
            }
        
            Stage stage = (Stage) loginButton.getScene().getWindow();
        
            // Remember current state
            boolean wasMaximized = stage.isMaximized();
            boolean wasFullScreen = stage.isFullScreen();
        
            stage.setTitle(title);
            stage.setScene(scene);
        
            // Restore window state
            if (wasFullScreen) {
                stage.setFullScreen(true);
            } else if (wasMaximized) {
                stage.setMaximized(true);
            }
        
            // Add full screen toggle for dashboard
            scene.setOnKeyPressed(event -> {
                if (event.getCode() == javafx.scene.input.KeyCode.F11) {
                    stage.setFullScreen(!stage.isFullScreen());
                } else if (event.isAltDown() && event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                    stage.setFullScreen(!stage.isFullScreen());
                } else if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE && stage.isFullScreen()) {
                    stage.setFullScreen(false);
                }
            });
        
            System.out.println("Dashboard loaded successfully for " + user.getRole());
        
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open dashboard: " + e.getMessage());
            System.err.println("Dashboard loading error details:");
            System.err.println("User: " + user.getClass().getSimpleName());
            System.err.println("Role: " + user.getRole());
            System.err.println("Error: " + e.getMessage());
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


