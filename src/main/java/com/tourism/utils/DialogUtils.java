package com.tourism.utils;

import com.tourism.Main;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

public class DialogUtils {
    
    /**
     * Creates a properly configured alert dialog that displays correctly over full-screen applications
     */
    public static Alert createAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        configureDialog(alert, title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert;
    }
    
    /**
     * Creates a properly configured custom dialog that displays correctly over full-screen applications
     */
    public static <T> Dialog<T> createDialog(String title) {
        Dialog<T> dialog = new Dialog<>();
        configureDialog(dialog, title);
        return dialog;
    }
    
    /**
     * Configures a dialog to display properly over full-screen applications
     */
    private static void configureDialog(Dialog<?> dialog, String title) {
        dialog.setTitle(title);
        
        // Set the owner to the primary stage
        if (Main.primaryStage != null) {
            dialog.initOwner(Main.primaryStage);
        }
        
        // Set modality to ensure dialog appears on top
        dialog.initModality(Modality.APPLICATION_MODAL);
        
        // Set style to ensure proper display
        dialog.initStyle(StageStyle.DECORATED);
        
        // Ensure dialog appears on top and is always on top
        dialog.getDialogPane().getScene().getWindow().setOnShowing(e -> {
            dialog.getDialogPane().getScene().getWindow().requestFocus();
        });
        
        // Set dialog to be always on top
        if (dialog.getDialogPane().getScene() != null && dialog.getDialogPane().getScene().getWindow() != null) {
            ((javafx.stage.Stage) dialog.getDialogPane().getScene().getWindow()).setAlwaysOnTop(true);
        }
        
        // Ensure dialog is properly positioned
        dialog.setOnShowing(e -> {
            if (Main.primaryStage != null) {
                dialog.setX(Main.primaryStage.getX() + (Main.primaryStage.getWidth() - dialog.getWidth()) / 2);
                dialog.setY(Main.primaryStage.getY() + (Main.primaryStage.getHeight() - dialog.getHeight()) / 2);
            }
        });
    }
    
    /**
     * Shows an alert dialog with proper configuration for full-screen applications
     */
    public static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = createAlert(alertType, title, message);
        alert.showAndWait();
    }
    
    /**
     * Shows an information alert
     */
    public static void showInfo(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message);
    }
    
    /**
     * Shows an error alert
     */
    public static void showError(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, message);
    }
    
    /**
     * Shows a warning alert
     */
    public static void showWarning(String title, String message) {
        showAlert(Alert.AlertType.WARNING, title, message);
    }
    
    /**
     * Shows a confirmation alert and returns the result
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = createAlert(Alert.AlertType.CONFIRMATION, title, message);
        return alert.showAndWait().orElse(javafx.scene.control.ButtonType.CANCEL) == javafx.scene.control.ButtonType.OK;
    }
}
