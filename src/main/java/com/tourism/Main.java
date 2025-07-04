package com.tourism;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.stage.Stage;
import com.tourism.utils.FileHandler;

public class Main extends Application {
    
    private boolean wasMaximized = false;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize data files
        FileHandler.initializeDataFiles();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Scene scene = new Scene(loader.load(), 900, 700);
        
        primaryStage.setTitle("Nepal Tourism Management System");
        primaryStage.setScene(scene);
        
        // Enable resizing
        primaryStage.setResizable(true);
        
        // Set minimum window size
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        
        // Set maximum window size
        primaryStage.setMaxWidth(1920);
        primaryStage.setMaxHeight(1080);
        
        // Configure full screen
        primaryStage.setFullScreenExitHint("Press F11 or ESC to exit full screen mode");
        primaryStage.setFullScreenExitKeyCombination(new KeyCodeCombination(KeyCode.F11));
        
        // Track maximized state before going fullscreen
        primaryStage.maximizedProperty().addListener((obs, wasMaximized, isMaximized) -> {
            if (!primaryStage.isFullScreen()) {
                this.wasMaximized = isMaximized;
            }
        });
        
        // Handle fullscreen state changes
        primaryStage.fullScreenProperty().addListener((obs, wasFullScreen, isFullScreen) -> {
            if (!isFullScreen) {
                // When exiting fullscreen, restore previous state
                if (this.wasMaximized) {
                    primaryStage.setMaximized(true);
                } else {
                    primaryStage.setMaximized(false);
                    // Set a reasonable window size
                    primaryStage.setWidth(1200);
                    primaryStage.setHeight(800);
                    primaryStage.centerOnScreen();
                }
            }
        });
        
        // Add keyboard shortcuts
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F11) {
                toggleFullScreen(primaryStage);
            }
            // Alt + Enter also toggles full screen
            else if (event.isAltDown() && event.getCode() == KeyCode.ENTER) {
                toggleFullScreen(primaryStage);
            }
            // Escape exits full screen
            else if (event.getCode() == KeyCode.ESCAPE && primaryStage.isFullScreen()) {
                primaryStage.setFullScreen(false);
            }
        });
        
        primaryStage.show();
        
        // Start maximized (not full screen)
        primaryStage.setMaximized(true);
        this.wasMaximized = true;
    }
    
    private void toggleFullScreen(Stage stage) {
        if (!stage.isFullScreen()) {
            // Remember current maximized state before going fullscreen
            this.wasMaximized = stage.isMaximized();
            stage.setFullScreen(true);
        } else {
            stage.setFullScreen(false);
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}


