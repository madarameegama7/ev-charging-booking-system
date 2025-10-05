/*
 * File: Station.java
 * Purpose: Represents a charging station
 */

package com.example.evchargingapp.models;

public class Station {

    private String stationId;
    private String stationName;
    private String location;
    private GeoLocation geoLocation;
    private boolean isAvailable;

    public Station() {}

    public Station(String stationId, String stationName, String location, GeoLocation geoLocation, boolean isAvailable) {
        this.stationId = stationId;
        this.stationName = stationName;
        this.location = location;
        this.geoLocation = geoLocation;
        this.isAvailable = isAvailable;
    }

    // Getters and Setters
    public String getStationId() { return stationId; }
    public void setStationId(String stationId) { this.stationId = stationId; }

    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public GeoLocation getGeoLocation() { return geoLocation; }
    public void setGeoLocation(GeoLocation geoLocation) { this.geoLocation = geoLocation; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
}
