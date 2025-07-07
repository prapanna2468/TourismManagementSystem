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
        translations.put("Nationality", "राष्ट्रियता");
        translations.put("Languages", "भाषाहरू");
        translations.put("Experience", "अनुभव");
        
        // Registration specific
        translations.put("Create New Account - Journey", "नयाँ खाता बनाउनुहोस् - यात्रा");
        translations.put("Basic Information", "आधारभूत जानकारी");
        translations.put("Account Type", "खाताको प्रकार");
        translations.put("Additional Information", "थप जानकारी");
        translations.put("Create Account", "खाता बनाउनुहोस्");
        translations.put("Back to Login", "लगइनमा फर्कनुहोस्");
        translations.put("Required fields", "आवश्यक क्षेत्रहरू");
        translations.put("All information will be kept secure and confidential", "सबै जानकारी सुरक्षित र गोप्य राखिनेछ");
        
        // Form labels
        translations.put("Enter unique username", "अद्वितीय प्रयोगकर्ता नाम प्रविष्ट गर्नुहोस्");
        translations.put("Enter secure password", "सुरक्षित पासवर्ड प्रविष्ट गर्नुहोस्");
        translations.put("Enter your complete name", "आफ्नो पूरा नाम प्रविष्ट गर्नुहोस्");
        translations.put("your.email@example.com", "तपाईंको.इमेल@उदाहरण.com");
        translations.put("+977-XXXXXXXXX", "+९७७-XXXXXXXXX");
        translations.put("Choose your account type", "आफ्नो खाताको प्रकार छान्नुहोस्");
        translations.put("e.g., Nepali, Indian, American, etc.", "जस्तै, नेपाली, भारतीय, अमेरिकी, आदि");
        translations.put("Enter your country of citizenship", "आफ्नो नागरिकताको देश प्रविष्ट गर्नुहोस्");
        translations.put("English, Nepali, Hindi, Mandarin", "अंग्रेजी, नेपाली, हिन्दी, मन्डारिन");
        translations.put("List all languages you can speak fluently (separate with commas)", "तपाईंले धाराप्रवाह बोल्न सक्ने सबै भाषाहरू सूचीबद्ध गर्नुहोस् (अल्पविरामले छुट्याउनुहोस्)");
        translations.put("Enter number of years (0-50)", "वर्षहरूको संख्या प्रविष्ट गर्नुहोस् (०-५०)");
        translations.put("How many years have you been working as a tour guide?", "तपाईंले कति वर्षदेखि टुर गाइडको रूपमा काम गर्दै आउनुभएको छ?");
        
        // Help text
        translations.put("Choose 'Tourist' if you want to book treks, or 'Guide' if you want to offer guiding services", "यदि तपाईं ट्रेक बुक गर्न चाहनुहुन्छ भने 'पर्यटक' छान्नुहोस्, वा गाइडिङ सेवाहरू प्रदान गर्न चाहनुहुन्छ भने 'गाइड' छान्नुहोस्");
        
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
        translations.put("Exit", "बाहिर निस्कनुहोस्");
        translations.put("Refresh", "रिफ्रेस गर्नुहोस्");
        translations.put("Assign Guide", "गाइड नियुक्त गर्नुहोस्");
        translations.put("Update Status", "स्थिति अपडेट गर्नुहोस्");
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
