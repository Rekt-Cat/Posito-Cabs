package com.example.positocabs.Models.Event;

public class DriverRequestReceived {

    private String key;
    private String pickupLocation,pickupLocationString;
    private String dropLocation,dropLocationString,distanceString,duration,riderPrice;
    private String distanceInt;
    private String tripId;
    private String riderId;

    public DriverRequestReceived() {
    }

    public String getRiderId() {
        return riderId;
    }

    public void setRiderId(String riderId) {
        this.riderId = riderId;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getRiderPrice() {
        return riderPrice;
    }

    public void setRiderPrice(String riderPrice) {
        this.riderPrice = riderPrice;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
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

    public String getDistanceString() {
        return distanceString;
    }

    public void setDistanceString(String distanceString) {
        this.distanceString = distanceString;
    }

    public String getDistanceInt() {
        return distanceInt;
    }

    public void setDistanceInt(String distanceInt) {
        this.distanceInt = distanceInt;
    }
}
