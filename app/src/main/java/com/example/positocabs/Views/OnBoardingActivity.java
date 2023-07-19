package com.example.positocabs.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.positocabs.R;
import com.example.positocabs.Views.Auth.LogInActivity;
import com.example.positocabs.Views.Auth.MakeProfileActivity;
import com.example.positocabs.Views.Profile.EditProfileActivity;

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
                startActivity(intent);
            }
        });

        driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(OnBoardingActivity.this, LogInActivity.class);
                intent.putExtra("userType", 2);
                startActivity(intent);
            }
        });
    }
}