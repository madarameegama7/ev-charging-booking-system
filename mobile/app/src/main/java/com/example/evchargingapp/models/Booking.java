package com.example.evchargingapp.models;

public class Booking {
    private String id;
    private String bookingId;
    private String stationId;
    private String ownerNic;
    private String startTime;  // Changed from startTimeUtc
    private String endTime;    // Changed from endTimeUtc
    private int status;

    // Constructors
    public Booking() {}

    public Booking(String id, String bookingId, String stationId, String ownerNic, 
                   String startTime, String endTime, int status) {
        this.id = id;
        this.bookingId = bookingId;
        this.stationId = stationId;
        this.ownerNic = ownerNic;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getStationId() { return stationId; }
    public void setStationId(String stationId) { this.stationId = stationId; }

    public String getOwnerNic() { return ownerNic; }
    public void setOwnerNic(String ownerNic) { this.ownerNic = ownerNic; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getStatusText() {
        switch (status) {
            case 0: return "Pending";
            case 1: return "Approved";
            case 2: return "Cancelled";
            case 3: return "Completed";
            default: return "Unknown";
        }
    }

    // For backward compatibility - you can keep these or remove them
    public String getStartTimeUtc() { return startTime; }
    public void setStartTimeUtc(String startTimeUtc) { this.startTime = startTimeUtc; }
    
    public String getEndTimeUtc() { return endTime; }
    public void setEndTimeUtc(String endTimeUtc) { this.endTime = endTimeUtc; }
}