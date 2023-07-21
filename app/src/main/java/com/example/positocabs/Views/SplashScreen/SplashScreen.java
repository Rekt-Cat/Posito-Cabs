package com.example.positocabs.Views.SplashScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.positocabs.R;
import com.example.positocabs.Services.Common;
import com.example.positocabs.Services.UsersLocationService;
import com.example.positocabs.Utils.UserUtils;
import com.example.positocabs.Views.Maps.MapsFragment;
import com.example.positocabs.Views.OnBoardingActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class SplashScreen extends AppCompatActivity implements FirebaseAuth.AuthStateListener {
    public static final int REQUEST_CODE = 1;
    FirebaseAuth auth;
    String name;
    NetworkInfo info = null;
    boolean connected;
    String[] token = {""};


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

                    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if(task.isComplete()){
                                token[0]=String.valueOf(task.getResult());
                                UserUtils.updateToken(SplashScreen.this,token[0]);
                            }
                        }
                    });
                    //startActivity(new Intent(SplashScreen.this, MapsFragment.class));
                    finish();
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
                //startLocationService();

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

//    private void startLocationService() {
//        if(!isLocalServiceRunning()){
//            Intent i = new Intent(SplashScreen.this,UsersLocationService.class);
//            i.setAction(Common.ACTION_START_LOCATION_SERVICE);
//            startService(i);
//        }
//
//    }
//    private void stopLocationService() {
//        if(!isLocalServiceRunning()){
//            Intent i = new Intent(SplashScreen.this,UsersLocationService.class);
//            i.setAction(Common.ACTION_STOP_LOCATION_SERVICE);
//            stopService(i);
//        }
//    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
       // stopLocationService();
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

//    public boolean isLocalServiceRunning(){
//        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        if(activityManager!=null){
//            for(ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)){
//                if(UsersLocationService.class.getName().equals(service.service.getClassName())){
//                    if(service.foreground){
//                        return true;
//                    }
//                }
//            }
//            return false;
//        }
//        return false;
//
//    }


}