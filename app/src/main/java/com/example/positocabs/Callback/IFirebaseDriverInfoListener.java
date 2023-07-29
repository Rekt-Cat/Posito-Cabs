package com.example.positocabs.Callback;

import com.example.positocabs.Models.DriverGeoModel;

public interface IFirebaseDriverInfoListener {

    void onDriverInfoLoadSuccess(DriverGeoModel driverGeoModel);

}
