package com.example.positocabs.Views.Auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.positocabs.R;
import com.example.positocabs.ViewModel.AuthViewModel;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {
    AuthViewModel authViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);



        authViewModel= ViewModelProviders.of(this).get(AuthViewModel.class);
        authViewModel.register("ayushdhyani.12@gmail.com","bhaisex");

        authViewModel.getUserData().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if(firebaseUser!=null){
                    Log.d("lol", "inside your mum registerUser: "+firebaseUser.getUid());
                    Toast.makeText(SignupActivity.this, "YOLOLOL", Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.d("lol", "inside your mum registerUser: ");

                }
            }
        });


    }
}