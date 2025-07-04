package com.tourism.controller;

import com.tourism.model.Attraction;
import com.tourism.model.Booking;
import com.tourism.util.FileManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class TouristDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private ComboBox<String> attractionComboBox;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> difficultyComboBox;
    @FXML private Label priceLabel;
    @FXML private Button bookButton;
    @FXML private TableView<Booking> bookingTable;
    @FXML private TableColumn<Booking, String> bookingIdColumn;
    @FXML private TableColumn<Booking, String> attractionColumn;
    @FXML private TableColumn<Booking, LocalDate> dateColumn;
    @FXML private TableColumn<Booking, String> difficultyColumn;
    @FXML private TableColumn<Booking, Double> priceColumn;
    @FXML private TableColumn<Booking, String> statusColumn;
    @FXML private Button logoutButton;

    private String currentUser;
    private ObservableList<Booking> bookingList = FXCollections.observableArrayList();
    private List<Attraction> attractions;

    @FXML
    private void initialize() {
        setupTable();
        loadAttractions();
        setupDifficultyComboBox();
        setupEventHandlers();
    }

    public void setCurrentUser(String username) {
        this.currentUser = username;
        welcomeLabel.setText("Welcome, " + username + "!");
        loadUserBookings();
    }

    private void setupTable() {
        bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        attractionColumn.setCellValueFactory(new PropertyValueFactory<>("attraction"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        difficultyColumn.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        bookingTable.setItems(bookingList);
    }

    private void loadAttractions() {
        attractions = FileManager.loadAttractions();
        ObservableList<String> attractionNames = FXCollections.observableArrayList();
        for (Attraction attraction : attractions) {
            attractionNames.add(attraction.getName());
        }
        attractionComboBox.setItems(attractionNames);
    }

    private void setupDifficultyComboBox() {
        difficultyComboBox.getItems().addAll("Easy", "Medium", "Hard");
    }

    private void setupEventHandlers() {
        attractionComboBox.setOnAction(e -> calculatePrice());
        difficultyComboBox.setOnAction(e -> calculatePrice());
        datePicker.setOnAction(e -> calculatePrice());
        
        attractionComboBox.setOnAction(e -> checkAltitudeWarning());
    }

    private void checkAltitudeWarning() {
        String selectedAttraction = attractionComboBox.getValue();
        if (selectedAttraction != null) {
            for (Attraction attraction : attractions) {
                if (attraction.getName().equals(selectedAttraction) && 
                    attraction.getAltitude().equals("High")) {
                    showAlert("High Altitude Warning", 
                        "Warning: This trek involves high altitude. Please ensure you are physically prepared and consult a doctor if necessary.");
                    break;
                }
            }
        }
    }

    private void calculatePrice() {
        String selectedAttraction = attractionComboBox.getValue();
        String selectedDifficulty = difficultyComboBox.getValue();
        LocalDate selectedDate = datePicker.getValue();

        if (selectedAttraction != null && selectedDifficulty != null) {
            double basePrice = 0;
            for (Attraction attraction : attractions) {
                if (attraction.getName().equals(selectedAttraction)) {
                    basePrice = attraction.getBasePrice();
                    break;
                }
            }

            // Adjust price based on difficulty
            double multiplier = 1.0;
            switch (selectedDifficulty) {
                case "Easy": multiplier = 1.0; break;
                case "Medium": multiplier = 1.2; break;
                case "Hard": multiplier = 1.5; break;
            }

            double finalPrice = basePrice * multiplier;

            // Apply festival discount (August-October)
            if (selectedDate != null) {
                int month = selectedDate.getMonthValue();
                if (month >= 8 && month <= 10) {
                    finalPrice *= 0.8; // 20% discount
                    priceLabel.setText(String.format("Price: $%.2f (20%% Festival Discount Applied!)", finalPrice));
                } else {
                    priceLabel.setText(String.format("Price: $%.2f", finalPrice));
                }
            } else {
                priceLabel.setText(String.format("Price: $%.2f", finalPrice));
            }
        }
    }

    @FXML
    private void handleBooking() {
        if (!validateBookingFields()) {
            return;
        }

        String bookingId = "BK" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String attraction = attractionComboBox.getValue();
        LocalDate date = datePicker.getValue();
        String difficulty = difficultyComboBox.getValue();
        
        // Calculate final price
        double basePrice = 0;
        for (Attraction attr : attractions) {
            if (attr.getName().equals(attraction)) {
                basePrice = attr.getBasePrice();
                break;
            }
        }

        double multiplier = 1.0;
        switch (difficulty) {
            case "Easy": multiplier = 1.0; break;
            case "Medium": multiplier = 1.2; break;
            case "Hard": multiplier = 1.5; break;
        }

        double finalPrice = basePrice * multiplier;
        int month = date.getMonthValue();
        if (month >= 8 && month <= 10) {
            finalPrice *= 0.8; // 20% discount
        }

        Booking booking = new Booking(bookingId, currentUser, attraction, date, difficulty, finalPrice, "Confirmed");
        FileManager.saveBooking(booking);
        
        showAlert("Success", "Booking created successfully! Booking ID: " + bookingId);
        loadUserBookings();
        clearBookingFields();
    }

    @FXML
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUserBookings() {
        List<Booking> allBookings = FileManager.loadBookings();
        bookingList.clear();
        for (Booking booking : allBookings) {
            if (booking.getTourist().equals(currentUser)) {
                bookingList.add(booking);
            }
        }
    }

    private boolean validateBookingFields() {
        if (attractionComboBox.getValue() == null) {
            showAlert("Validation Error", "Please select an attraction.");
            return false;
        }
        if (datePicker.getValue() == null) {
            showAlert("Validation Error", "Please select a date.");
            return false;
        }
        if (datePicker.getValue().isBefore(LocalDate.now())) {
            showAlert("Validation Error", "Cannot book for past dates.");
            return false;
        }
        if (difficultyComboBox.getValue() == null) {
            showAlert("Validation Error", "Please select difficulty level.");
            return false;
        }
        return true;
    }

    private void clearBookingFields() {
        attractionComboBox.setValue(null);
        datePicker.setValue(null);
        difficultyComboBox.setValue(null);
        priceLabel.setText("Price: $0.00");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
