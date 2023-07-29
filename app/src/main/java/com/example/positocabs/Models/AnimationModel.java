package com.example.positocabs.Models;

public class AnimationModel {

    private boolean isRun;
    private GeoQueryModel geoQueryModel;

    public AnimationModel(boolean isRun, GeoQueryModel geoQueryModel) {
        this.isRun = isRun;
        this.geoQueryModel = geoQueryModel;
    }

    public boolean isRun() {
        return isRun;
    }

    public void setRun(boolean run) {
        isRun = run;
    }

    public GeoQueryModel getGeoQueryModel() {
        return geoQueryModel;
    }

    public void setGeoQueryModel(GeoQueryModel geoQueryModel) {
        this.geoQueryModel = geoQueryModel;
    }
}
