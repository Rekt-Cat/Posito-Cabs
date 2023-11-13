package com.example.positocabs.Utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.positocabs.Callback.StartMainActivity;
import com.example.positocabs.Views.SplashScreen.SplashScreen;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

public class UserLocationListener extends BroadcastReceiver {



    private static final String TAG = "MyLocationReceiver";
    public static final int REQUEST_CHECK_SETTING=1001;
    private Context context;
    private Snackbar snackbar;
    private Activity activity;

    private int activityType;

    private LocationRequest locationRequest;

    public UserLocationListener(Context context, Snackbar snackbar,Activity activity,int activityType) {
        this.context = context;
        this.snackbar = snackbar;
        this.activity=activity;
        this.activityType=activityType;
    }

    @Override
    public void onReceive(Context context, Intent intent) {


        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (gpsEnabled && networkEnabled) {
                if (snackbar != null) {
                    snackbar.dismiss();
                }
                Log.d(TAG, "GPS is enabled");

            } else {

                locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                        .setIntervalMillis(5000)
                        .setMinUpdateIntervalMillis(2000)
                        .setWaitForAccurateLocation(false)
                        .setMaxUpdateDelayMillis(3000)
                        .setMinUpdateDistanceMeters(10f)
                        .build();


                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest);
                builder.setAlwaysShow(true);

                Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(context)
                                .checkLocationSettings(builder.build());
                result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                        try {
                            LocationSettingsResponse response = task.getResult(ApiException.class);
                        } catch (ApiException e) {
                            int code = e.getStatusCode();
                            if(code== LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                                try {
                                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                    resolvableApiException.startResolutionForResult(activity, REQUEST_CHECK_SETTING);
                                } catch (IntentSender.SendIntentException ex) {

                                }
                            }
                        }
                    }
                });


                snackbar.show();
                Log.d(TAG, "GPS is disabled");
            }
        }

    }



}

