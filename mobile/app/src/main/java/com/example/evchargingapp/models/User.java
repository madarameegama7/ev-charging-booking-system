package com.example.evchargingapp.models;

public class User {
    private String nic;
    private String name;
    private String email;
    private String password;
    private boolean isActive;

    public User(String nic, String name, String email, String password, boolean isActive) {
        this.nic = nic;
        this.name = name;
        this.email = email;
        this.password = password;
        this.isActive = isActive;
    }

    // Getters and Setters
    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
