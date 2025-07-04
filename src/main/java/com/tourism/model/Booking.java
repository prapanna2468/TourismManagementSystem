package com.tourism.model;

import java.time.LocalDate;

public class Booking {
    private String bookingId;
    private String tourist;
    private String attraction;
    private LocalDate date;
    private String difficulty;
    private double price;
    private String status;
    private String guide;

    public Booking() {}

    public Booking(String bookingId, String tourist, String attraction, LocalDate date, 
                   String difficulty, double price, String status) {
        this.bookingId = bookingId;
        this.tourist = tourist;
        this.attraction = attraction;
        this.date = date;
        this.difficulty = difficulty;
        this.price = price;
        this.status = status;
        this.guide = "Unassigned";
    }

    // Getters and Setters
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getTourist() { return tourist; }
    public void setTourist(String tourist) { this.tourist = tourist; }

    public String getAttraction() { return attraction; }
    public void setAttraction(String attraction) { this.attraction = attraction; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getGuide() { return guide; }
    public void setGuide(String guide) { this.guide = guide; }

    @Override
    public String toString() {
        return "Booking ID: " + bookingId + "\n" +
               "Tourist: " + tourist + "\n" +
               "Attraction: " + attraction + "\n" +
               "Date: " + date + "\n" +
               "Difficulty: " + difficulty + "\n" +
               "Price: $" + price + "\n" +
               "Status: " + status + "\n" +
               "Guide: " + guide + "\n" +
               "------------------------\n";
    }
}
