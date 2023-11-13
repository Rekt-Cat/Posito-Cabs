package com.example.positocabs.Views.MainScreen.DriverMain;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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

import com.example.positocabs.Callback.RegisterDriverOnline;
import com.example.positocabs.R;
import com.example.positocabs.Utils.NetworkChangeListener;
import com.example.positocabs.Utils.UserLocationListener;
import com.example.positocabs.Utils.UserUtils;
import com.example.positocabs.Views.MainScreen.DriverMain.Fragment.DriverMapsFragment;
import com.example.positocabs.Views.MainScreen.DriverMain.Fragment.DriverNotificationFragment;
import com.example.positocabs.Views.MainScreen.DriverMain.Fragment.DriverProfileFragment;
import com.example.positocabs.Views.MainScreen.RiderMain.RiderMainActivity;
import com.example.positocabs.Views.SplashScreen.SplashScreen;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.example.positocabs.Views.MainScreen.DriverMain.Fragment.DriverMapsFragment;
import com.example.positocabs.Views.MainScreen.DriverMain.Fragment.DriverNotificationFragment;
import com.example.positocabs.Views.MainScreen.DriverMain.Fragment.DriverProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;

public class DriverMainActivity extends AppCompatActivity implements DriverMapsFragment.BottomSheetListener, RegisterDriverOnline {

    private BottomNavigationView bottomNavigationView;
    private boolean isBottomSheetOpen = false;

    //initializing driver frags
    private DriverMapsFragment driverMapsFragment = new DriverMapsFragment();
    private DriverNotificationFragment driverNotificationFragment = new DriverNotificationFragment();
    private DriverProfileFragment driverProfileFragment = new DriverProfileFragment();
    private UserLocationListener userLocationListener;
    private NetworkChangeListener networkChangeListener;

    private String carType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main);

        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        carType = preferences.getString("carType", "");

        networkChangeListener= new NetworkChangeListener(carType);

        userLocationListener= new UserLocationListener(this, Snackbar.make(findViewById(android.R.id.content),"Location service is not enabled!",Snackbar.LENGTH_INDEFINITE)
                , DriverMainActivity.this,0);
        //REGISTERING BROADCAST FOR USER LOCATION ENABLED OR NOT
        registerReceiver(userLocationListener, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        //REGISTERING BROADCAST FOR USER INTERNET IS ON OR NOT
        registerReceiver(networkChangeListener, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
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

    @Override
    public void onBackPressed() {

        if(!isBottomSheetOpen){
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.driver_container);

            if(currentFragment instanceof DriverMapsFragment){

            }
            else{
                super.onBackPressed();
            }
        }
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
    public void onBottomSheetOpened(boolean bool) {
        isBottomSheetOpen = bool;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(userLocationListener);
    }

    @Override
    public void InitDriverOnlineSystem(String carType) {
        driverMapsFragment.init(carType);
    }
}