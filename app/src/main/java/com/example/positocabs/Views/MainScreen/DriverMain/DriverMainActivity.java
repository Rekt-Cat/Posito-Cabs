package com.example.positocabs.Views.MainScreen.DriverMain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.positocabs.R;
import com.example.positocabs.Views.MainScreen.DriverMain.Fragments.DriverNotificationFragment;
import com.example.positocabs.Views.MainScreen.DriverMain.Fragments.DriverProfileFragment;
import com.example.positocabs.Views.MainScreen.DriverMain.Fragments.DriverMapsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class DriverMainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main);

        bottomNavigationView=findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if(id==R.id.nav_home){
                    loadFrag(new DriverMapsFragment(), true);
                }
                else if (id==R.id.nav_notification){
                    loadFrag(new DriverNotificationFragment(), false);
                }
                else{
                    loadFrag(new DriverProfileFragment(), false);
                }

                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_home);

    }

    public void loadFrag(Fragment fragment, boolean flag){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(flag){
            fragmentTransaction.add(R.id.driver_container, fragment).addToBackStack(null);
        }
        else {
            fragmentTransaction.replace(R.id.driver_container, fragment);
        }

        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.driver_container);

        if (currentFragment instanceof DriverMapsFragment) {

        } else {

            super.onBackPressed();
        }
    }
}