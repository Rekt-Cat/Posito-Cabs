package com.example.positocabs.Views.Auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.positocabs.R;

public class SignInActivity extends AppCompatActivity {
    Button loginActivity;
    TextView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_screen);
        loginActivity =findViewById(R.id.signIn_signIn);
        register=findViewById(R.id.Register_signIn);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this,SignupActivity.class));
            }
        });
        loginActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(SignInActivity.this,LogInActivity.class));
            }
        });


    }
}