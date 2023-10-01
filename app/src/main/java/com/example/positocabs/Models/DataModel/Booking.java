package com.example.positocabs.Models.DataModel;

public class Booking {
    String pickUpLocation,dropLocation;
    int price;


    public Booking(String pickUpLocation, String dropLocation) {
        this.pickUpLocation = pickUpLocation;
        this.dropLocation = dropLocation;
        price=0;
    }

    public Booking() {
    }

    public String getPickUpLocation() {
        return pickUpLocation;
    }

    public void setPickUpLocation(String pickUpLocation) {
        this.pickUpLocation = pickUpLocation;
    }

    public String getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(String dropLocation) {
        this.dropLocation = dropLocation;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
