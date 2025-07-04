package com.tourism.utils;

import java.util.HashMap;
import java.util.Map;

public class LanguageManager {
    private static boolean isNepali = false;
    private static Map<String, String> translations = new HashMap<>();
    
    static {
        initializeTranslations();
    }
    
    private static void initializeTranslations() {
        // Login/Register
        translations.put("Login", "लगइन");
        translations.put("Register", "दर्ता");
        translations.put("Username", "प्रयोगकर्ता नाम");
        translations.put("Password", "पासवर्ड");
        translations.put("Full Name", "पूरा नाम");
        translations.put("Email", "इमेल");
        translations.put("Phone", "फोन");
        translations.put("Role", "भूमिका");
        translations.put("Tourist", "पर्यटक");
        translations.put("Guide", "गाइड");
        translations.put("Admin", "प्रशासक");
        
        // Dashboard
        translations.put("Dashboard", "ड्यासबोर्ड");
        translations.put("Welcome", "स्वागतम्");
        translations.put("Bookings", "बुकिङ");
        translations.put("Attractions", "आकर्षण");
        translations.put("Profile", "प्रोफाइल");
        translations.put("Logout", "लगआउट");
        
        // Booking
        translations.put("Book Now", "अहिले बुक गर्नुहोस्");
        translations.put("Select Attraction", "आकर्षण छान्नुहोस्");
        translations.put("Select Date", "मिति छान्नुहोस्");
        translations.put("Price", "मूल्य");
        translations.put("Total", "जम्मा");
        translations.put("Confirm", "पुष्टि गर्नुहोस्");
        translations.put("Cancel", "रद्द गर्नुहोस्");
        
        // Alerts
        translations.put("High Altitude Warning", "उच्च उचाइ चेतावनी");
        translations.put("Festival Discount Applied", "चाडपर्व छुट लागू");
        translations.put("Success", "सफल");
        translations.put("Error", "त्रुटि");
        
        // Common
        translations.put("Save", "सेभ गर्नुहोस्");
        translations.put("Delete", "मेटाउनुहोस्");
        translations.put("Edit", "सम्पादन गर्नुहोस्");
        translations.put("Search", "खोज्नुहोस्");
        translations.put("Add", "थप्नुहोस्");
        translations.put("Update", "अपडेट गर्नुहोस्");
        translations.put("View", "हेर्नुहोस्");
        translations.put("Back", "फिर्ता");
        translations.put("Next", "अर्को");
        translations.put("Previous", "अघिल्लो");
        translations.put("Close", "बन्द गर्नुहोस्");
        translations.put("Yes", "हो");
        translations.put("No", "होइन");
    }
    
    public static void toggleLanguage() {
        isNepali = !isNepali;
    }
    
    public static boolean isNepali() {
        return isNepali;
    }
    
    public static String getText(String key) {
        if (isNepali && translations.containsKey(key)) {
            return translations.get(key);
        }
        return key;
    }
    
    public static String getCurrentLanguage() {
        return isNepali ? "नेपाली" : "English";
    }
}
