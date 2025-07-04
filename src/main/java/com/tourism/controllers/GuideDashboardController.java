package com.tourism.controllers;

import com.tourism.models.*;
import com.tourism.utils.FileHandler;
import com.tourism.utils.LanguageManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.List;

public class GuideDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Label dashboardInfoLabel;
    @FXML private Label earningsLabel;
    @FXML private Label languagesLabel;
    @FXML private Label experienceLabel;
    @FXML private TableView<Booking> upcomingTreksTable;
    @FXML private TableColumn<Booking, Integer> bookingIdColumn;
    @FXML private TableColumn<Booking, String> touristColumn;
    @FXML private TableColumn<Booking, String> attractionColumn;
    @FXML private TableColumn<Booking, LocalDate> dateColumn;
    @FXML private TableColumn<Booking, String> difficultyColumn;
    @FXML private TextArea updatesTextArea;
    @FXML private Button languageToggleButton;
    @FXML private Button logoutButton;
    @FXML private Button refreshButton;
    
    private Guide currentUser;
    private ObservableList<Booking> assignedBookings;
    
    public void setCurrentUser(Guide user) {
        this.currentUser = user;
        System.out.println("Setting current user: " + user.getFullName() + " (" + user.getRole() + ")");
        
        // Initialize collections if null
        if (this.currentUser.getAssignedBookings() == null) {
            // This shouldn't happen, but let's be safe
            System.out.println("Warning: Guide assigned bookings list was null, initializing...");
        }
        
        initializeDashboard();
    }
    
    @FXML
    private void initialize() {
        setupTableColumns();
        loadImportantUpdates();
        updateLanguage();
    }
    
    private void initializeDashboard() {
        try {
            System.out.println("Initializing guide dashboard for: " + currentUser.getFullName());
            
            // Display user info using polymorphism
            welcomeLabel.setText(LanguageManager.getText("Welcome") + ", " + currentUser.getFullName() + "!");
            dashboardInfoLabel.setText(currentUser.getDashboardInfo());
            
            // Display specific guide information
            earningsLabel.setText("Total Earnings: $" + String.format("%.2f", currentUser.getTotalEarnings()));
            languagesLabel.setText("Languages: " + currentUser.getLanguagesString());
            experienceLabel.setText("Experience: " + currentUser.getExperienceYears() + " years");
            
            loadAssignedBookings();
            
            System.out.println("Guide dashboard initialized successfully");
            
        } catch (Exception e) {
            System.err.println("Error initializing guide dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void setupTableColumns() {
        bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        touristColumn.setCellValueFactory(new PropertyValueFactory<>("touristUsername"));
        attractionColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAttraction().getName()));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("trekDate"));
        difficultyColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAttraction().getDifficulty()));
    }
    
    private void loadAssignedBookings() {
        try {
            List<Booking> allBookings = FileHandler.loadBookings();
            assignedBookings = FXCollections.observableArrayList();
            
            System.out.println("Loading bookings for guide: " + currentUser.getUsername());
            System.out.println("Total bookings in system: " + allBookings.size());
            
            for (Booking booking : allBookings) {
                if (booking.getGuideUsername() != null && 
                    booking.getGuideUsername().equals(currentUser.getUsername()) && 
                    booking.isUpcoming()) {
                    assignedBookings.add(booking);
                    System.out.println("Found assigned booking: " + booking.getBookingId());
                }
            }
            
            upcomingTreksTable.setItems(assignedBookings);
            
            System.out.println("Loaded " + assignedBookings.size() + " assigned bookings");
            
            // Update earnings display
            earningsLabel.setText("Total Earnings: $" + String.format("%.2f", currentUser.getTotalEarnings()));
            
        } catch (Exception e) {
            System.err.println("Error loading assigned bookings: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadImportantUpdates() {
        StringBuilder updates = new StringBuilder();
        updates.append("🌦️ WEATHER ALERTS:\n");
        updates.append("• Heavy snow expected on Everest Base Camp trek (Next 3 days)\n");
        updates.append("• Clear weather forecast for Annapurna Circuit\n");
        updates.append("• Monsoon season ending - Good trekking conditions ahead\n\n");
        
        updates.append("⚠️ SAFETY NOTICES:\n");
        updates.append("• Altitude sickness precautions for high-altitude treks\n");
        updates.append("• Emergency helicopter services available 24/7\n");
        updates.append("• Carry proper gear and first aid supplies\n\n");
        
        updates.append("📢 IMPORTANT ANNOUNCEMENTS:\n");
        updates.append("• Festival season discounts active (August-October)\n");
        updates.append("• New safety protocols for COVID-19\n");
        updates.append("• Guide training workshop scheduled next month\n");
        
        updatesTextArea.setText(updates.toString());
        updatesTextArea.setEditable(false);
    }
    
    @FXML
    private void handleRefresh() {
        loadAssignedBookings();
        loadImportantUpdates();
        dashboardInfoLabel.setText(currentUser.getDashboardInfo());
        showAlert("Success", "Dashboard refreshed successfully!");
    }
    
    @FXML
    private void toggleLanguage() {
        LanguageManager.toggleLanguage();
        updateLanguage();
        initializeDashboard(); // Refresh dashboard with new language
    }
    
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setTitle("Nepal Tourism Management System");
            stage.setScene(scene);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void updateLanguage() {
        refreshButton.setText(LanguageManager.getText("Refresh"));
        logoutButton.setText(LanguageManager.getText("Logout"));
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
