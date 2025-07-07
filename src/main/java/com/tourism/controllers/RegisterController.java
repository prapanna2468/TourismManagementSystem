package com.tourism.controllers;

import com.tourism.Main;
import com.tourism.models.*;
import com.tourism.utils.DialogUtils;
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
    @FXML private Label nationalityHelpLabel;
    
    // Guide fields
    @FXML private TextField languagesField;
    @FXML private TextField experienceField;
    @FXML private Label languagesLabel;
    @FXML private Label experienceLabel;
    @FXML private Label languagesHelpLabel;
    @FXML private Label experienceHelpLabel;
    
    // UI Labels for translation
    @FXML private Label titleLabel;
    @FXML private Label basicInfoLabel;
    @FXML private Label usernameLabel;
    @FXML private Label passwordLabel;
    @FXML private Label fullNameLabel;
    @FXML private Label emailLabel;
    @FXML private Label phoneLabel;
    @FXML private Label accountTypeLabel;
    @FXML private Label roleLabel;
    @FXML private Label roleHelpLabel;
    @FXML private Label requiredFieldsLabel;
    @FXML private Label securityLabel;
    
    @FXML private Button registerButton;
    @FXML private Button backButton;
    @FXML private Button languageToggleButton;
    
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
            roleSpecificLabel.setText(LanguageManager.getText("Additional Information"));
        } else if (isGuide) {
            roleSpecificLabel.setText(LanguageManager.getText("Additional Information"));
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
            DialogUtils.showError("Error", "Username already exists! Please choose a different one.");
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
            
            DialogUtils.showInfo("Success", "Registration successful! You can now login with your credentials.");
            handleBack();
            
        } catch (NumberFormatException e) {
            DialogUtils.showError("Error", "Please enter a valid number for experience years!");
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtils.showError("Error", "Registration failed! Please check all fields and try again.");
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
            
            DialogUtils.showError("Error", "Please fill in all required fields!");
            return false;
        }
        
        // Validate email format
        String email = emailField.getText().trim();
        if (!email.contains("@") || !email.contains(".")) {
            DialogUtils.showError("Error", "Please enter a valid email address!");
            return false;
        }
        
        // Validate username length
        if (usernameField.getText().trim().length() < 3) {
            DialogUtils.showError("Error", "Username must be at least 3 characters long!");
            return false;
        }
        
        // Validate password length
        if (passwordField.getText().trim().length() < 3) {
            DialogUtils.showError("Error", "Password must be at least 3 characters long!");
            return false;
        }
        
        String role = roleComboBox.getValue();
        
        // Role-specific validation
        if ("Tourist".equals(role)) {
            if (nationalityField.getText().trim().isEmpty()) {
                DialogUtils.showError("Error", "Please enter your nationality!");
                return false;
            }
        }
        
        if ("Guide".equals(role)) {
            if (languagesField.getText().trim().isEmpty()) {
                DialogUtils.showError("Error", "Please enter the languages you speak!");
                return false;
            }
            
            if (experienceField.getText().trim().isEmpty()) {
                DialogUtils.showError("Error", "Please enter your years of experience!");
                return false;
            }
            
            try {
                int experience = Integer.parseInt(experienceField.getText().trim());
                if (experience < 0 || experience > 50) {
                    DialogUtils.showError("Error", "Please enter a valid experience between 0 and 50 years!");
                    return false;
                }
            } catch (NumberFormatException e) {
                DialogUtils.showError("Error", "Please enter a valid number for experience years!");
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
    
    @FXML
    private void toggleLanguage() {
        LanguageManager.toggleLanguage();
        updateLanguage();
        updateFieldPrompts();
        updateRoleComboBox();
    }
    
    private void updateLanguage() {
        // Update main labels
        titleLabel.setText(LanguageManager.getText("Create New Account - Journey"));
        basicInfoLabel.setText("📋 " + LanguageManager.getText("Basic Information"));
        usernameLabel.setText(LanguageManager.getText("Username") + " *");
        passwordLabel.setText(LanguageManager.getText("Password") + " *");
        fullNameLabel.setText(LanguageManager.getText("Full Name") + " *");
        emailLabel.setText(LanguageManager.getText("Email") + " *");
        phoneLabel.setText(LanguageManager.getText("Phone") + " *");
        accountTypeLabel.setText("👤 " + LanguageManager.getText("Account Type"));
        roleLabel.setText(LanguageManager.getText("Role") + " *");
        roleHelpLabel.setText(LanguageManager.getText("Choose 'Tourist' if you want to book treks, or 'Guide' if you want to offer guiding services"));
        
        // Update role-specific labels
        nationalityLabel.setText("🌍 " + LanguageManager.getText("Nationality") + " *");
        nationalityHelpLabel.setText(LanguageManager.getText("Enter your country of citizenship"));
        languagesLabel.setText("🗣️ " + LanguageManager.getText("Languages") + " *");
        languagesHelpLabel.setText(LanguageManager.getText("List all languages you can speak fluently (separate with commas)"));
        experienceLabel.setText("⭐ " + LanguageManager.getText("Experience") + " *");
        experienceHelpLabel.setText(LanguageManager.getText("How many years have you been working as a tour guide?"));
        
        // Update buttons
        registerButton.setText(LanguageManager.getText("Create Account"));
        backButton.setText(LanguageManager.getText("Back to Login"));
        languageToggleButton.setText(LanguageManager.getCurrentLanguage());
        
        // Update footer labels
        requiredFieldsLabel.setText("* " + LanguageManager.getText("Required fields"));
        securityLabel.setText(LanguageManager.getText("All information will be kept secure and confidential"));
    }
    
    private void updateFieldPrompts() {
        // Update field prompts
        usernameField.setPromptText(LanguageManager.getText("Enter unique username"));
        passwordField.setPromptText(LanguageManager.getText("Enter secure password"));
        fullNameField.setPromptText(LanguageManager.getText("Enter your complete name"));
        emailField.setPromptText(LanguageManager.getText("your.email@example.com"));
        phoneField.setPromptText(LanguageManager.getText("+977-XXXXXXXXX"));
        roleComboBox.setPromptText(LanguageManager.getText("Choose your account type"));
        nationalityField.setPromptText(LanguageManager.getText("e.g., Nepali, Indian, American, etc."));
        languagesField.setPromptText(LanguageManager.getText("English, Nepali, Hindi, Mandarin"));
        experienceField.setPromptText(LanguageManager.getText("Enter number of years (0-50)"));
    }
    
    private void updateRoleComboBox() {
        // Store current selection
        String currentSelection = roleComboBox.getValue();
        
        // Clear and update items
        roleComboBox.getItems().clear();
        roleComboBox.getItems().addAll(
            LanguageManager.getText("Tourist"),
            LanguageManager.getText("Guide")
        );
        
        // Restore selection if it was made
        if (currentSelection != null) {
            if ("Tourist".equals(currentSelection)) {
                roleComboBox.setValue(LanguageManager.getText("Tourist"));
            } else if ("Guide".equals(currentSelection)) {
                roleComboBox.setValue(LanguageManager.getText("Guide"));
            }
        }
    }
}
