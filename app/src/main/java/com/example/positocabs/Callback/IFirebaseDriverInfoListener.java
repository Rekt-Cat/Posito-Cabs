package com.example.positocabs.Callback;

import android.view.View;

import com.example.positocabs.Models.DriverGeoModel;

public interface IFirebaseDriverInfoListener {

    void onDriverInfoLoadSuccess(DriverGeoModel driverGeoModel, View view);

}
