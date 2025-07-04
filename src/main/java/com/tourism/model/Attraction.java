package com.tourism.model;

public class Attraction {
    private String name;
    private String altitude;
    private String difficulty;
    private double basePrice;

    public Attraction() {}

    public Attraction(String name, String altitude, String difficulty, double basePrice) {
        this.name = name;
        this.altitude = altitude;
        this.difficulty = difficulty;
        this.basePrice = basePrice;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAltitude() { return altitude; }
    public void setAltitude(String altitude) { this.altitude = altitude; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }

    @Override
    public String toString() {
        return "Name: " + name + "\n" +
               "Altitude: " + altitude + "\n" +
               "Difficulty: " + difficulty + "\n" +
               "Base Price: $" + basePrice + "\n" +
               "------------------------\n";
    }
}
