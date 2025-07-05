package com.tourism.controllers;

import com.tourism.Main;
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
import java.time.Month;
import java.util.List;

public class TouristDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Label dashboardInfoLabel;
    @FXML private ComboBox<Attraction> attractionComboBox;
    @FXML private DatePicker trekDatePicker;
    @FXML private Label priceLabel;
    @FXML private Button bookButton;
    @FXML private Button languageToggleButton;
    @FXML private Button logoutButton;
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, Integer> bookingIdColumn;
    @FXML private TableColumn<Booking, String> attractionColumn;
    @FXML private TableColumn<Booking, LocalDate> dateColumn;
    @FXML private TableColumn<Booking, String> statusColumn;
    @FXML private TableColumn<Booking, Double> priceColumn;
    @FXML private Button updateBookingButton;
    @FXML private Button cancelBookingButton;
    
    private Tourist currentUser;
    private ObservableList<Attraction> attractions;
    private ObservableList<Booking> userBookings;
    
    public void setCurrentUser(Tourist user) {
        this.currentUser = user;
        initializeDashboard();
    }
    
    @FXML
    private void initialize() {
        setupTableColumns();
        setupEventHandlers();
        updateLanguage();
    }
    
    private void initializeDashboard() {
        // Display user info using polymorphism
        welcomeLabel.setText(LanguageManager.getText("Welcome") + ", " + currentUser.getFullName() + "!");
        dashboardInfoLabel.setText(currentUser.getDashboardInfo());
        
        loadAttractions();
        loadUserBookings();
        updatePriceCalculation();
    }
    
    private void setupTableColumns() {
        bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        attractionColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAttraction().getName()));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("trekDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
    }
    
    private void setupEventHandlers() {
        attractionComboBox.setOnAction(e -> updatePriceCalculation());
        trekDatePicker.setOnAction(e -> updatePriceCalculation());
        
        // Custom cell factory for attraction ComboBox
        attractionComboBox.setCellFactory(listView -> new ListCell<Attraction>() {
            @Override
            protected void updateItem(Attraction attraction, boolean empty) {
                super.updateItem(attraction, empty);
                if (empty || attraction == null) {
                    setText(null);
                } else {
                    setText(attraction.getName() + " - " + attraction.getAltitudeLevel() + " Altitude");
                }
            }
        });
        
        attractionComboBox.setButtonCell(new ListCell<Attraction>() {
            @Override
            protected void updateItem(Attraction attraction, boolean empty) {
                super.updateItem(attraction, empty);
                if (empty || attraction == null) {
                    setText(null);
                } else {
                    setText(attraction.getName());
                }
            }
        });
    }
    
    private void loadAttractions() {
        List<Attraction> attractionList = FileHandler.loadAttractions();
        attractions = FXCollections.observableArrayList(attractionList);
        attractionComboBox.setItems(attractions);
    }
    
    private void loadUserBookings() {
        List<Booking> allBookings = FileHandler.loadBookings();
        userBookings = FXCollections.observableArrayList();
        
        for (Booking booking : allBookings) {
            if (booking.getTouristUsername().equals(currentUser.getUsername())) {
                userBookings.add(booking);
                currentUser.addBooking(booking); // Update user's booking list
            }
        }
        
        bookingsTable.setItems(userBookings);
    }
    
    private void updatePriceCalculation() {
        Attraction selectedAttraction = attractionComboBox.getValue();
        LocalDate selectedDate = trekDatePicker.getValue();
        
        if (selectedAttraction != null && selectedDate != null) {
            boolean isFestivalSeason = isFestivalSeason(selectedDate);
            double price = selectedAttraction.calculatePrice(isFestivalSeason);
            
            String priceText = "$" + String.format("%.2f", price);
            if (isFestivalSeason) {
                priceText += " (20% Festival Discount Applied!)";
            }
            priceLabel.setText(priceText);
        } else {
            priceLabel.setText("Select attraction and date");
        }
    }
    
    private boolean isFestivalSeason(LocalDate date) {
        Month month = date.getMonth();
        return month == Month.AUGUST || month == Month.SEPTEMBER || month == Month.OCTOBER;
    }
    
    @FXML
    private void handleBooking() {
        Attraction selectedAttraction = attractionComboBox.getValue();
        LocalDate selectedDate = trekDatePicker.getValue();
        
        if (selectedAttraction == null || selectedDate == null) {
            showAlert("Error", "Please select attraction and date!");
            return;
        }
        
        if (selectedDate.isBefore(LocalDate.now())) {
            showAlert("Error", "Cannot book for past dates!");
            return;
        }
        
        // Check if attraction is available
        if (!selectedAttraction.isAvailable()) {
            showAlert("Error", "This attraction is fully booked!");
            return;
        }
        
        // Show high altitude warning
        if (selectedAttraction.isHighAltitude()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(LanguageManager.getText("High Altitude Warning"));
            alert.setHeaderText("High Altitude Trek Selected!");
            alert.setContentText("This trek involves high altitude. Please ensure you are physically fit and consult a doctor if you have any health concerns. Proper acclimatization is essential.");
            
            ButtonType continueButton = new ButtonType("Continue Booking");
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(continueButton, cancelButton);
            
            if (alert.showAndWait().orElse(cancelButton) == cancelButton) {
                return;
            }
        }
        
        // Create booking
        Booking newBooking = new Booking(currentUser.getUsername(), selectedAttraction, selectedDate);
        newBooking.confirmBooking();
        
        // Show festival discount popup if applicable
        if (newBooking.isFestivalDiscountApplied()) {
            Alert festivalAlert = new Alert(Alert.AlertType.INFORMATION);
            festivalAlert.setTitle(LanguageManager.getText("Festival Discount Applied"));
            festivalAlert.setHeaderText("🎉 Dashain & Tihar Festival Discount!");
            festivalAlert.setContentText("Congratulations! You've received a 20% discount for booking during the festival season (August-October). Enjoy your trek!");
            festivalAlert.showAndWait();
        }
        
        // Save booking
        FileHandler.saveBooking(newBooking);
        currentUser.addBooking(newBooking);
        userBookings.add(newBooking);
        
        // Update dashboard info
        dashboardInfoLabel.setText(currentUser.getDashboardInfo());
        
        showAlert("Success", "Booking confirmed successfully!\nBooking ID: " + newBooking.getBookingId());
        
        // Clear selection
        attractionComboBox.setValue(null);
        trekDatePicker.setValue(null);
        priceLabel.setText("Select attraction and date");
    }
    
    @FXML
    private void handleUpdateBooking() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert("Error", "Please select a booking to update!");
            return;
        }
        
        if (!selectedBooking.canBeModified()) {
            showAlert("Error", "This booking cannot be modified!");
            return;
        }
        
        // For simplicity, we'll just show the booking details
        // In a full implementation, you'd open an edit dialog
        showAlert("Booking Details", selectedBooking.toString());
    }
    
    @FXML
    private void handleCancelBooking() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showAlert("Error", "Please select a booking to cancel!");
            return;
        }
        
        if (!selectedBooking.canBeCancelled()) {
            showAlert("Error", "This booking cannot be cancelled!");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Cancellation");
        confirmAlert.setHeaderText("Cancel Booking");
        confirmAlert.setContentText("Are you sure you want to cancel this booking?");
        
        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            selectedBooking.cancelBooking();
            bookingsTable.refresh();
            dashboardInfoLabel.setText(currentUser.getDashboardInfo());
            showAlert("Success", "Booking cancelled successfully!");
        }
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
            Scene scene = new Scene(loader.load());
            
            // Use the new scene switching method to maintain full screen
            Main.switchScene(scene, "Journey - Nepal Tourism System");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void updateLanguage() {
        bookButton.setText(LanguageManager.getText("Book Now"));
        updateBookingButton.setText(LanguageManager.getText("Update"));
        cancelBookingButton.setText(LanguageManager.getText("Cancel"));
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
