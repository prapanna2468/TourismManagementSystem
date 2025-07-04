package com.tourism;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import com.tourism.utils.FileHandler;

public class Main extends Application {
    
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
        
        // Set maximum window size (optional)
        primaryStage.setMaxWidth(1920);
        primaryStage.setMaxHeight(1080);
        
        // Enable full screen mode with F11 key
        primaryStage.setFullScreenExitHint("Press F11 to exit full screen mode");
        primaryStage.setFullScreenExitKeyCombination(new KeyCodeCombination(KeyCode.F11));
        
        // Add keyboard shortcut for full screen toggle
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F11) {
                primaryStage.setFullScreen(!primaryStage.isFullScreen());
            }
            // Alt + Enter also toggles full screen
            else if (event.isAltDown() && event.getCode() == KeyCode.ENTER) {
                primaryStage.setFullScreen(!primaryStage.isFullScreen());
            }
            // Escape exits full screen
            else if (event.getCode() == KeyCode.ESCAPE && primaryStage.isFullScreen()) {
                primaryStage.setFullScreen(false);
            }
        });
        
        primaryStage.show();
        
        // Optional: Start in maximized mode (not full screen)
        primaryStage.setMaximized(true);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

