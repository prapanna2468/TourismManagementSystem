package com.tourism.models;

// Admin class inheriting from Person
public class Admin extends Person {
    private String department;
    private int accessLevel;
    
    public Admin(String username, String password, String fullName, String email, String phone) {
        super(username, password, fullName, email, phone);
        this.department = "Tourism Management";
        this.accessLevel = 10; // Highest access level
    }
    
    // Encapsulation
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public int getAccessLevel() { return accessLevel; }
    public void setAccessLevel(int accessLevel) { this.accessLevel = accessLevel; }
    
    // Polymorphism - Override abstract methods
    @Override
    public String getRole() {
        return "Admin";
    }
    
    @Override
    public String getDashboardInfo() {
        return "Welcome Administrator " + getFullName() + "!\n" +
               "Department: " + department + "\n" +
               "Access Level: " + accessLevel + "\n" +
               "Full System Access Granted";
    }
    
    // Admin-specific methods
    public boolean canManageUsers() {
        return accessLevel >= 5;
    }
    
    public boolean canViewAnalytics() {
        return accessLevel >= 3;
    }
    
    public boolean canManageSystem() {
        return accessLevel >= 8;
    }
    
    @Override
    public String toString() {
        return super.toString() + "\n" +
               "Department: " + department + "\n" +
               "Access Level: " + accessLevel;
    }
}
