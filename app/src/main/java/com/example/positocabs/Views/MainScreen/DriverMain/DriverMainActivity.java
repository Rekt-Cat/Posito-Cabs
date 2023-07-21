package com.example.positocabs.Views.MainScreen.DriverMain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.positocabs.R;
import com.example.positocabs.Views.MainScreen.DriverMain.Fragments.HomeFragment;
import com.example.positocabs.Views.MainScreen.DriverMain.Fragments.NotificationFragment;
import com.example.positocabs.Views.MainScreen.DriverMain.Fragments.ProfileFragment;
import com.example.positocabs.Views.Maps.MapsFragment;
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
                    loadFrag(new MapsFragment(), true);
                }
                else if (id==R.id.nav_notification){
                    loadFrag(new NotificationFragment(), false);
                }
                else{
                    loadFrag(new ProfileFragment(), false);
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
            fragmentTransaction.add(R.id.container, fragment);
        }
        else {
            fragmentTransaction.replace(R.id.container, fragment);
        }

        fragmentTransaction.commit();
    }
}