package com.tourism.models;

// Base class demonstrating inheritance and encapsulation
public abstract class Person {
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    
    // Constructor
    public Person(String username, String password, String fullName, String email, String phone) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }
    
    // Encapsulation - Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    // Abstract method for polymorphism
    public abstract String getRole();
    
    // Abstract method for dashboard access
    public abstract String getDashboardInfo();
    
    // Common method for all persons
    public String getContactInfo() {
        return "Email: " + email + ", Phone: " + phone;
    }
    
    @Override
    public String toString() {
        return "Username: " + username + "\n" +
               "Full Name: " + fullName + "\n" +
               "Email: " + email + "\n" +
               "Phone: " + phone + "\n" +
               "Role: " + getRole();
    }
}
