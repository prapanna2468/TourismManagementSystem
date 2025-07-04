package com.tourism.controllers;

import com.tourism.models.*;
import com.tourism.utils.FileHandler;
import com.tourism.utils.LanguageManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class AdminDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Label dashboardInfoLabel;
    @FXML private TabPane mainTabPane;
    
    // Guide Management Tab
    @FXML private TableView<Guide> guidesTable;
    @FXML private TableColumn<Guide, String> guideUsernameColumn;
    @FXML private TableColumn<Guide, String> guideNameColumn;
    @FXML private TableColumn<Guide, String> guideLanguagesColumn;
    @FXML private TableColumn<Guide, Integer> guideExperienceColumn;
    @FXML private TextField guideUsernameField;
    @FXML private TextField guidePasswordField;
    @FXML private TextField guideNameField;
    @FXML private TextField guideEmailField;
    @FXML private TextField guidePhoneField;
    @FXML private TextField guideLanguagesField;
    @FXML private TextField guideExperienceField;
    @FXML private Button addGuideButton;
    @FXML private Button updateGuideButton;
    @FXML private Button deleteGuideButton;
    
    // Attraction Management Tab
    @FXML private TableView<Attraction> attractionsTable;
    @FXML private TableColumn<Attraction, String> attractionNameColumn;
    @FXML private TableColumn<Attraction, String> attractionLocationColumn;
    @FXML private TableColumn<Attraction, String> attractionAltitudeColumn;
    @FXML private TableColumn<Attraction, String> attractionDifficultyColumn;
    @FXML private TableColumn<Attraction, Double> attractionPriceColumn;
    @FXML private TextField attractionNameField;
    @FXML private TextField attractionLocationField;
    @FXML private ComboBox<String> attractionAltitudeCombo;
    @FXML private ComboBox<String> attractionDifficultyCombo;
    @FXML private TextField attractionPriceField;
    @FXML private Button addAttractionButton;
    @FXML private Button updateAttractionButton;
    @FXML private Button deleteAttractionButton;
    
    // Booking Management Tab
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, Integer> bookingIdColumn;
    @FXML private TableColumn<Booking, String> bookingTouristColumn;
    @FXML private TableColumn<Booking, String> bookingGuideColumn;
    @FXML private TableColumn<Booking, String> bookingAttractionColumn;
    @FXML private TableColumn<Booking, LocalDate> bookingDateColumn;
    @FXML private TableColumn<Booking, String> bookingStatusColumn;
    @FXML private TableColumn<Booking, Double> bookingPriceColumn;
    @FXML private ComboBox<Guide> assignGuideCombo;
    @FXML private ComboBox<String> bookingStatusCombo;
    @FXML private Button assignGuideButton;
    @FXML private Button updateBookingStatusButton;
    @FXML private Button deleteBookingButton;
    
    // Analytics Tab
    @FXML private PieChart nationalityChart;
    @FXML private BarChart<String, Number> popularAttractionsChart;
    @FXML private Label totalRevenueLabel;
    @FXML private Label totalBookingsLabel;
    @FXML private Label totalTouristsLabel;
    @FXML private Label totalGuidesLabel;
    
    // Common
    @FXML private Button languageToggleButton;
    @FXML private Button logoutButton;
    @FXML private Button refreshDataButton;
    
    private Admin currentUser;
    private ObservableList<Guide> guides;
    private ObservableList<Attraction> attractions;
    private ObservableList<Booking> bookings;
    
    public void setCurrentUser(Admin user) {
        this.currentUser = user;
        initializeDashboard();
    }
    
    @FXML
    private void initialize() {
        setupTableColumns();
        setupComboBoxes();
        updateLanguage();
    }
    
    private void initializeDashboard() {
        // Display user info using polymorphism
        welcomeLabel.setText(LanguageManager.getText("Welcome") + ", " + currentUser.getFullName() + "!");
        dashboardInfoLabel.setText(currentUser.getDashboardInfo());
        
        loadAllData();
        updateAnalytics();
    }
    
    private void setupTableColumns() {
        // Guide table columns
        guideUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        guideNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        guideLanguagesColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getLanguagesString()));
        guideExperienceColumn.setCellValueFactory(new PropertyValueFactory<>("experienceYears"));
        
        // Attraction table columns
        attractionNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        attractionLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        attractionAltitudeColumn.setCellValueFactory(new PropertyValueFactory<>("altitudeLevel"));
        attractionDifficultyColumn.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
        attractionPriceColumn.setCellValueFactory(new PropertyValueFactory<>("basePrice"));
        
        // Booking table columns
        bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        bookingTouristColumn.setCellValueFactory(new PropertyValueFactory<>("touristUsername"));
        bookingGuideColumn.setCellValueFactory(new PropertyValueFactory<>("guideUsername"));
        bookingAttractionColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAttraction().getName()));
        bookingDateColumn.setCellValueFactory(new PropertyValueFactory<>("trekDate"));
        bookingStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        bookingPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
    }
    
    private void setupComboBoxes() {
        attractionAltitudeCombo.setItems(FXCollections.observableArrayList("High", "Low"));
        attractionDifficultyCombo.setItems(FXCollections.observableArrayList("Easy", "Medium", "Hard"));
        bookingStatusCombo.setItems(FXCollections.observableArrayList("Pending", "Confirmed", "Cancelled", "Completed"));
    }
    
    private void loadAllData() {
        // Load guides
        List<Guide> guideList = FileHandler.loadGuides();
        guides = FXCollections.observableArrayList(guideList);
        guidesTable.setItems(guides);
        assignGuideCombo.setItems(guides);
        
        // Load attractions
        List<Attraction> attractionList = FileHandler.loadAttractions();
        attractions = FXCollections.observableArrayList(attractionList);
        attractionsTable.setItems(attractions);
        
        // Load bookings
        List<Booking> bookingList = FileHandler.loadBookings();
        bookings = FXCollections.observableArrayList(bookingList);
        bookingsTable.setItems(bookings);
    }
    
    // Guide Management Methods
    @FXML
    private void handleAddGuide() {
        if (!validateGuideFields()) return;
        
        try {
            List<String> languages = Arrays.asList(guideLanguagesField.getText().trim().split(",\\s*"));
            Guide newGuide = new Guide(
                guideUsernameField.getText().trim(),
                guidePasswordField.getText().trim(),
                guideNameField.getText().trim(),
                guideEmailField.getText().trim(),
                guidePhoneField.getText().trim(),
                languages,
                Integer.parseInt(guideExperienceField.getText().trim())
            );
            
            FileHandler.saveGuide(newGuide);
            guides.add(newGuide);
            clearGuideFields();
            showAlert("Success", "Guide added successfully!");
            
        } catch (Exception e) {
            showAlert("Error", "Failed to add guide!");
        }
    }
    
    @FXML
    private void handleUpdateGuide() {
        Guide selectedGuide = guidesTable.getSelectionModel().getSelectedItem();
        if (selectedGuide == null) {
            showAlert("Error", "Please select a guide to update!");
            return;
        }
        
        if (!validateGuideFields()) return;
        
        try {
            selectedGuide.setFullName(guideNameField.getText().trim());
            selectedGuide.setEmail(guideEmailField.getText().trim());
            selectedGuide.setPhone(guidePhoneField.getText().trim());
            selectedGuide.setExperienceYears(Integer.parseInt(guideExperienceField.getText().trim()));
            
            guidesTable.refresh();
            showAlert("Success", "Guide updated successfully!");
            
        } catch (Exception e) {
            showAlert("Error", "Failed to update guide!");
        }
    }
    
    @FXML
    private void handleDeleteGuide() {
        Guide selectedGuide = guidesTable.getSelectionModel().getSelectedItem();
        if (selectedGuide == null) {
            showAlert("Error", "Please select a guide to delete!");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setContentText("Are you sure you want to delete this guide?");
        
        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            guides.remove(selectedGuide);
            showAlert("Success", "Guide deleted successfully!");
        }
    }
    
    // Attraction Management Methods
    @FXML
    private void handleAddAttraction() {
        if (!validateAttractionFields()) return;
        
        try {
            Attraction newAttraction = new Attraction(
                attractionNameField.getText().trim(),
                attractionLocationField.getText().trim(),
                attractionAltitudeCombo.getValue(),
                attractionDifficultyCombo.getValue(),
                Double.parseDouble(attractionPriceField.getText().trim())
            );
            
            FileHandler.saveAttraction(newAttraction);
            attractions.add(newAttraction);
            clearAttractionFields();
            showAlert("Success", "Attraction added successfully!");
            
        } catch (Exception e) {
            showAlert("Error", "Failed to add attraction!");
        }
    }
    
    @FXML
    private void handleUpdateAttraction() {
        Attraction selectedAttraction = attractionsTable.getSelectionModel().getSelectedItem();
        if (selectedAttraction == null) {
            showAlert("Error", "Please select an attraction to update!");
            return;
        }
        
        if (!validateAttractionFields()) return;
        
        try {
            selectedAttraction.setName(attractionNameField.getText().trim());
            selectedAttraction.setLocation(attractionLocationField.getText().trim());
            selectedAttraction.setAltitudeLevel(attractionAltitudeCombo.getValue());
            selectedAttraction.setDifficulty(attractionDifficultyCombo.getValue());
            selectedAttraction.setBasePrice(Double.parseDouble(attractionPriceField.getText().trim()));
            
            attractionsTable.refresh();
            showAlert("Success", "Attraction updated successfully!");
            
        } catch (Exception e) {
            showAlert("Error", "Failed to update attraction!");
        }
    }
    
    @FXML
    private void handleDeleteAttraction() {
        Attraction selectedAttraction = attractionsTable.getSelectionModel().getSelectedItem();
        if (selectedAttraction == null) {
            showAlert("Error", "Please select an attraction to delete!");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setContentText("Are you sure you want to delete this attraction?");
        
        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            attractions.remove(selectedAttraction);
            showAlert("Success", "Attraction deleted successfully!");
        }
    }
    
    // Booking Management Methods
    @FXML
    private void handleAssignGuide() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        Guide selectedGuide = assignGuideCombo.getValue();
        
        if (selectedBooking == null || selectedGuide == null) {
            showAlert("Error", "Please select both booking and guide!");
            return;
        }
        
        selectedBooking.setGuideUsername(selectedGuide.getUsername());
        selectedGuide.assignBooking(selectedBooking);
        bookingsTable.refresh();
        showAlert("Success", "Guide assigned successfully!");
    }
    
    @FXML
    private void handleUpdateBookingStatus() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        String newStatus = bookingStatusCombo.getValue();
        
        if (selectedBooking == null || newStatus == null) {
            showAlert("Error", "Please select booking and status!");
            return;
        }
        
        selectedBooking.setStatus(newStatus);
        bookingsTable.refresh();
        showAlert("Success", "Booking status updated successfully!");
    }
    
    @FXML
    private void handleDeleteBooking() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert("Error", "Please select a booking to delete!");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setContentText("Are you sure you want to delete this booking?");
        
        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            bookings.remove(selectedBooking);
            updateAnalytics();
            showAlert("Success", "Booking deleted successfully!");
        }
    }
    
    // Analytics Methods
    private void updateAnalytics() {
        updateNationalityChart();
        updatePopularAttractionsChart();
        updateStatistics();
    }
    
    private void updateNationalityChart() {
        List<Tourist> tourists = FileHandler.loadTourists();
        Map<String, Long> nationalityCount = tourists.stream()
            .collect(Collectors.groupingBy(Tourist::getNationality, Collectors.counting()));
        
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        nationalityCount.forEach((nationality, count) -> 
            pieChartData.add(new PieChart.Data(nationality, count)));
        
        nationalityChart.setData(pieChartData);
        nationalityChart.setTitle("Tourist Nationality Distribution");
    }
    
    private void updatePopularAttractionsChart() {
        Map<String, Long> attractionCount = bookings.stream()
            .collect(Collectors.groupingBy(
                booking -> booking.getAttraction().getName(), 
                Collectors.counting()));
        
        CategoryAxis xAxis = (CategoryAxis) popularAttractionsChart.getXAxis();
        NumberAxis yAxis = (NumberAxis) popularAttractionsChart.getYAxis();
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Bookings");
        
        attractionCount.forEach((attraction, count) -> 
            series.getData().add(new XYChart.Data<>(attraction, count)));
        
        popularAttractionsChart.getData().clear();
        popularAttractionsChart.getData().add(series);
        popularAttractionsChart.setTitle("Most Popular Attractions");
    }
    
    private void updateStatistics() {
        double totalRevenue = bookings.stream()
            .filter(booking -> "Confirmed".equals(booking.getStatus()) || "Completed".equals(booking.getStatus()))
            .mapToDouble(Booking::getTotalPrice)
            .sum();
        
        totalRevenueLabel.setText("Total Revenue: $" + String.format("%.2f", totalRevenue));
        totalBookingsLabel.setText("Total Bookings: " + bookings.size());
        totalTouristsLabel.setText("Total Tourists: " + FileHandler.loadTourists().size());
        totalGuidesLabel.setText("Total Guides: " + guides.size());
    }
    
    // Validation Methods
    private boolean validateGuideFields() {
        if (guideUsernameField.getText().trim().isEmpty() ||
            guidePasswordField.getText().trim().isEmpty() ||
            guideNameField.getText().trim().isEmpty() ||
            guideEmailField.getText().trim().isEmpty() ||
            guidePhoneField.getText().trim().isEmpty() ||
            guideLanguagesField.getText().trim().isEmpty() ||
            guideExperienceField.getText().trim().isEmpty()) {
            
            showAlert("Error", "Please fill in all guide fields!");
            return false;
        }
        
        try {
            Integer.parseInt(guideExperienceField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid experience years!");
            return false;
        }
        
        return true;
    }
    
    private boolean validateAttractionFields() {
        if (attractionNameField.getText().trim().isEmpty() ||
            attractionLocationField.getText().trim().isEmpty() ||
            attractionAltitudeCombo.getValue() == null ||
            attractionDifficultyCombo.getValue() == null ||
            attractionPriceField.getText().trim().isEmpty()) {
            
            showAlert("Error", "Please fill in all attraction fields!");
            return false;
        }
        
        try {
            Double.parseDouble(attractionPriceField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid price!");
            return false;
        }
        
        return true;
    }
    
    // Utility Methods
    private void clearGuideFields() {
        guideUsernameField.clear();
        guidePasswordField.clear();
        guideNameField.clear();
        guideEmailField.clear();
        guidePhoneField.clear();
        guideLanguagesField.clear();
        guideExperienceField.clear();
    }
    
    private void clearAttractionFields() {
        attractionNameField.clear();
        attractionLocationField.clear();
        attractionAltitudeCombo.setValue(null);
        attractionDifficultyCombo.setValue(null);
        attractionPriceField.clear();
    }
    
    @FXML
    private void handleRefreshData() {
        loadAllData();
        updateAnalytics();
        showAlert("Success", "Data refreshed successfully!");
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
        addGuideButton.setText(LanguageManager.getText("Add"));
        updateGuideButton.setText(LanguageManager.getText("Update"));
        deleteGuideButton.setText(LanguageManager.getText("Delete"));
        addAttractionButton.setText(LanguageManager.getText("Add"));
        updateAttractionButton.setText(LanguageManager.getText("Update"));
        deleteAttractionButton.setText(LanguageManager.getText("Delete"));
        assignGuideButton.setText(LanguageManager.getText("Assign Guide"));
        updateBookingStatusButton.setText(LanguageManager.getText("Update Status"));
        deleteBookingButton.setText(LanguageManager.getText("Delete"));
        refreshDataButton.setText(LanguageManager.getText("Refresh"));
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
