package com.example.positocabs.Views.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.positocabs.R;
import com.example.positocabs.ViewModel.AuthViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OtpActivity extends AppCompatActivity {

    //digits
    EditText d1,d2,d3,d4,d5,d6;
    AppCompatButton verifyBtn;
    TextView phoneNoText, resendHyperlink;
    String phoneNo, getOtpBackend;
    FirebaseAuth firebaseAuth;
    AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        //Casting views
        d1=findViewById(R.id.otp_01);
        d2=findViewById(R.id.otp_02);
        d3=findViewById(R.id.otp_03);
        d4=findViewById(R.id.otp_04);
        d5=findViewById(R.id.otp_05);
        d6=findViewById(R.id.otp_06);
        phoneNoText=findViewById(R.id.phone_no_txt);
        verifyBtn=findViewById(R.id.verify_otp_btn);
        resendHyperlink=findViewById(R.id.resend_otp_hyperlink);
        final ProgressBar progressBar=findViewById(R.id.progress_bar);

        firebaseAuth = FirebaseAuth.getInstance();
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

//        authViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
//                .getInstance(getApplication())).get(AuthViewModel.class);
//        authViewModel.getUserData().observe(this, new Observer<FirebaseUser>() {
//            @Override
//            public void onChanged(FirebaseUser firebaseUser) {
//                if(firebaseUser != null){
//
//                }
//            }
//        });

        //getting intent
        phoneNoText.setText(String.format(
                "+91-%s", getIntent().getStringExtra("phoneNo")
        ));

        phoneNo = getIntent().getStringExtra("phoneNo");
        getOtpBackend = getIntent().getStringExtra("verificationId");

        //verifying otp logic
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!d1.getText().toString().trim().isEmpty() && !d2.getText().toString().trim().isEmpty()
                        && !d3.getText().toString().trim().isEmpty() && !d4.getText().toString().trim().isEmpty()
                        && !d5.getText().toString().trim().isEmpty() && !d6.getText().toString().trim().isEmpty()) {

                    //User entered otp
                    String enteredOtp = d1.getText().toString() + d2.getText().toString()
                            + d3.getText().toString() + d4.getText().toString()
                            + d5.getText().toString() + d6.getText().toString();

                    //verifying otp
                    if (getOtpBackend != null) {
                        progressBar.setVisibility(View.VISIBLE);
                        verifyBtn.setVisibility(View.INVISIBLE);

                        authViewModel.registerUser(phoneNo, getOtpBackend, enteredOtp,
                                new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            FirebaseUser user = firebaseAuth.getCurrentUser();
                                            Toast.makeText(OtpActivity.this, "OTP verified!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(OtpActivity.this, "InValid OTP!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        progressBar.setVisibility(View.INVISIBLE);
                        verifyBtn.setVisibility(View.VISIBLE);
                    }
                    else {
                        Toast.makeText(OtpActivity.this, "Enter all numbers!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        autoNumberMove();

    }

    private void autoNumberMove(){
        d1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    d2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        d2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    d3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        d3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    d4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        d4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    d5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        d5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    d6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}