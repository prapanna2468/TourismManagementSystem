package com.tourism.controllers;

import com.tourism.Main;
import com.tourism.models.*;
import com.tourism.utils.DialogUtils;
import com.tourism.utils.FileHandler;
import com.tourism.utils.LanguageManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class GuideDashboardController implements Initializable {
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
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("GuideDashboardController initialize() called");
        setupTableColumns();
        loadImportantUpdates();
        updateLanguage();
        
        // Initialize with default values
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome Guide!");
        }
        if (dashboardInfoLabel != null) {
            dashboardInfoLabel.setText("Loading guide information...");
        }
        if (earningsLabel != null) {
            earningsLabel.setText("Total Earnings: $0.00");
        }
        if (languagesLabel != null) {
            languagesLabel.setText("Languages: Loading...");
        }
        if (experienceLabel != null) {
            experienceLabel.setText("Experience: Loading...");
        }
    }
    
    public void setCurrentUser(Guide user) {
        System.out.println("Setting current user: " + user.getFullName());
        this.currentUser = user;
        initializeDashboard();
    }
    
    private void initializeDashboard() {
        try {
            if (currentUser == null) {
                System.err.println("Current user is null!");
                return;
            }
        
            System.out.println("Initializing guide dashboard for: " + currentUser.getFullName());
        
            // First load the latest guide data from file
            List<Guide> allGuides = FileHandler.loadGuides();
            Guide latestGuideData = allGuides.stream()
                .filter(g -> g.getUsername().equals(currentUser.getUsername()))
                .findFirst()
                .orElse(null);
        
            if (latestGuideData != null) {
                currentUser.setTotalEarnings(latestGuideData.getTotalEarnings());
                System.out.println("Loaded latest earnings: $" + currentUser.getTotalEarnings());
            }
        
            // Display user info using polymorphism
            if (welcomeLabel != null) {
                welcomeLabel.setText(LanguageManager.getText("Welcome") + ", " + currentUser.getFullName() + "!");
            }
        
            if (dashboardInfoLabel != null) {
                dashboardInfoLabel.setText(currentUser.getDashboardInfo());
            }
        
            // Display specific guide information
            if (earningsLabel != null) {
                earningsLabel.setText("Total Earnings: $" + String.format("%.2f", currentUser.getTotalEarnings()));
            }
        
            if (languagesLabel != null) {
                languagesLabel.setText("Languages: " + currentUser.getLanguagesString());
            }
        
            if (experienceLabel != null) {
                experienceLabel.setText("Experience: " + currentUser.getExperienceYears() + " years");
            }
        
            // Load assigned bookings
            loadAssignedBookings();
        
            System.out.println("Guide dashboard initialized successfully");
            System.out.println("Final earnings display: $" + currentUser.getTotalEarnings());
            System.out.println("Assigned bookings count: " + assignedBookings.size());
        
        } catch (Exception e) {
            System.err.println("Error initializing guide dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void setupTableColumns() {
        try {
            if (bookingIdColumn != null) {
                bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
            }
            if (touristColumn != null) {
                touristColumn.setCellValueFactory(new PropertyValueFactory<>("touristUsername"));
            }
            if (attractionColumn != null) {
                attractionColumn.setCellValueFactory(cellData -> {
                    if (cellData.getValue() != null && cellData.getValue().getAttraction() != null) {
                        return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAttraction().getName());
                    }
                    return new javafx.beans.property.SimpleStringProperty("N/A");
                });
            }
            if (dateColumn != null) {
                dateColumn.setCellValueFactory(new PropertyValueFactory<>("trekDate"));
            }
            if (difficultyColumn != null) {
                difficultyColumn.setCellValueFactory(cellData -> {
                    if (cellData.getValue() != null && cellData.getValue().getAttraction() != null) {
                        return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAttraction().getDifficulty());
                    }
                    return new javafx.beans.property.SimpleStringProperty("N/A");
                });
            }
        } catch (Exception e) {
            System.err.println("Error setting up table columns: " + e.getMessage());
        }
    }
    
    private void loadAssignedBookings() {
        try {
            if (currentUser == null) {
                System.err.println("Cannot load bookings - current user is null");
                return;
            }
        
            // Reload guide data to get latest earnings
            List<Guide> allGuides = FileHandler.loadGuides();
            Guide updatedGuide = allGuides.stream()
                .filter(g -> g.getUsername().equals(currentUser.getUsername()))
                .findFirst()
                .orElse(null);
        
            if (updatedGuide != null) {
                currentUser.setTotalEarnings(updatedGuide.getTotalEarnings());
                System.out.println("Updated guide earnings: $" + currentUser.getTotalEarnings());
            }
        
            List<Booking> allBookings = FileHandler.loadBookings();
            
            // CLEAR existing bookings to prevent duplicates
            if (assignedBookings == null) {
                assignedBookings = FXCollections.observableArrayList();
            } else {
                assignedBookings.clear(); // Clear existing bookings
            }
        
            System.out.println("Loading bookings for guide: " + currentUser.getUsername());
            System.out.println("Total bookings in system: " + allBookings.size());
        
            for (Booking booking : allBookings) {
                System.out.println("Checking booking " + booking.getBookingId() + 
                    " - Guide: '" + booking.getGuideUsername() + "' - Status: " + booking.getStatus());
        
                if (booking.getGuideUsername() != null && 
                    booking.getGuideUsername().equals(currentUser.getUsername())) {
            
                    assignedBookings.add(booking);
                    System.out.println("✓ Added assigned booking: " + booking.getBookingId() + 
                        " for " + booking.getAttraction().getName());
                }
            }
        
            if (upcomingTreksTable != null) {
                upcomingTreksTable.setItems(assignedBookings);
            }
        
            System.out.println("Loaded " + assignedBookings.size() + " assigned bookings");
        
            // Update earnings display with latest data
            if (earningsLabel != null) {
                earningsLabel.setText("Total Earnings: $" + String.format("%.2f", currentUser.getTotalEarnings()));
            }
        
            // Update dashboard info
            if (dashboardInfoLabel != null) {
                dashboardInfoLabel.setText(currentUser.getDashboardInfo());
            }
        
        } catch (Exception e) {
            System.err.println("Error loading assigned bookings: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadImportantUpdates() {
        try {
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
            
            if (updatesTextArea != null) {
                updatesTextArea.setText(updates.toString());
                updatesTextArea.setEditable(false);
            }
        } catch (Exception e) {
            System.err.println("Error loading updates: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleRefresh() {
        try {
            System.out.println("Refreshing guide dashboard...");
            
            // Reload all data from files
            initializeDashboard();
            loadImportantUpdates();
        
            DialogUtils.showInfo("Success", "Dashboard refreshed successfully!\n" +
                "Earnings: $" + String.format("%.2f", currentUser.getTotalEarnings()) + "\n" +
                "Assigned Bookings: " + (assignedBookings != null ? assignedBookings.size() : 0));
        
        } catch (Exception e) {
            System.err.println("Error refreshing dashboard: " + e.getMessage());
            DialogUtils.showError("Error", "Failed to refresh dashboard");
        }
    }
    
    @FXML
    private void toggleLanguage() {
        try {
            LanguageManager.toggleLanguage();
            updateLanguage();
            initializeDashboard(); // Refresh dashboard with new language
        } catch (Exception e) {
            System.err.println("Error toggling language: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loader.load());
            
            // Use the new scene switching method to maintain full screen
            Main.switchScene(scene, "Journey - Nepal Tourism System");
            
        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void updateLanguage() {
        try {
            if (refreshButton != null) {
                refreshButton.setText(LanguageManager.getText("Refresh"));
            }
            if (logoutButton != null) {
                logoutButton.setText(LanguageManager.getText("Logout"));
            }
            if (languageToggleButton != null) {
                languageToggleButton.setText(LanguageManager.getCurrentLanguage());
            }
        } catch (Exception e) {
            System.err.println("Error updating language: " + e.getMessage());
        }
    }
}
