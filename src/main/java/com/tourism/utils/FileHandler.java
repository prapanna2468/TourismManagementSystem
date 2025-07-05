package com.tourism.utils;

import com.tourism.models.*;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class FileHandler {
    private static final String DATA_DIR = "data/";
    private static final String TOURISTS_FILE = DATA_DIR + "tourists.txt";
    private static final String GUIDES_FILE = DATA_DIR + "guides.txt";
    private static final String ATTRACTIONS_FILE = DATA_DIR + "attractions.txt";
    private static final String BOOKINGS_FILE = DATA_DIR + "bookings.txt";
    private static final String SEPARATOR = "------------------------";
    
    public static void initializeDataFiles() {
        createDataDirectory();
        initializeDefaultAttractions();
        initializeDefaultGuides();
    }
    
    private static void createDataDirectory() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }
    
    // Tourist operations
    public static void saveTourist(Tourist tourist) {
        try (FileWriter writer = new FileWriter(TOURISTS_FILE, true)) {
            writer.write("Username: " + tourist.getUsername() + "\n");
            writer.write("Password: " + tourist.getPassword() + "\n");
            writer.write("Full Name: " + tourist.getFullName() + "\n");
            writer.write("Email: " + tourist.getEmail() + "\n");
            writer.write("Phone: " + tourist.getPhone() + "\n");
            writer.write("Nationality: " + tourist.getNationality() + "\n");
            writer.write("Role: Tourist\n");
            writer.write("Total Spent: " + tourist.getTotalSpent() + "\n");
            writer.write(SEPARATOR + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static List<Tourist> loadTourists() {
        List<Tourist> tourists = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(TOURISTS_FILE))) {
            String line;
            Map<String, String> data = new HashMap<>();
            
            while ((line = reader.readLine()) != null) {
                if (line.equals(SEPARATOR)) {
                    if (!data.isEmpty()) {
                        Tourist tourist = new Tourist(
                            data.get("Username"),
                            data.get("Password"),
                            data.get("Full Name"),
                            data.get("Email"),
                            data.get("Phone"),
                            data.get("Nationality")
                        );
                        // After creating the tourist, set the total spent if available
                        if (data.containsKey("Total Spent")) {
                            try {
                                double spent = Double.parseDouble(data.get("Total Spent"));
                                tourist.setTotalSpent(spent);
                            } catch (NumberFormatException e) {
                                System.err.println("Error parsing tourist spending: " + e.getMessage());
                            }
                        }
                        tourists.add(tourist);
                        data.clear();
                    }
                } else if (line.contains(": ")) {
                    String[] parts = line.split(": ", 2);
                    data.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet, return empty list
        }
        return tourists;
    }
    
    // Guide operations
    public static void saveGuide(Guide guide) {
        try (FileWriter writer = new FileWriter(GUIDES_FILE, true)) {
            writer.write("Username: " + guide.getUsername() + "\n");
            writer.write("Password: " + guide.getPassword() + "\n");
            writer.write("Full Name: " + guide.getFullName() + "\n");
            writer.write("Email: " + guide.getEmail() + "\n");
            writer.write("Phone: " + guide.getPhone() + "\n");
            writer.write("Languages: " + String.join(", ", guide.getLanguages()) + "\n");
            writer.write("Experience: " + guide.getExperienceYears() + "\n");
            writer.write("Role: Guide\n");
            writer.write("Total Earnings: " + guide.getTotalEarnings() + "\n");
            writer.write(SEPARATOR + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static List<Guide> loadGuides() {
        List<Guide> guides = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(GUIDES_FILE))) {
            String line;
            Map<String, String> data = new HashMap<>();
            
            while ((line = reader.readLine()) != null) {
                if (line.equals(SEPARATOR)) {
                    if (!data.isEmpty() && data.containsKey("Languages") && data.containsKey("Experience")) {
                        try {
                            List<String> languages = Arrays.asList(data.get("Languages").split("\\s*,\\s*"));
                            int experience = Integer.parseInt(data.get("Experience"));
                            
                            Guide guide = new Guide(
                                data.get("Username"),
                                data.get("Password"),
                                data.get("Full Name"),
                                data.get("Email"),
                                data.get("Phone"),
                                languages,
                                experience
                            );
                            // After creating the guide, set the total earnings if available
                            if (data.containsKey("Total Earnings")) {
                                try {
                                    double earnings = Double.parseDouble(data.get("Total Earnings"));
                                    guide.setTotalEarnings(earnings);
                                } catch (NumberFormatException e) {
                                    System.err.println("Error parsing guide earnings: " + e.getMessage());
                                }
                            }
                            guides.add(guide);
                        } catch (NumberFormatException e) {
                            System.err.println("Error parsing guide experience: " + e.getMessage());
                        }
                        data.clear();
                    }
                } else if (line.contains(": ")) {
                    String[] parts = line.split(": ", 2);
                    if (parts.length == 2) {
                        data.put(parts[0], parts[1]);
                    }
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet, return empty list
            System.out.println("Guides file not found, returning empty list");
        }
        return guides;
    }
    
    // Attraction operations
    public static void saveAttraction(Attraction attraction) {
        List<Attraction> attractions = loadAttractions();
        attractions.add(attraction);
        saveAllAttractions(attractions);
    }
    
    public static List<Attraction> loadAttractions() {
        List<Attraction> attractions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ATTRACTIONS_FILE))) {
            String line;
            Map<String, String> data = new HashMap<>();
            
            while ((line = reader.readLine()) != null) {
                if (line.equals(SEPARATOR)) {
                    if (!data.isEmpty()) {
                        Attraction attraction = new Attraction(
                            data.get("Name"),
                            data.get("Location"),
                            data.get("Altitude"),
                            data.get("Difficulty"),
                            Double.parseDouble(data.get("Base Price"))
                        );
                        attractions.add(attraction);
                        data.clear();
                    }
                } else if (line.contains(": ")) {
                    String[] parts = line.split(": ", 2);
                    data.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet, return empty list
        }
        return attractions;
    }
    
    private static void saveAllAttractions(List<Attraction> attractions) {
        try (FileWriter writer = new FileWriter(ATTRACTIONS_FILE)) {
            for (Attraction attraction : attractions) {
                writer.write("Name: " + attraction.getName() + "\n");
                writer.write("Location: " + attraction.getLocation() + "\n");
                writer.write("Altitude: " + attraction.getAltitudeLevel() + "\n");
                writer.write("Difficulty: " + attraction.getDifficulty() + "\n");
                writer.write("Base Price: " + attraction.getBasePrice() + "\n");
                writer.write(SEPARATOR + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Booking operations
    public static void saveBooking(Booking booking) {
        try (FileWriter writer = new FileWriter(BOOKINGS_FILE, true)) {
            writer.write("Booking ID: " + booking.getBookingId() + "\n");
            writer.write("Tourist: " + booking.getTouristUsername() + "\n");
            writer.write("Guide: " + booking.getGuideUsername() + "\n");
            writer.write("Attraction: " + booking.getAttraction().getName() + "\n");
            writer.write("Trek Date: " + booking.getTrekDate() + "\n");
            writer.write("Status: " + booking.getStatus() + "\n");
            writer.write("Total Price: " + booking.getTotalPrice() + "\n");
            writer.write("Festival Discount: " + booking.isFestivalDiscountApplied() + "\n");
            writer.write(SEPARATOR + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static List<Booking> loadBookings() {
        List<Booking> bookings = new ArrayList<>();
        List<Attraction> attractions = loadAttractions();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKINGS_FILE))) {
            String line;
            Map<String, String> data = new HashMap<>();
            
            while ((line = reader.readLine()) != null) {
                if (line.equals(SEPARATOR)) {
                    if (!data.isEmpty()) {
                        // Find the attraction
                        Attraction attraction = attractions.stream()
                            .filter(a -> a.getName().equals(data.get("Attraction")))
                            .findFirst()
                            .orElse(null);
                        
                        if (attraction != null) {
                            Booking booking = new Booking(
                                data.get("Tourist"),
                                attraction,
                                LocalDate.parse(data.get("Trek Date"))
                            );
                            booking.setGuideUsername(data.get("Guide"));
                            booking.setStatus(data.get("Status"));
                            bookings.add(booking);
                        }
                        data.clear();
                    }
                } else if (line.contains(": ")) {
                    String[] parts = line.split(": ", 2);
                    data.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet, return empty list
        }
        return bookings;
    }
    
    // Initialize default data
    private static void initializeDefaultAttractions() {
        File file = new File(ATTRACTIONS_FILE);
        if (!file.exists()) {
            List<Attraction> defaultAttractions = Arrays.asList(
                new Attraction("Everest Base Camp", "Khumbu", "High", "Hard", 1200.0),
                new Attraction("Annapurna Circuit", "Annapurna", "High", "Medium", 800.0),
                new Attraction("Langtang Valley", "Langtang", "High", "Medium", 600.0),
                new Attraction("Chitwan Safari", "Chitwan", "Low", "Easy", 300.0),
                new Attraction("Pokhara Sightseeing", "Pokhara", "Low", "Easy", 150.0),
                new Attraction("Manaslu Circuit", "Manaslu", "High", "Hard", 1000.0)
            );
            saveAllAttractions(defaultAttractions);
        }
    }
    
    private static void initializeDefaultGuides() {
        File file = new File(GUIDES_FILE);
        if (!file.exists()) {
            List<String> languages1 = Arrays.asList("English", "Nepali", "Hindi");
            List<String> languages2 = Arrays.asList("English", "Nepali");
            
            Guide guide1 = new Guide("ram_guide", "ram123", "Ram Bahadur", "ram@guide.com", "+977-1234567", languages1, 8);
            Guide guide2 = new Guide("sita_guide", "sita123", "Sita Sharma", "sita@guide.com", "+977-7654321", languages2, 5);
            
            saveGuide(guide1);
            saveGuide(guide2);
        }
    }
}
