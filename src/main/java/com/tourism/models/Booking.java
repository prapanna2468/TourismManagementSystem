package com.tourism.models;

import java.time.LocalDate;
import java.time.Month;

// Booking class demonstrating encapsulation and composition
public class Booking {
    private static int nextId = 1;
    
    private int bookingId;
    private String touristUsername;
    private String guideUsername;
    private Attraction attraction;
    private LocalDate bookingDate;
    private LocalDate trekDate;
    private String status; // "Confirmed", "Pending", "Cancelled", "Completed"
    private double totalPrice;
    private boolean festivalDiscountApplied;
    private String notes;
    
    // Constructor
    public Booking(String touristUsername, Attraction attraction, LocalDate trekDate) {
        this.bookingId = nextId++;
        this.touristUsername = touristUsername;
        this.attraction = attraction;
        this.bookingDate = LocalDate.now();
        this.trekDate = trekDate;
        this.status = "Pending";
        this.guideUsername = "";
        this.notes = "";
        
        // Calculate price and check for festival discount
        this.festivalDiscountApplied = isFestivalSeason(trekDate);
        this.totalPrice = attraction.calculatePrice(festivalDiscountApplied);
    }
    
    // Constructor with booking ID (for loading from file)
    public Booking(int bookingId, String touristUsername, Attraction attraction, LocalDate trekDate) {
        this.bookingId = bookingId;
        this.touristUsername = touristUsername;
        this.attraction = attraction;
        this.bookingDate = LocalDate.now();
        this.trekDate = trekDate;
        this.status = "Pending";
        this.guideUsername = "";
        this.notes = "";
        
        // Update nextId to prevent conflicts
        if (bookingId >= nextId) {
            nextId = bookingId + 1;
        }
        
        // Calculate price and check for festival discount
        this.festivalDiscountApplied = isFestivalSeason(trekDate);
        this.totalPrice = attraction.calculatePrice(festivalDiscountApplied);
    }
    
    // Static method to set next ID (for loading from file)
    public static void setNextId(int id) {
        nextId = id;
    }
    
    public static int getNextId() {
        return nextId;
    }
    
    // Encapsulation - Getters and Setters
    public int getBookingId() { return bookingId; }
    
    public String getTouristUsername() { return touristUsername; }
    public void setTouristUsername(String touristUsername) { this.touristUsername = touristUsername; }
    
    public String getGuideUsername() { return guideUsername; }
    public void setGuideUsername(String guideUsername) { this.guideUsername = guideUsername; }
    
    public Attraction getAttraction() { return attraction; }
    public void setAttraction(Attraction attraction) { 
        this.attraction = attraction;
        // Recalculate price when attraction changes
        this.totalPrice = attraction.calculatePrice(festivalDiscountApplied);
    }
    
    public LocalDate getBookingDate() { return bookingDate; }
    
    public LocalDate getTrekDate() { return trekDate; }
    public void setTrekDate(LocalDate trekDate) { 
        this.trekDate = trekDate;
        // Recalculate festival discount when date changes
        this.festivalDiscountApplied = isFestivalSeason(trekDate);
        this.totalPrice = attraction.calculatePrice(festivalDiscountApplied);
    }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public double getTotalPrice() { return totalPrice; }
    
    public boolean isFestivalDiscountApplied() { return festivalDiscountApplied; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    // Business logic methods
    private boolean isFestivalSeason(LocalDate date) {
        Month month = date.getMonth();
        return month == Month.AUGUST || month == Month.SEPTEMBER || month == Month.OCTOBER;
    }
    
    public boolean isUpcoming() {
        return trekDate.isAfter(LocalDate.now()) && 
               ("Confirmed".equals(status) || "Pending".equals(status));
    }
    
    public boolean canBeCancelled() {
        return trekDate.isAfter(LocalDate.now().plusDays(7)) && 
               ("Confirmed".equals(status) || "Pending".equals(status));
    }
    
    public boolean canBeModified() {
        return trekDate.isAfter(LocalDate.now().plusDays(3)) && 
               ("Confirmed".equals(status) || "Pending".equals(status));
    }
    
    public boolean canBeDeleted() {
        return "Cancelled".equals(status);
    }
    
    public void confirmBooking() {
        if ("Pending".equals(status)) {
            this.status = "Confirmed";
            attraction.incrementBookings();
        }
    }
    
    public void cancelBooking() {
        if (canBeCancelled()) {
            this.status = "Cancelled";
            attraction.decrementBookings();
        }
    }
    
    public String getFestivalDiscountMessage() {
        if (festivalDiscountApplied) {
            return "🎉 Festival Discount Applied! (Dashain & Tihar Season - 20% OFF)";
        }
        return "";
    }
    
    @Override
    public String toString() {
        String guideInfo = guideUsername.isEmpty() ? "Not Assigned" : guideUsername;
        
        return "Booking ID: " + bookingId + "\n" +
               "Tourist: " + touristUsername + "\n" +
               "Guide: " + guideInfo + "\n" +
               "Attraction: " + attraction.getName() + "\n" +
               "Trek Date: " + trekDate + "\n" +
               "Status: " + status + "\n" +
               "Total Price: $" + String.format("%.2f", totalPrice) + "\n" +
               (festivalDiscountApplied ? "Festival Discount: Applied (20% OFF)\n" : "") +
               "Booking Date: " + bookingDate;
    }
}
