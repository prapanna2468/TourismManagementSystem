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
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
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
            DialogUtils.showError("Error", "Please select attraction and date!");
            return;
        }
        
        if (selectedDate.isBefore(LocalDate.now())) {
            DialogUtils.showError("Error", "Cannot book for past dates!");
            return;
        }
        
        // Check if attraction is available
        if (!selectedAttraction.isAvailable()) {
            DialogUtils.showError("Error", "This attraction is fully booked!");
            return;
        }
        
        // Show high altitude warning
        if (selectedAttraction.isHighAltitude()) {
            Alert alert = DialogUtils.createAlert(Alert.AlertType.WARNING, 
                LanguageManager.getText("High Altitude Warning"), 
                "This trek involves high altitude. Please ensure you are physically fit and consult a doctor if you have any health concerns. Proper acclimatization is essential.");
            
            alert.setHeaderText("High Altitude Trek Selected!");
            
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
            Alert festivalAlert = DialogUtils.createAlert(Alert.AlertType.INFORMATION,
                LanguageManager.getText("Festival Discount Applied"),
                "Congratulations! You've received a 20% discount for booking during the festival season (August-October). Enjoy your trek!");
            
            festivalAlert.setHeaderText("🎉 Dashain & Tihar Festival Discount!");
            festivalAlert.showAndWait();
        }
        
        // Save booking
        FileHandler.saveBooking(newBooking);
        currentUser.addBooking(newBooking);
        userBookings.add(newBooking);
        
        // Update dashboard info
        dashboardInfoLabel.setText(currentUser.getDashboardInfo());
        
        DialogUtils.showInfo("Success", "Booking confirmed successfully!\nBooking ID: " + newBooking.getBookingId());
        
        // Clear selection
        attractionComboBox.setValue(null);
        trekDatePicker.setValue(null);
        priceLabel.setText("Select attraction and date");
    }
    
    @FXML
    private void handleUpdateBooking() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            DialogUtils.showError("Error", "Please select a booking to update!");
            return;
        }
        
        if (!selectedBooking.canBeModified()) {
            DialogUtils.showError("Error", "This booking cannot be modified! Bookings can only be modified at least 3 days before the trek date.");
            return;
        }
        
        // Show booking update dialog
        showBookingUpdateDialog(selectedBooking);
    }

    private void showBookingUpdateDialog(Booking booking) {
        Dialog<ButtonType> dialog = DialogUtils.createDialog("Update Booking - ID: " + booking.getBookingId());
        dialog.setHeaderText("Modify your booking details");

        // Create the dialog content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Current booking info
        Label currentInfoLabel = new Label("Current Booking Information:");
        currentInfoLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label currentAttractionLabel = new Label("Current Attraction: " + booking.getAttraction().getName());
        Label currentDateLabel = new Label("Current Date: " + booking.getTrekDate());
        Label currentPriceLabel = new Label("Current Price: $" + String.format("%.2f", booking.getTotalPrice()));

        // Update fields
        Label updateLabel = new Label("Update Options:");
        updateLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2E8B57;");

        Label attractionLabel = new Label("New Attraction:");
        ComboBox<Attraction> newAttractionCombo = new ComboBox<>();
        newAttractionCombo.setItems(attractions);
        newAttractionCombo.setValue(booking.getAttraction());

        // Custom cell factory for attraction display
        newAttractionCombo.setCellFactory(listView -> new ListCell<Attraction>() {
            @Override
            protected void updateItem(Attraction attraction, boolean empty) {
                super.updateItem(attraction, empty);
                if (empty || attraction == null) {
                    setText(null);
                } else {
                    setText(attraction.getName() + " - " + attraction.getAltitudeLevel() + " Altitude ($" +
                            String.format("%.2f", attraction.getBasePrice()) + ")");
                }
            }
        });

        newAttractionCombo.setButtonCell(new ListCell<Attraction>() {
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

        Label dateLabel = new Label("New Trek Date:");
        DatePicker newDatePicker = new DatePicker();
        newDatePicker.setValue(booking.getTrekDate());

        // Price preview
        Label newPriceLabel = new Label("New Price: Calculating...");
        newPriceLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2E8B57;");

        // Notes field
        Label notesLabel = new Label("Special Requests/Notes:");
        TextArea notesArea = new TextArea();
        notesArea.setText(booking.getNotes());
        notesArea.setPrefRowCount(3);
        notesArea.setWrapText(true);

        // Update price calculation when attraction or date changes
        Runnable updatePrice = () -> {
            Attraction selectedAttraction = newAttractionCombo.getValue();
            LocalDate selectedDate = newDatePicker.getValue();

            if (selectedAttraction != null && selectedDate != null) {
                boolean isFestivalSeason = isFestivalSeason(selectedDate);
                double price = selectedAttraction.calculatePrice(isFestivalSeason);

                String priceText = "New Price: $" + String.format("%.2f", price);
                if (isFestivalSeason) {
                    priceText += " (20% Festival Discount!)";
                }
                newPriceLabel.setText(priceText);
            } else {
                newPriceLabel.setText("New Price: Select attraction and date");
            }
        };

        newAttractionCombo.setOnAction(e -> updatePrice.run());
        newDatePicker.setOnAction(e -> updatePrice.run());

        // Initial price calculation
        updatePrice.run();

        // Add components to grid
        int row = 0;
        grid.add(currentInfoLabel, 0, row++, 2, 1);
        grid.add(currentAttractionLabel, 0, row++, 2, 1);
        grid.add(currentDateLabel, 0, row++, 2, 1);
        grid.add(currentPriceLabel, 0, row++, 2, 1);

        // Add separator
        Separator separator = new Separator();
        grid.add(separator, 0, row++, 2, 1);

        grid.add(updateLabel, 0, row++, 2, 1);
        grid.add(attractionLabel, 0, row);
        grid.add(newAttractionCombo, 1, row++);
        grid.add(dateLabel, 0, row);
        grid.add(newDatePicker, 1, row++);
        grid.add(newPriceLabel, 0, row++, 2, 1);
        grid.add(notesLabel, 0, row++, 2, 1);
        grid.add(notesArea, 0, row++, 2, 1);

        dialog.getDialogPane().setContent(grid);

        // Add buttons
        ButtonType updateButtonType = new ButtonType("Update Booking", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, cancelButtonType);

        // Handle the result
        dialog.showAndWait().ifPresent(result -> {
            if (result == updateButtonType) {
                processBookingUpdate(booking, newAttractionCombo.getValue(),
                        newDatePicker.getValue(), notesArea.getText());
            }
        });
    }

    private void processBookingUpdate(Booking originalBooking, Attraction newAttraction,
                                     LocalDate newDate, String newNotes) {
        try {
            // Validation
            if (newAttraction == null || newDate == null) {
                DialogUtils.showError("Error", "Please select both attraction and date!");
                return;
            }

            if (newDate.isBefore(LocalDate.now())) {
                DialogUtils.showError("Error", "Cannot set trek date in the past!");
                return;
            }

            if (newDate.isBefore(LocalDate.now().plusDays(3))) {
                DialogUtils.showError("Error", "Trek date must be at least 3 days from today!");
                return;
            }

            // Check if anything actually changed
            boolean attractionChanged = !newAttraction.getName().equals(originalBooking.getAttraction().getName());
            boolean dateChanged = !newDate.equals(originalBooking.getTrekDate());
            boolean notesChanged = !newNotes.trim().equals(originalBooking.getNotes().trim());

            if (!attractionChanged && !dateChanged && !notesChanged) {
                DialogUtils.showInfo("Info", "No changes were made to the booking.");
                return;
            }

            // Show high altitude warning if new attraction is high altitude
            if (attractionChanged && newAttraction.isHighAltitude() &&
                    !originalBooking.getAttraction().isHighAltitude()) {

                Alert alert = DialogUtils.createAlert(Alert.AlertType.WARNING, "High Altitude Warning",
                    "Your new selection involves high altitude. Please ensure you are physically fit and consult a doctor if you have any health concerns.");
                
                alert.setHeaderText("High Altitude Trek Selected!");

                ButtonType continueButton = new ButtonType("Continue Update");
                ButtonType cancelButton = new ButtonType("Cancel Update", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(continueButton, cancelButton);

                if (alert.showAndWait().orElse(cancelButton) == cancelButton) {
                    return;
                }
            }

            // Calculate old and new prices for comparison
            double oldPrice = originalBooking.getTotalPrice();
            boolean newFestivalSeason = isFestivalSeason(newDate);
            double newPrice = newAttraction.calculatePrice(newFestivalSeason);
            double priceDifference = newPrice - oldPrice;

            // Update the booking
            originalBooking.setAttraction(newAttraction);
            originalBooking.setTrekDate(newDate);
            originalBooking.setNotes(newNotes.trim());

            // Update tourist's spending
            currentUser.updateBookingInList(originalBooking);

            // Save changes to file
            List<Booking> allBookings = FileHandler.loadBookings();
            for (int i = 0; i < allBookings.size(); i++) {
                if (allBookings.get(i).getBookingId() == originalBooking.getBookingId()) {
                    allBookings.set(i, originalBooking);
                    break;
                }
            }
            FileHandler.saveAllBookings(allBookings);

            // Update tourist data
            List<Tourist> allTourists = FileHandler.loadTourists();
            for (int i = 0; i < allTourists.size(); i++) {
                if (allTourists.get(i).getUsername().equals(currentUser.getUsername())) {
                    allTourists.set(i, currentUser);
                    break;
                }
            }
            FileHandler.saveAllTourists(allTourists);

            // Refresh UI
            bookingsTable.refresh();
            dashboardInfoLabel.setText(currentUser.getDashboardInfo());

            // Show success message with details
            StringBuilder message = new StringBuilder("Booking updated successfully!\n\n");
            message.append("Booking ID: ").append(originalBooking.getBookingId()).append("\n");

            if (attractionChanged) {
                message.append("✓ Attraction changed\n");
            }
            if (dateChanged) {
                message.append("✓ Date changed\n");
            }
            if (notesChanged) {
                message.append("✓ Notes updated\n");
            }

            message.append("\nPrice Change: ");
            if (priceDifference > 0) {
                message.append("+$").append(String.format("%.2f", priceDifference));
            } else if (priceDifference < 0) {
                message.append("-$").append(String.format("%.2f", Math.abs(priceDifference)));
            } else {
                message.append("No change");
            }

            message.append("\nNew Total: $").append(String.format("%.2f", newPrice));

            if (newFestivalSeason && !originalBooking.isFestivalDiscountApplied()) {
                message.append("\n🎉 Festival discount now applied!");
            }

            DialogUtils.showInfo("Success", message.toString());

        } catch (Exception e) {
            e.printStackTrace();
            DialogUtils.showError("Error", "Failed to update booking: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleCancelBooking() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            DialogUtils.showError("Error", "Please select a booking to cancel!");
            return;
        }
        
        if (!selectedBooking.canBeCancelled()) {
            // Check if it's already cancelled and can be deleted
            if (selectedBooking.canBeDeleted()) {
                if (DialogUtils.showConfirmation("Delete Cancelled Booking", 
                    "This booking is already cancelled. Do you want to delete it permanently?")) {
                    deleteBooking(selectedBooking);
                }
                return;
            }
            
            DialogUtils.showError("Error", "This booking cannot be cancelled! Bookings can only be cancelled at least 7 days before the trek date.");
            return;
        }
        
        if (DialogUtils.showConfirmation("Confirm Cancellation", 
            "Are you sure you want to cancel this booking?\n\nAfter cancellation, you can delete it permanently if needed.")) {
            
            // Update tourist's spending before cancelling
            currentUser.removeBooking(selectedBooking);
            
            selectedBooking.cancelBooking();
            
            // Save changes to file
            List<Booking> allBookings = FileHandler.loadBookings();
            for (int i = 0; i < allBookings.size(); i++) {
                if (allBookings.get(i).getBookingId() == selectedBooking.getBookingId()) {
                    allBookings.set(i, selectedBooking);
                    break;
                }
            }
            FileHandler.saveAllBookings(allBookings);
            
            // Update tourist data
            List<Tourist> allTourists = FileHandler.loadTourists();
            for (int i = 0; i < allTourists.size(); i++) {
                if (allTourists.get(i).getUsername().equals(currentUser.getUsername())) {
                    allTourists.set(i, currentUser);
                    break;
                }
            }
            FileHandler.saveAllTourists(allTourists);
            
            // Refresh UI
            bookingsTable.refresh();
            dashboardInfoLabel.setText(currentUser.getDashboardInfo());
            
            DialogUtils.showInfo("Success", "Booking cancelled successfully!\n\nYour total spending has been updated.\nYou can now delete this cancelled booking if you wish.");
        }
    }
    
    private void deleteBooking(Booking booking) {
        try {
            // Remove from user's booking list
            userBookings.remove(booking);
            currentUser.removeBooking(booking);
            
            // Remove from all bookings file
            List<Booking> allBookings = FileHandler.loadBookings();
            allBookings.removeIf(b -> b.getBookingId() == booking.getBookingId());
            FileHandler.saveAllBookings(allBookings);
            
            // Update tourist data
            List<Tourist> allTourists = FileHandler.loadTourists();
            for (int i = 0; i < allTourists.size(); i++) {
                if (allTourists.get(i).getUsername().equals(currentUser.getUsername())) {
                    allTourists.set(i, currentUser);
                    break;
                }
            }
            FileHandler.saveAllTourists(allTourists);
            
            // Update dashboard info
            dashboardInfoLabel.setText(currentUser.getDashboardInfo());
            
            DialogUtils.showInfo("Success", "Cancelled booking deleted permanently!");
            
        } catch (Exception e) {
            e.printStackTrace();
            DialogUtils.showError("Error", "Failed to delete booking: " + e.getMessage());
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
}
