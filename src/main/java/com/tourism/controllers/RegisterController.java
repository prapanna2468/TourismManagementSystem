package com.tourism.controllers;

import com.tourism.models.*;
import com.tourism.utils.FileHandler;
import com.tourism.utils.LanguageManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.Arrays;
import java.util.List;

public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private TextField nationalityField;
    @FXML private TextField languagesField;
    @FXML private TextField experienceField;
    @FXML private Button registerButton;
    @FXML private Button backButton;
    @FXML private Label nationalityLabel;
    @FXML private Label languagesLabel;
    @FXML private Label experienceLabel;
    
    @FXML
    private void initialize() {
        roleComboBox.getItems().addAll("Tourist", "Guide");
        roleComboBox.setOnAction(e -> toggleRoleFields());
        updateLanguage();
    }
    
    private void toggleRoleFields() {
        String selectedRole = roleComboBox.getValue();
        boolean isTourist = "Tourist".equals(selectedRole);
        
        nationalityField.setVisible(isTourist);
        nationalityLabel.setVisible(isTourist);
        
        languagesField.setVisible(!isTourist);
        languagesLabel.setVisible(!isTourist);
        experienceField.setVisible(!isTourist);
        experienceLabel.setVisible(!isTourist);
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
        if (isUsernameExists(username)) {
            showAlert("Error", "Username already exists! Please choose a different one.");
            return;
        }
        
        try {
            if ("Tourist".equals(role)) {
                String nationality = nationalityField.getText().trim();
                Tourist tourist = new Tourist(username, password, fullName, email, phone, nationality);
                FileHandler.saveTourist(tourist);
                
            } else if ("Guide".equals(role)) {
                String languagesStr = languagesField.getText().trim();
                int experience = Integer.parseInt(experienceField.getText().trim());
                List<String> languages = Arrays.asList(languagesStr.split(",\\s*"));
                
                Guide guide = new Guide(username, password, fullName, email, phone, languages, experience);
                FileHandler.saveGuide(guide);
            }
            
            showAlert("Success", "Registration successful! You can now login.");
            handleBack();
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Registration failed! Please try again.");
        }
    }
    
    private boolean validateFields() {
        if (usernameField.getText().trim().isEmpty() ||
            passwordField.getText().trim().isEmpty() ||
            fullNameField.getText().trim().isEmpty() ||
            emailField.getText().trim().isEmpty() ||
            phoneField.getText().trim().isEmpty() ||
            roleComboBox.getValue() == null) {
            
            showAlert("Error", "Please fill in all required fields!");
            return false;
        }
        
        String role = roleComboBox.getValue();
        if ("Tourist".equals(role) && nationalityField.getText().trim().isEmpty()) {
            showAlert("Error", "Please enter your nationality!");
            return false;
        }
        
        if ("Guide".equals(role)) {
            if (languagesField.getText().trim().isEmpty()) {
                showAlert("Error", "Please enter languages you speak!");
                return false;
            }
            try {
                Integer.parseInt(experienceField.getText().trim());
            } catch (NumberFormatException e) {
                showAlert("Error", "Please enter valid experience years!");
                return false;
            }
        }
        
        return true;
    }
    
    private boolean isUsernameExists(String username) {
        // Check tourists
        List<Tourist> tourists = FileHandler.loadTourists();
        for (Tourist tourist : tourists) {
            if (tourist.getUsername().equals(username)) {
                return true;
            }
        }
        
        // Check guides
        List<Guide> guides = FileHandler.loadGuides();
        for (Guide guide : guides) {
            if (guide.getUsername().equals(username)) {
                return true;
            }
        }
        
        // Check admin username
        if ("Prapanna".equals(username)) {
            return true;
        }
        
        return false;
    }
    
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setTitle("Nepal Tourism Management System");
            stage.setScene(scene);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void updateLanguage() {
        // Update UI elements with current language
        registerButton.setText(LanguageManager.getText("Register"));
        backButton.setText(LanguageManager.getText("Back"));
        nationalityLabel.setText(LanguageManager.getText("Nationality"));
        languagesLabel.setText(LanguageManager.getText("Languages"));
        experienceLabel.setText(LanguageManager.getText("Experience"));
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(LanguageManager.getText(title));
        alert.setHeaderText(null);
        alert.setContentText(LanguageManager.getText(message));
        alert.showAndWait();
    }
}
