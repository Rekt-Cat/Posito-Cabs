package com.example.positocabs.Views.Auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

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
                intent.putExtra("userType", 1);
                savingUserType("1");
                startActivity(intent);
            }
        });

        driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(OnBoardingActivity.this, LogInActivity.class);
                intent.putExtra("userType", 2);
                savingUserType("2");
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