package com.tourism.controllers;

import com.tourism.Main;
import com.tourism.models.*;
import com.tourism.utils.FileHandler;
import com.tourism.utils.LanguageManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
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
    
    // Role-specific containers
    @FXML private VBox roleSpecificSection;
    @FXML private Label roleSpecificLabel;
    @FXML private VBox touristFields;
    @FXML private VBox guideFields;
    
    // Tourist fields
    @FXML private TextField nationalityField;
    @FXML private Label nationalityLabel;
    
    // Guide fields
    @FXML private TextField languagesField;
    @FXML private TextField experienceField;
    @FXML private Label languagesLabel;
    @FXML private Label experienceLabel;
    
    @FXML private Button registerButton;
    @FXML private Button backButton;
    
    @FXML
    private void initialize() {
        roleComboBox.getItems().addAll("Tourist", "Guide");
        roleComboBox.setOnAction(e -> toggleRoleFields());
        updateLanguage();
        
        // Initially hide role-specific section
        roleSpecificSection.setVisible(false);
        roleSpecificSection.setManaged(false);
    }
    
    private void toggleRoleFields() {
        String selectedRole = roleComboBox.getValue();
        
        if (selectedRole == null) {
            roleSpecificSection.setVisible(false);
            roleSpecificSection.setManaged(false);
            return;
        }
        
        // Show the role-specific section
        roleSpecificSection.setVisible(true);
        roleSpecificSection.setManaged(true);
        
        boolean isTourist = "Tourist".equals(selectedRole);
        boolean isGuide = "Guide".equals(selectedRole);
        
        // Show/hide tourist fields
        touristFields.setVisible(isTourist);
        touristFields.setManaged(isTourist);
        
        // Show/hide guide fields
        guideFields.setVisible(isGuide);
        guideFields.setManaged(isGuide);
        
        // Update section label
        if (isTourist) {
            roleSpecificLabel.setText("Tourist Information");
        } else if (isGuide) {
            roleSpecificLabel.setText("Guide Information");
        }
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
                
                // Parse languages - handle comma separation properly
                List<String> languages = Arrays.asList(languagesStr.split("\\s*,\\s*"));
                
                Guide guide = new Guide(username, password, fullName, email, phone, languages, experience);
                FileHandler.saveGuide(guide);
            }
            
            showAlert("Success", "Registration successful! You can now login with your credentials.");
            handleBack();
            
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid number for experience years!");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Registration failed! Please check all fields and try again.");
        }
    }
    
    private boolean validateFields() {
        // Check basic required fields
        if (usernameField.getText().trim().isEmpty() ||
            passwordField.getText().trim().isEmpty() ||
            fullNameField.getText().trim().isEmpty() ||
            emailField.getText().trim().isEmpty() ||
            phoneField.getText().trim().isEmpty() ||
            roleComboBox.getValue() == null) {
            
            showAlert("Error", "Please fill in all required fields!");
            return false;
        }
        
        // Validate email format
        String email = emailField.getText().trim();
        if (!email.contains("@") || !email.contains(".")) {
            showAlert("Error", "Please enter a valid email address!");
            return false;
        }
        
        // Validate username length
        if (usernameField.getText().trim().length() < 3) {
            showAlert("Error", "Username must be at least 3 characters long!");
            return false;
        }
        
        // Validate password length
        if (passwordField.getText().trim().length() < 3) {
            showAlert("Error", "Password must be at least 3 characters long!");
            return false;
        }
        
        String role = roleComboBox.getValue();
        
        // Role-specific validation
        if ("Tourist".equals(role)) {
            if (nationalityField.getText().trim().isEmpty()) {
                showAlert("Error", "Please enter your nationality!");
                return false;
            }
        }
        
        if ("Guide".equals(role)) {
            if (languagesField.getText().trim().isEmpty()) {
                showAlert("Error", "Please enter the languages you speak!");
                return false;
            }
            
            if (experienceField.getText().trim().isEmpty()) {
                showAlert("Error", "Please enter your years of experience!");
                return false;
            }
            
            try {
                int experience = Integer.parseInt(experienceField.getText().trim());
                if (experience < 0 || experience > 50) {
                    showAlert("Error", "Please enter a valid experience between 0 and 50 years!");
                    return false;
                }
            } catch (NumberFormatException e) {
                showAlert("Error", "Please enter a valid number for experience years!");
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
            Scene scene = new Scene(loader.load());
            
            // Use the new scene switching method to maintain full screen
            Main.switchScene(scene, "Journey - Nepal Tourism System");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void updateLanguage() {
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
