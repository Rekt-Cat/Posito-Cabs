package com.example.positocabs.Views.SplashScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.positocabs.R;
import com.example.positocabs.Views.MainScreen.DriverMain.DriverMainActivity;
import com.example.positocabs.Views.MainScreen.RiderMain.RiderMainActivity;
import com.example.positocabs.Views.Auth.OnBoardingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class SplashScreen extends AppCompatActivity implements FirebaseAuth.AuthStateListener {
    public static final int REQUEST_CODE = 1;
    FirebaseAuth auth;
    String name;
    NetworkInfo info = null;
    boolean connected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        auth = FirebaseAuth.getInstance();

        requestPermissions();
    }

    private boolean isNetworkAvailable() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        Log.d("lolt", "internet spl : " + connected);
        if (ContextCompat.checkSelfPermission(SplashScreen.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (connected) {
                if (auth.getCurrentUser() != null) {

                    checkUserType();
                } else {
                    startActivity(new Intent(SplashScreen.this, OnBoardingActivity.class));
                    finish();
                }
            } else {
                Toast.makeText(SplashScreen.this, "No Internet Connection!", Toast.LENGTH_LONG).show();
            }
        }


    }


    @Override
    protected void onStart() {
        super.onStart();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                connected = isNetworkAvailable();
                FirebaseAuth.getInstance().addAuthStateListener(SplashScreen.this);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0) {
                List<String> deniedPermissions = new ArrayList<>();
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        deniedPermissions.add(permissions[i]);
                    }
                }

                if (deniedPermissions.isEmpty()) {
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    onAuthStateChanged(firebaseAuth);
                } else {
                    Toast.makeText(this, "Please Restart the app and grant all the permissions.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Permission cancelled, Please restart the app and grant all the permissions ", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void requestPermissions() {
        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }

        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[0]), REQUEST_CODE);
        } else {

        }
    }

    private void checkUserType(){
        // Check if the user's choice(userType) is already stored in SharedPreferences
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String userType = preferences.getString("userType", "");

        // Redirect the user to their corresponding activity based on the stored choice
        if (userType.equals("Rider")) {
            startActivity(new Intent(SplashScreen.this, RiderMainActivity.class));
        } else if (userType.equals("Driver")) {
            startActivity(new Intent(SplashScreen.this, DriverMainActivity.class));
        } else {
            // If the user's choice is not stored or unknown
            startActivity(new Intent(SplashScreen.this, OnBoardingActivity.class));
        }

        // Finish the splash activity to prevent the user from navigating back to it
        finish();
    }
}