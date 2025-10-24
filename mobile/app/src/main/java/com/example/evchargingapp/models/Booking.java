/*
 * File: Booking.java
 * Purpose: Represents a reservation made by an EV Owner
 */

package com.example.evchargingapp.models;

import com.google.gson.annotations.SerializedName;

public class Booking {

    @SerializedName("bookingId")
    private String bookingId;

    @SerializedName("stationId")
    private String stationId;

    @SerializedName("ownerNic")
    private String ownerNic;

    @SerializedName("startTimeUtc")
    private String startTimeUtc;

    @SerializedName("endTimeUtc")
    private String endTimeUtc;

    // Backend sends status as an integer (0, 1, 2, 3)
    @SerializedName("status")
    private int status;

    public Booking() {}

    public Booking(String bookingId, String stationId, String ownerNic, String startTimeUtc, String endTimeUtc, int status) {
        this.bookingId = bookingId;
        this.stationId = stationId;
        this.ownerNic = ownerNic;
        this.startTimeUtc = startTimeUtc;
        this.endTimeUtc = endTimeUtc;
        this.status = status;
    }

    // Getters
    public String getBookingId() { return bookingId; }
    public String getStationId() { return stationId; }
    public String getOwnerNic() { return ownerNic; }
    public String getStartTimeUtc() { return startTimeUtc; }
    public String getEndTimeUtc() { return endTimeUtc; }
    public int getStatus() { return status; }

    // Setters
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public void setStationId(String stationId) { this.stationId = stationId; }
    public void setOwnerNic(String ownerNic) { this.ownerNic = ownerNic; }
    public void setStartTimeUtc(String startTimeUtc) { this.startTimeUtc = startTimeUtc; }
    public void setEndTimeUtc(String endTimeUtc) { this.endTimeUtc = endTimeUtc; }
    public void setStatus(int status) { this.status = status; }

    // Utility: Convert numeric status to readable string
    public String getStatusText() {
        switch (status) {
            case 0: return "Pending";
            case 1: return "Approved";
            case 2: return "Cancelled";
            case 3: return "Completed";
            default: return "Unknown";
        }
    }
}
