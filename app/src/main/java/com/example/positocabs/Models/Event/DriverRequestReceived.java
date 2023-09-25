package com.example.positocabs.Models.Event;

public class DriverRequestReceived {

    private String key;
    private String pickupLocation;
    private String dropLocation;

    public DriverRequestReceived(String key, String pickupLocation, String dropLocation) {
        this.key = key;
        this.pickupLocation = pickupLocation;
        this.dropLocation = dropLocation;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(String dropLocation) {
        this.dropLocation = dropLocation;
    }
}
