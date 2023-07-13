package com.example.positocabs.Views.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
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

    FirebaseAuth auth;
    String verficationId;

    TextInputLayout lPhoneNo;
    TextInputEditText phoneNo;
    AppCompatButton logInBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        //Casting views
        lPhoneNo=findViewById(R.id.phone_no_layout);
        phoneNo=findViewById(R.id.phone_no_edit_text);
        logInBtn=findViewById(R.id.login_btn);
        final ProgressBar progressBar=findViewById(R.id.progress_bar);

        auth=FirebaseAuth.getInstance();

        phoneNo.setText("");

        //Login logic
        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phoneNo_txt=phoneNo.getText().toString();

                if(!phoneNo_txt.trim().isEmpty()){
                    if(phoneNo_txt.trim().length()==10){

                        progressBar.setVisibility(View.VISIBLE);
                        logInBtn.setVisibility(View.INVISIBLE);

                        sendVerificationOtp(phoneNo_txt, progressBar);
                    }
                    else{
                        Toast.makeText(LogInActivity.this, "Invalid Phone Number!", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(LogInActivity.this, "Enter Phone Number!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void sendVerificationOtp(String phoneNo,ProgressBar progressBar){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phoneNo,
                10,
                TimeUnit.SECONDS,
                LogInActivity.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        progressBar.setVisibility(View.INVISIBLE);
                        logInBtn.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        logInBtn.setVisibility(View.VISIBLE);

                        Toast.makeText(LogInActivity.this, "failed!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken token){
                        progressBar.setVisibility(View.INVISIBLE);
                        logInBtn.setVisibility(View.VISIBLE);

                        Intent intent = new Intent(LogInActivity.this, OtpActivity.class);
                        intent.putExtra("phoneNo",phoneNo);
                        intent.putExtra("verificationId",verificationId);
                        startActivity(intent);
                    }
                }

        );

    }
}
