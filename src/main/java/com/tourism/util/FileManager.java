package com.tourism.util;

import com.tourism.model.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static final String DATA_DIR = "data/";
    private static final String TOURISTS_FILE = DATA_DIR + "tourists.txt";
    private static final String GUIDES_FILE = DATA_DIR + "guides.txt";
    private static final String ATTRACTIONS_FILE = DATA_DIR + "attractions.txt";
    private static final String BOOKINGS_FILE = DATA_DIR + "bookings.txt";

    public static void initializeDataFiles() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        if (!new File(ATTRACTIONS_FILE).exists()) {
            initializeDefaultAttractions();
        }
    }

    private static void initializeDefaultAttractions() {
        List<Attraction> defaultAttractions = new ArrayList<>();
        defaultAttractions.add(new Attraction("Everest Base Camp", "High", "Hard", 1000.0));
        defaultAttractions.add(new Attraction("Annapurna Circuit", "High", "Medium", 800.0));
        defaultAttractions.add(new Attraction("Chitwan National Park", "Low", "Easy", 300.0));
        defaultAttractions.add(new Attraction("Pokhara Lake", "Low", "Easy", 200.0));
        defaultAttractions.add(new Attraction("Langtang Valley", "High", "Medium", 600.0));
        
        saveAttractions(defaultAttractions);
    }

    public static void saveUser(User user) {
        String filename = user.getRole().equals("Tourist") ? TOURISTS_FILE : GUIDES_FILE;
        try (FileWriter writer = new FileWriter(filename, true)) {
            writer.write(user.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<User> loadUsers(String role) {
        String filename = role.equals("Tourist") ? TOURISTS_FILE : GUIDES_FILE;
        List<User> users = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            User user = null;
            
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Username: ")) {
                    user = new User();
                    user.setUsername(line.substring(10));
                } else if (line.startsWith("Password: ") && user != null) {
                    user.setPassword(line.substring(10));
                } else if (line.startsWith("Full Name: ") && user != null) {
                    user.setFullName(line.substring(11));
                } else if (line.startsWith("Email: ") && user != null) {
                    user.setEmail(line.substring(7));
                } else if (line.startsWith("Phone: ") && user != null) {
                    user.setPhone(line.substring(7));
                } else if (line.startsWith("Role: ") && user != null) {
                    user.setRole(line.substring(6));
                } else if (line.equals("------------------------") && user != null) {
                    users.add(user);
                    user = null;
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet
        }
        
        return users;
    }

    public static void saveAttractions(List<Attraction> attractions) {
        try (FileWriter writer = new FileWriter(ATTRACTIONS_FILE)) {
            for (Attraction attraction : attractions) {
                writer.write(attraction.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Attraction> loadAttractions() {
        List<Attraction> attractions = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(ATTRACTIONS_FILE))) {
            String line;
            Attraction attraction = null;
            
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Name: ")) {
                    attraction = new Attraction();
                    attraction.setName(line.substring(6));
                } else if (line.startsWith("Altitude: ") && attraction != null) {
                    attraction.setAltitude(line.substring(10));
                } else if (line.startsWith("Difficulty: ") && attraction != null) {
                    attraction.setDifficulty(line.substring(12));
                } else if (line.startsWith("Base Price: $") && attraction != null) {
                    attraction.setBasePrice(Double.parseDouble(line.substring(13)));
                } else if (line.equals("------------------------") && attraction != null) {
                    attractions.add(attraction);
                    attraction = null;
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet
        }
        
        return attractions;
    }
}
