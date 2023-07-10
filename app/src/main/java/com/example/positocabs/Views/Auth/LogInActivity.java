package com.example.positocabs.Views.Auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.positocabs.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LogInActivity extends AppCompatActivity {

    TextInputLayout lEmail,lPassword;
    TextInputEditText email,password;
    TextView registerHyperlink;
    AppCompatButton logInBtn;
    ImageView backBtn,fbBtn,googleBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        //Casting views
        lEmail=findViewById(R.id.email_logIn);
        email=findViewById(R.id.email_edit_text);
        lPassword=findViewById(R.id.password_logIn);
        password=findViewById(R.id.password_edit_text);
        backBtn=findViewById(R.id.back_btn_logIn);
        logInBtn=findViewById(R.id.logIn);
        fbBtn=findViewById(R.id.facebook_logIn);
        googleBtn=findViewById(R.id.google_logIn);
        registerHyperlink=findViewById(R.id.register_logIn);

        email.setText("");
        password.setText("");

        //Login logic
        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email_txt=email.getText().toString();
                String password_txt=password.getText().toString();

                Toast.makeText(LogInActivity.this, email_txt +" : "+ password_txt, Toast.LENGTH_SHORT).show();


            }
        });
    }
}