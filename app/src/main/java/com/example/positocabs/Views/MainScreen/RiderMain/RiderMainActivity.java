package com.example.positocabs.Views.MainScreen.RiderMain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.positocabs.R;
import com.example.positocabs.Views.MainScreen.RiderMain.Fragment.RiderMapsFragment;
import com.example.positocabs.Views.MainScreen.RiderMain.Fragment.RiderDiscountFragment;
import com.example.positocabs.Views.MainScreen.RiderMain.Fragment.RiderProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class RiderMainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_main);

        bottomNavigationView=findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if(id==R.id.nav_home){
                    loadFrag(new RiderMapsFragment(), true);
                }
                else if (id==R.id.nav_discount){
                    loadFrag(new RiderDiscountFragment(), false);
                }
                else{
                    loadFrag(new RiderProfileFragment(), false);
                }

                return false;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    public void loadFrag(Fragment fragment, boolean flag){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().addToBackStack(null);
        if(flag){
            fragmentTransaction.add(R.id.container, fragment);
        }
        else {
            fragmentTransaction.replace(R.id.container, fragment);
        }

        fragmentTransaction.commit();
    }
}