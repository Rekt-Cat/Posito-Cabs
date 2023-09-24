package com.example.positocabs.Views.Auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.positocabs.Callback.LoginCallback;
import com.example.positocabs.Models.DataModel.DriverDoc;
import com.example.positocabs.R;
import com.example.positocabs.ViewModel.AuthViewModel;
import com.example.positocabs.ViewModel.SaveUserDataViewModel;
import com.example.positocabs.Views.MainScreen.DriverMain.DriverMainActivity;
import com.example.positocabs.Views.MainScreen.RiderMain.RiderMainActivity;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    //digits
    private EditText d1,d2,d3,d4,d5,d6;
    private AppCompatButton verifyBtn;
    private TextView phoneNoText, resendOtp;
    private ImageView backBtn;
    private String phoneNo, getOtpBackend,uId;
    private FirebaseAuth firebaseAuth;
    private AuthViewModel authViewModel;
    private SaveUserDataViewModel saveUserDataViewModel;
    private ProgressBar progressBar;


    private CountDownTimer timer;
    private String userType;
    private FirebaseUser user;
    private MutableLiveData<FirebaseUser> userData;
    private MutableLiveData<Boolean> loggedStatus;

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
        resendOtp=findViewById(R.id.resend_otp);
        backBtn=findViewById(R.id.back_btn);
        progressBar=findViewById(R.id.progress_bar);

        //getting userType intent
        String userType = getIntent().getStringExtra("userType");

        firebaseAuth = FirebaseAuth.getInstance();
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        //saveUserDataViewModel = new ViewModelProvider(this).get(SaveUserDataViewModel.class);

        //getting intent
        phoneNoText.setText(String.format(
                "+91-%s", getIntent().getStringExtra("phoneNo")
        ));

        phoneNo = getIntent().getStringExtra("phoneNo");
        getOtpBackend = getIntent().getStringExtra("verificationId");

        resendTimer();

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
                        showBtnProgressBar();

                        authViewModel.logginInUser(userType, phoneNo, getOtpBackend, enteredOtp, new LoginCallback() {
                            @Override
                            public void onLoginCompleted(boolean bool) {
                                // Use the isUserRegistered value here, as it will be available after the login process is complete
                                if(bool){
                                    if(userType.equals("Rider")){
                                        hideBtnProgressBar();
                                        Toast.makeText(OtpActivity.this, "RiderMain", Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(OtpActivity.this, RiderMainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.putExtra("userType", userType);
                                        startActivity(intent);
                                    }
                                    else{
                                        FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                                                .child(firebaseAuth.getCurrentUser().getUid())
                                                .child("DriverId")
                                                .child("Docs")
                                                .child("carType");

                                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String str = snapshot.getValue().toString();
                                                writeCarType(str);

                                                hideBtnProgressBar();
                                                Toast.makeText(OtpActivity.this, "DriverMain", Toast.LENGTH_SHORT).show();
                                                Intent intent=new Intent(OtpActivity.this, DriverMainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.putExtra("userType", userType);
                                                startActivity(intent);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                    }
                                }
                                else {
                                    hideBtnProgressBar();
                                    Toast.makeText(OtpActivity.this, "MakeProfile", Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(OtpActivity.this, MakeProfileActivity.class);
                                    intent.putExtra("userType", userType);
                                    startActivity(intent);
                                }
                            }
                        });

                    }
                    else {

                        Toast.makeText(OtpActivity.this, "otpBackend null!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(OtpActivity.this, "Enter all number!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //resend logic
        resendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(OtpActivity.this, "resend!", Toast.LENGTH_SHORT).show();
                resendOtp(phoneNo);
            }
        });

        //back btn logic
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

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
                if (editable.length() == 0) {
                    d2.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                                d2.setOnKeyListener(null);
                                d2.clearFocus();
                                d1.requestFocus();
                            }
                            return false;
                        }
                    });
                } else {
                    d2.setOnKeyListener(null);
                }
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
                if (editable.length() == 0) {
                    d3.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                                d3.setOnKeyListener(null);
                                d3.clearFocus();
                                d2.requestFocus();
                            }
                            return false;
                        }
                    });
                } else {
                    d3.setOnKeyListener(null);
                }
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
                if (editable.length() == 0) {
                    d4.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                                d4.setOnKeyListener(null);
                                d4.clearFocus();
                                d3.requestFocus();
                            }
                            return false;
                        }
                    });
                } else {
                    d4.setOnKeyListener(null);
                }
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
                if (editable.length() == 0) {
                    d5.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                                d5.setOnKeyListener(null);
                                d5.clearFocus();
                                d4.requestFocus();
                            }
                            return false;
                        }
                    });
                } else {
                    d5.setOnKeyListener(null);
                }
            }
        });

        d6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    d6.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                                d6.setOnKeyListener(null);
                                d6.clearFocus();
                                d5.requestFocus();
                            }
                            return false;
                        }
                    });
                } else {
                    d6.setOnKeyListener(null);
                }
            }
        });
    }

    private void resendOtp(String phoneNo){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phoneNo,
                60,
                TimeUnit.SECONDS,
                OtpActivity.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                    }

                    @Override
                    public void onCodeSent(@NonNull String resendVerificationId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken token){

                        getOtpBackend = resendVerificationId;
                        Toast.makeText(OtpActivity.this, "OTP sent!", Toast.LENGTH_SHORT).show();
                    }
                }

        );

        //starting a timer until next otp
        resendTimer();

    }

    private void resendTimer(){
        timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                resendOtp.setClickable(false);
                resendOtp.setTextColor(getResources().getColor(R.color.grey_txt));
                resendOtp.setText("Resend OTP in "+ l/1000 +" seconds");
            }

            @Override
            public void onFinish() {
                resendOtp.setClickable(true);
                resendOtp.setTextColor(getResources().getColor(R.color.skyblue_hyperlink));
                resendOtp.setText("Resend OTP");
            }
        };

        timer.start();
    }

    private void showBtnProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
        verifyBtn.setVisibility(View.GONE);
    }

    private void hideBtnProgressBar(){
        progressBar.setVisibility(View.GONE);
        verifyBtn.setVisibility(View.VISIBLE);
    }

    private void writeCarType(String cType){
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("carType", cType);
        editor.apply();
    }
}
