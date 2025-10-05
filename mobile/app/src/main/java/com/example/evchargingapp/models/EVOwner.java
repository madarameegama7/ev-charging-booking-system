/*
 * File: EVOwner.java
 * Purpose: Model class representing an EV Owner
 */

package com.example.evchargingapp.models;

public class EVOwner {

    private String nic;
    private String name;
    private String phone;
    private String email;
    private boolean isActive;

    // Empty constructor for JSON parsing
    public EVOwner() {
    }

    public EVOwner(String nic, String name, String phone, String email, boolean isActive) {
        this.nic = nic;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.isActive = isActive;
    }

    // Getters and Setters
    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
