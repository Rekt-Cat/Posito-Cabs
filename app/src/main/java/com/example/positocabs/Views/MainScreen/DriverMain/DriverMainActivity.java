package com.example.positocabs.Views.MainScreen.DriverMain;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.positocabs.R;
import com.example.positocabs.Views.MainScreen.DriverMain.Fragment.DriverMapsFragment;
import com.example.positocabs.Views.MainScreen.DriverMain.Fragment.DriverNotificationFragment;
import com.example.positocabs.Views.MainScreen.DriverMain.Fragment.DriverProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class DriverMainActivity extends AppCompatActivity {

   private BottomNavigationView bottomNavigationView;

    //initializing driver frags
    private DriverMapsFragment driverMapsFragment = new DriverMapsFragment();
    private DriverNotificationFragment driverNotificationFragment = new DriverNotificationFragment();
    private DriverProfileFragment driverProfileFragment = new DriverProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main);

        //casting views
        bottomNavigationView=findViewById(R.id.bottom_navigation);

        //adding fragments to bottom nav
        getSupportFragmentManager().beginTransaction()
                .add(R.id.driver_container, driverMapsFragment)
                .add(R.id.driver_container, driverNotificationFragment)
                .add(R.id.driver_container, driverProfileFragment)
                .hide(driverNotificationFragment)
                .hide(driverProfileFragment)
                .commit();

        // Set up the bottom navigation item click listener
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();

                int id = item.getItemId();

                if(id==R.id.nav_notification){
                    transaction.hide(driverMapsFragment);
                    transaction.show(driverNotificationFragment);
                    transaction.hide(driverProfileFragment);
                }
                else if (id==R.id.nav_profile){
                    transaction.hide(driverMapsFragment);
                    transaction.hide(driverNotificationFragment);
                    transaction.show(driverProfileFragment);
                }
                else{
                    transaction.show(driverMapsFragment);
                    transaction.hide(driverNotificationFragment);
                    transaction.hide(driverProfileFragment);
                }

                transaction.commit();
                return true;
            }
        });

    }
}