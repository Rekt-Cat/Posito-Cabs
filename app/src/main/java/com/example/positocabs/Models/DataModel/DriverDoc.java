package com.example.positocabs.Models.DataModel;

public class DriverDoc {

    String dl,vehicleInsurance,pan,vehiclePermit,carType;

    public DriverDoc(String dl, String vehicleInsurance, String pan, String vehiclePermit, String carType) {
        this.dl = dl;
        this.vehicleInsurance = vehicleInsurance;
        this.pan = pan;
        this.vehiclePermit = vehiclePermit;
        this.carType = carType;
    }

    public DriverDoc() {
    }

    public String getDl() {
        return dl;
    }

    public void setDl(String dl) {
        this.dl = dl;
    }

    public String getVehicleInsurance() {
        return vehicleInsurance;
    }

    public void setVehicleInsurance(String vehicleInsurance) {
        this.vehicleInsurance = vehicleInsurance;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getVehiclePermit() {
        return vehiclePermit;
    }

    public void setVehiclePermit(String vehiclePermit) {
        this.vehiclePermit = vehiclePermit;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }
}
