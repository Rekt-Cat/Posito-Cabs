package com.example.positocabs.Models.DataModel;

public class Driver {

    private User user;
    private Booking booking;

    public Driver(User user, Booking booking) {
        this.user = user;
        this.booking = booking;
    }

    public Driver() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }
}
