package com.example.positocabs.Views.MainScreen.DriverMain;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.positocabs.R;
import com.example.positocabs.Utils.UserUtils;
import com.example.positocabs.Views.MainScreen.DriverMain.Fragment.DriverMapsFragment;
import com.example.positocabs.Views.MainScreen.DriverMain.Fragment.DriverNotificationFragment;
import com.example.positocabs.Views.MainScreen.DriverMain.Fragment.DriverProfileFragment;
import com.example.positocabs.Views.SplashScreen.SplashScreen;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.example.positocabs.Views.MainScreen.DriverMain.Fragment.DriverMapsFragment;
import com.example.positocabs.Views.MainScreen.DriverMain.Fragment.DriverNotificationFragment;
import com.example.positocabs.Views.MainScreen.DriverMain.Fragment.DriverProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;

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

        //saving token of the user
        tokenSave(this);
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

}