package com.example.positocabs.Views.MainScreen.RiderMain;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.positocabs.R;
import com.example.positocabs.Views.MainScreen.RiderMain.Fragment.BottomSheetFrag.BAddressFragment;
import com.example.positocabs.Views.MainScreen.RiderMain.Fragment.RequestDriverFragment;
import com.example.positocabs.Views.MainScreen.RiderMain.Fragment.RiderDiscountFragment;
import com.example.positocabs.Views.MainScreen.RiderMain.Fragment.RiderMapsFragment;
import com.example.positocabs.Views.MainScreen.RiderMain.Fragment.RiderProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class RiderMainActivity extends AppCompatActivity implements RiderMapsFragment.BottomSheetListener, BAddressFragment.SearchForDrivers {

    private BottomNavigationView bottomNavigationView;
    private boolean isBottomSheetOpen = false;

    //initializing rider frags
    private RiderMapsFragment riderMapsFragment = new RiderMapsFragment();
    private RiderDiscountFragment riderDiscountFragment = new RiderDiscountFragment();
    private RiderProfileFragment riderProfileFragment = new RiderProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_main);

        //casting views
        bottomNavigationView=findViewById(R.id.bottom_navigation);

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
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.driver_container);

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


    @Override
    public void findDrivers() {
        Context context=this;
        RequestDriverFragment requestDriverFragment= new RequestDriverFragment();
        requestDriverFragment.sendRequestToDrivers(context);
    }
}