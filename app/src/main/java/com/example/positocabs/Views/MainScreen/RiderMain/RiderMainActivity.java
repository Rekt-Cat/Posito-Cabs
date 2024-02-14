package com.example.positocabs.Views.MainScreen.RiderMain;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.positocabs.Models.DataModel.User;
import com.example.positocabs.R;
import com.example.positocabs.Utils.NetworkChangeListener;
import com.example.positocabs.Utils.UserLocationListener;
import com.example.positocabs.Utils.UserUtils;
import com.example.positocabs.Views.MainScreen.RiderMain.Fragment.BottomSheetFrag.BAddressFragment;
import com.example.positocabs.Views.MainScreen.RiderMain.Fragment.RequestDriverFragment;
import com.example.positocabs.Views.MainScreen.RiderMain.Fragment.RiderDiscountFragment;
import com.example.positocabs.Views.MainScreen.RiderMain.Fragment.RiderMapsFragment;
import com.example.positocabs.Views.MainScreen.RiderMain.Fragment.RiderProfileFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;

public class RiderMainActivity extends AppCompatActivity implements RiderMapsFragment.BottomSheetListener {

    private BottomNavigationView bottomNavigationView;
    private boolean isBottomSheetOpen = false;

    //initializing rider frags
    private RiderMapsFragment riderMapsFragment = new RiderMapsFragment();
    private RiderDiscountFragment riderDiscountFragment = new RiderDiscountFragment();
    private RiderProfileFragment riderProfileFragment = new RiderProfileFragment();
    private UserLocationListener userLocationListener;
    private NetworkChangeListener networkChangeListener = new NetworkChangeListener();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_main);
        //saving token
        tokenSave(this);
        //casting views
        bottomNavigationView=findViewById(R.id.bottom_navigation);
        userLocationListener= new UserLocationListener(this, Snackbar.make(findViewById(android.R.id.content),"Location service is not enabled!",Snackbar.LENGTH_INDEFINITE)
        ,RiderMainActivity.this,0);

        //REGISTERING BROADCAST FOR USER LOCATION ENABLED OR NOT
        registerReceiver(userLocationListener, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        //REGISTERING BROADCAST FOR USER INTERNET IS ON OR NOT
        registerReceiver(networkChangeListener, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        //adding fragments to bottom nav
        getSupportFragmentManager().beginTransaction()
                .add(R.id.rider_container, riderMapsFragment)
                .add(R.id.rider_container, riderDiscountFragment)
                .add(R.id.rider_container, riderProfileFragment)
                .hide(riderDiscountFragment)
                .hide(riderProfileFragment)
                .commit();

        // Set up the bottom navigation item click listener
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();

                int id = item.getItemId();

                if(id == R.id.nav_discount){

                    transaction.hide(riderMapsFragment);
                    transaction.show(riderDiscountFragment);
                    transaction.hide(riderProfileFragment);
                }
                else if (id == R.id.nav_profile) {
                    transaction.hide(riderMapsFragment);
                    transaction.hide(riderDiscountFragment);
                    transaction.show(riderProfileFragment);
                }
                else {
                    transaction.show(riderMapsFragment);
                    transaction.hide(riderDiscountFragment);
                    transaction.hide(riderProfileFragment);
                }

                transaction.commit();
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {

        if(!isBottomSheetOpen){
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.rider_container);

            if (currentFragment instanceof RiderMapsFragment) {

            } else {

                super.onBackPressed();
            }
        }
    }

    @Override
    public void onBottomSheetOpened(boolean bool) {
        isBottomSheetOpen = bool;
    }

    public void tokenSave(Context context){
        FirebaseMessaging.getInstance().getToken().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                Log.d("hasee", "Token is : "+s);
                UserUtils.updateToken(context,s);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
       try {
           unregisterReceiver(userLocationListener);
       }
       catch (Exception e){
           Log.d("unregisterReceiverException", "exception : "+ e.getMessage());
       }

    }

    //    @Override
//    public void findDrivers() {
//        Context context=this;
//        RequestDriverFragment requestDriverFragment= new RequestDriverFragment();
//        requestDriverFragment.sendRequestToDrivers(context);
//    }
}