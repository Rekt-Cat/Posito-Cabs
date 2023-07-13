package com.example.positocabs.Views.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.positocabs.R;
import com.example.positocabs.ViewModel.AuthViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SignupActivity extends AppCompatActivity {

    FirebaseAuth auth;
    String verificationId;

    TextInputLayout lPhoneNo;
    TextInputEditText phoneNo;
    ImageView backBtn, fbBtn, googleBtn;
    TextView signInHyperlink;
    AppCompatButton signUpBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Casting views
        lPhoneNo = findViewById(R.id.phone_no_signUp);
        phoneNo = findViewById(R.id.phone_no_edit_text);
        signUpBtn = findViewById(R.id.signUp_btn);
        signInHyperlink = findViewById(R.id.signIn_hyperlink);
        final ProgressBar progressBar=findViewById(R.id.progress_bar);

        auth = FirebaseAuth.getInstance();

        //SignUp Logic
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phoneNo_txt = phoneNo.getText().toString();

                if(!phoneNo_txt.trim().isEmpty()){
                    if (phoneNo_txt.trim().length() == 10){

                        progressBar.setVisibility(View.VISIBLE);
                        signUpBtn.setVisibility(View.INVISIBLE);

                        sendVerificationOtp(phoneNo_txt, progressBar);

                    }
                    else {
                        Toast.makeText(SignupActivity.this, "Invalid phone number", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(SignupActivity.this, "Enter phone number", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //signin logic
        signInHyperlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this,LogInActivity.class);
                startActivity(intent);
            }
        });

    }

    private void sendVerificationOtp(String phoneNo,ProgressBar progressBar){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+phoneNo,
                60,
                TimeUnit.SECONDS,
                SignupActivity.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        progressBar.setVisibility(View.INVISIBLE);
                        signUpBtn.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        signUpBtn.setVisibility(View.VISIBLE);

                        Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        progressBar.setVisibility(View.INVISIBLE);
                        signUpBtn.setVisibility(View.VISIBLE);

                        Intent intent = new Intent(SignupActivity.this, OtpActivity.class);
                        intent.putExtra("phoneNo",phoneNo);
                        intent.putExtra("verificationId",verificationId);
                        startActivity(intent);
                    }
                }
        );

    }
}