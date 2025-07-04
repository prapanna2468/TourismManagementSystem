package com.tourism;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.tourism.utils.FileHandler;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Initialize data files
        FileHandler.initializeDataFiles();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);
        
        primaryStage.setTitle("Nepal Tourism Management System");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
