```plaintext
package com.tourism.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class User {
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private String role;

    public User() {}

    public User(String username, String password, String fullName, String email, String phone, String role) {
        this.username = username;
        this.password = encryptPassword(password);
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    public static String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return password;
        }
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = encryptPassword(password); }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        return "Username: " + username + "\n" +
               "Password: " + password + "\n" +
               "Full Name: " + fullName + "\n" +
               "Email: " + email + "\n" +
               "Phone: " + phone + "\n" +
               "Role: " + role + "\n" +
               "------------------------\n";
    }
}
```
