package com.example.positocabs.Views.Auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.positocabs.R;

public class OnBoardingActivity extends AppCompatActivity {

    private LinearLayout rider,driver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        //casting views

        rider=findViewById(R.id.rider_card);
        driver=findViewById(R.id.driver_card);

        rider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(OnBoardingActivity.this, LogInActivity.class);
                intent.putExtra("userType", "Rider");
                savingUserType("Rider");
                startActivity(intent);
            }
        });

        driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(OnBoardingActivity.this, LogInActivity.class);
                intent.putExtra("userType", "Driver");
                savingUserType("Driver");
                startActivity(intent);
            }
        });


    }


    public void savingUserType(String userType) {

        // Save the user's choice in SharedPreferences
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userType", userType);
        editor.apply();
    }

}