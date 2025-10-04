/*
 * File: Booking.java
 * Purpose: Represents a reservation made by an EV Owner
 */

package com.example.evchargingapp.models;

public class Booking {

    private String bookingId;
    private String stationId;
    private String ownerNic;
    private String startTimeUtc;
    private String endTimeUtc;
    private String status; // Pending, Approved, Cancelled, Completed

    public Booking() {}

    public Booking(String bookingId, String stationId, String ownerNic, String startTimeUtc, String endTimeUtc, String status) {
        this.bookingId = bookingId;
        this.stationId = stationId;
        this.ownerNic = ownerNic;
        this.startTimeUtc = startTimeUtc;
        this.endTimeUtc = endTimeUtc;
        this.status = status;
    }

    // Getters and Setters
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getStationId() { return stationId; }
    public void setStationId(String stationId) { this.stationId = stationId; }

    public String getOwnerNic() { return ownerNic; }
    public void setOwnerNic(String ownerNic) { this.ownerNic = ownerNic; }

    public String getStartTimeUtc() { return startTimeUtc; }
    public void setStartTimeUtc(String startTimeUtc) { this.startTimeUtc = startTimeUtc; }

    public String getEndTimeUtc() { return endTimeUtc; }
    public void setEndTimeUtc(String endTimeUtc) { this.endTimeUtc = endTimeUtc; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
