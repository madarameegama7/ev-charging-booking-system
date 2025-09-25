/*
 * File: Reservation.java
 * Author: Janudi Adhikari
 * Date: 2025-09-25
 * Purpose: Model class representing a Reservation in EVChargingApp
 */

package com.example.evchargingapp.models;

public class Reservation {
    private String reservationId;
    private String nic;
    private String stationId;
    private String dateTime;
    private String status;

    // Constructor
    // Initializes a new Reservation object with all fields
    public Reservation(String reservationId, String nic, String stationId, String dateTime, String status) {
        this.reservationId = reservationId;
        this.nic = nic;
        this.stationId = stationId;
        this.dateTime = dateTime;
        this.status = status;
    }

    // Getters and Setters
    public String getReservationId() { return reservationId; }
    public void setReservationId(String reservationId) { this.reservationId = reservationId; }

    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }

    public String getStationId() { return stationId; }
    public void setStationId(String stationId) { this.stationId = stationId; }

    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
