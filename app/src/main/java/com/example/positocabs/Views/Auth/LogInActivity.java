package com.example.positocabs.Views.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.positocabs.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LogInActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private String verficationId;

    private TextInputLayout lPhoneNo;
    private TextInputEditText phoneNo;
    private AppCompatButton logInBtn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        //Casting views
        lPhoneNo=findViewById(R.id.phone_no_layout);
        phoneNo=findViewById(R.id.phone_no_edit_text);
        logInBtn=findViewById(R.id.login_btn);
        progressBar=findViewById(R.id.progress_bar);

        auth=FirebaseAuth.getInstance();

        phoneNo.setText("");


        //Login logic
        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phoneNo_txt=phoneNo.getText().toString();

                if(!phoneNo_txt.trim().isEmpty()){
                    if(phoneNo_txt.trim().length()==10){

                        showBtnProgressBar();

                        sendVerificationOtp(phoneNo_txt, progressBar);
                    }
                    else{
                        lPhoneNo.setError("Phone number must be of 10 digit");
                    }
                }
                else{
                    lPhoneNo.setError("Enter Phone Number");
                }

            }
        });
    }

    private void sendVerificationOtp(String phoneNo,ProgressBar progressBar){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phoneNo,
                60,
                TimeUnit.SECONDS,
                LogInActivity.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        hideBtnProgressBar();
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        hideBtnProgressBar();
                        Log.d("loginError", e.getMessage());
                        Toast.makeText(LogInActivity.this, "failed!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken token){
                        hideBtnProgressBar();

                        //getting userType intent
                        Intent xintent = getIntent();
                        String userType = xintent.getStringExtra("userType");

                        Intent intent = new Intent(LogInActivity.this, OtpActivity.class);
                        intent.putExtra("userType", userType);
                        intent.putExtra("phoneNo",phoneNo);
                        intent.putExtra("verificationId",verificationId);
                        startActivity(intent);
                    }
                }

        );

    }

    private void showBtnProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
        logInBtn.setVisibility(View.GONE);
    }

    private void hideBtnProgressBar(){
        progressBar.setVisibility(View.GONE);
        logInBtn.setVisibility(View.VISIBLE);
    }
}
