package com.example.positocabs.Views.Auth;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import com.example.positocabs.Callback.TaskCallback;
import com.example.positocabs.R;
import com.example.positocabs.ViewModel.SaveUserDataViewModel;
import com.example.positocabs.Views.MainScreen.DriverMain.DriverMainActivity;

public class DocVerificationActivity extends AppCompatActivity {

    private ImageView backBtn;
    private TextView dlFile,vehicleInsuranceFile,vehiclePermitFile,panFile;
    private LinearLayout dlCard,dlDetails,vehicleInsuranceCard,vehicleInsuranceDetails,
            panCard,panDetails,vehiclePermit,vehiclePermitDetails;
    private AppCompatButton dlBtn,vehicleInsuranceBtn,panBtn,vehiclePermitBtn,proceedBtn;
    private ProgressBar progressBar;

    private Uri dlUri,vehichleInsuranceUri,panUri,vehiclePermituri;

    private SaveUserDataViewModel saveUserDataViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_verification);

        //casting view
        backBtn=findViewById(R.id.back_btn);
        dlCard=findViewById(R.id.dl_card);
        dlDetails=findViewById(R.id.dl_details);
        dlBtn=findViewById(R.id.dl_btn);
        vehicleInsuranceCard=findViewById(R.id.vehicle_insurance_card);
        vehicleInsuranceDetails=findViewById(R.id.vehicle_insurance_details);
        vehicleInsuranceBtn=findViewById(R.id.vehicle_insurance_btn);
        panCard=findViewById(R.id.pan_card);
        panDetails=findViewById(R.id.pan_details);
        panBtn=findViewById(R.id.pan_btn);
        vehiclePermit=findViewById(R.id.vehicle_permit_card);
        vehiclePermitDetails=findViewById(R.id.vehicle_permit_details);
        vehiclePermitBtn=findViewById(R.id.vehicle_permit_btn);
        proceedBtn=findViewById(R.id.proceed_btn);
        progressBar=findViewById(R.id.progress_bar);
        dlFile=findViewById(R.id.dl_file);
        vehicleInsuranceFile=findViewById(R.id.vehicle_insurance_file);
        vehiclePermitFile=findViewById(R.id.vehicle_permit_file);
        panFile=findViewById(R.id.pan_file);

        //sadas
        saveUserDataViewModel= new ViewModelProvider(this).get(SaveUserDataViewModel.class);

        //null
        dlUri=null;
        vehichleInsuranceUri=null;
        panUri=null;
        vehiclePermituri=null;


        //Transition
        dlCard.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        vehicleInsuranceCard.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        panCard.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        vehiclePermit.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        //dl
        dlCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expand(view,dlCard,dlDetails);
            }
        });

        //Vehicle Insurance
        vehicleInsuranceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expand(view,vehicleInsuranceCard,vehicleInsuranceDetails);
            }
        });

        //Pan card
        panCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expand(view,panCard,panDetails);
            }
        });

        //Vehicle Permit
        vehiclePermit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expand(view,vehiclePermit,vehiclePermitDetails);
            }
        });

        //dl button
        dlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto(1);
            }
        });
        vehicleInsuranceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto(2);
            }
        });
        panBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto(3);
            }
        });
        vehiclePermitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto(4);
            }
        });

        //proceed Btn
        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dlUri!=null && vehichleInsuranceUri!=null && panUri!=null && vehiclePermituri!=null ){

                    showProceedBtnProgressBar();
                    saveUserDataViewModel.saveDriverDocs(dlUri, vehichleInsuranceUri, panUri, vehiclePermituri, new TaskCallback() {
                        @Override
                        public void onSuccess() {
                            Intent intent = new Intent(DocVerificationActivity.this, DriverMainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            hideProceedBtnProgressBar();
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            hideProceedBtnProgressBar();
                            Toast.makeText(DocVerificationActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                else{
                    Toast.makeText(DocVerificationActivity.this, "Please upload all documents!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //back btn logic
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    private void expand(View view, LinearLayout card, LinearLayout details){
        int v=(details.getVisibility() == View.GONE)? View.VISIBLE: View.GONE;

        TransitionManager.beginDelayedTransition(card, new AutoTransition());
        details.setVisibility(v);
    }

    private void takePhoto(int requestCode){
        Intent photoIntent = new Intent(Intent.ACTION_PICK);
        photoIntent.setType("image/*");
        startActivityForResult(photoIntent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            switch (requestCode){
                case 1:
                    dlUri = selectedImageUri;
                    dlFile.setText(dlUri.toString());
                    break;
                case 2:
                    vehichleInsuranceUri = selectedImageUri;
                    vehicleInsuranceFile.setText(vehichleInsuranceUri.toString());
                    break;
                case 3:
                    panUri = selectedImageUri;
                    panFile.setText(panUri.toString());
                    break;
                case 4:
                    vehiclePermituri = selectedImageUri;
                    vehiclePermitFile.setText(vehiclePermituri.toString());
                    break;
                default:
                    break;
            }
        } else {
            Log.d("hehe", "onActivityResult: NOO");
        }
    }

    private void showProceedBtnProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
        proceedBtn.setVisibility(View.GONE);
    }

    private void hideProceedBtnProgressBar(){
        progressBar.setVisibility(View.GONE);
        proceedBtn.setVisibility(View.VISIBLE);
    }
}