package com.example.positocabs.Models.DataModel;

public class Trip {

    private String tripId, driverId, riderId, status;
    private Booking booking;
    private User user;


    public Trip() {
    }

    public Trip(String tripId, String driverId, String riderId, String status, Booking booking) {
        this.tripId = tripId;
        this.driverId = driverId;
        this.riderId = riderId;
        this.status = status;
        this.booking = booking;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getRiderId() {
        return riderId;
    }

    public void setRiderId(String riderId) {
        this.riderId = riderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }
}
