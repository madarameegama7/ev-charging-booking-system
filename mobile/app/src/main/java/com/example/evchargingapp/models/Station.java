package com.example.evchargingapp.models;

public class Station {
    private String id;
    private String name;
    private GeoLocation location;
    private String type;
    private int availableSlots;
    private boolean isActive;

    public Station() {}

    public Station(String id, String name, GeoLocation location, String type, int availableSlots, boolean isActive) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.type = type;
        this.availableSlots = availableSlots;
        this.isActive = isActive;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public GeoLocation getLocation() { return location; }
    public void setLocation(GeoLocation location) { this.location = location; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getAvailableSlots() { return availableSlots; }
    public void setAvailableSlots(int availableSlots) { this.availableSlots = availableSlots; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
