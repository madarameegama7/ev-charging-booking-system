package com.example.evchargingapp.models;

public class Booking {
    private String id;
    private String stationId;
    private String ownerNic;
    private String startTimeUtc;
    private String endTimeUtc;
    private String status;
    private String createdAtUtc;

    public Booking() {}

    public Booking(String id, String stationId, String ownerNic, String startTimeUtc,
                   String endTimeUtc, String status, String createdAtUtc) {
        this.id = id;
        this.stationId = stationId;
        this.ownerNic = ownerNic;
        this.startTimeUtc = startTimeUtc;
        this.endTimeUtc = endTimeUtc;
        this.status = status;
        this.createdAtUtc = createdAtUtc;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

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

    public String getCreatedAtUtc() { return createdAtUtc; }
    public void setCreatedAtUtc(String createdAtUtc) { this.createdAtUtc = createdAtUtc; }
}
