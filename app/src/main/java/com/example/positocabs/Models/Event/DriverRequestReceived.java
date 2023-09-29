package com.example.positocabs.Models.Event;

public class DriverRequestReceived {

    private String key;
    private String pickupLocation,pickupLocationString;
    private String dropLocation,dropLocationString;

    public DriverRequestReceived() {
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

    public String getPickupLocationString() {
        return pickupLocationString;
    }

    public void setPickupLocationString(String pickupLocationString) {
        this.pickupLocationString = pickupLocationString;
    }

    public String getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(String dropLocation) {
        this.dropLocation = dropLocation;
    }

    public String getDropLocationString() {
        return dropLocationString;
    }

    public void setDropLocationString(String dropLocationString) {
        this.dropLocationString = dropLocationString;
    }
}
