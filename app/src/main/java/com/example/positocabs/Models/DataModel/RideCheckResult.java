package com.example.positocabs.Models.DataModel;

public class RideCheckResult {
    private Booking booking;
    private String status;

    public RideCheckResult(Booking booking, String status) {
        this.booking = booking;
        this.status = status;
    }

    public RideCheckResult() {
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
