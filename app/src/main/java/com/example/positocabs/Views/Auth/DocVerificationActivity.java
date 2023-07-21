package com.example.positocabs.Views.Auth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.positocabs.R;

public class DocVerificationActivity extends AppCompatActivity {

    private ImageView backBtn;
    private LinearLayout dlCard,dlDetails,vehicleInsuranceCard,vehicleInsuranceDetails,
            panCard,panDetails,vehiclePermit,vehiclePermitDetails;
    private AppCompatButton dlBtn,vehicleInsuranceBtn,panBtn,vehiclePermitBtn;

    private Uri imageUri;
    String myUrl ="";

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

                Intent photoIntent =new Intent(Intent.ACTION_PICK);
                photoIntent.setType("image/*");
                startActivityForResult(photoIntent, 1);
            }
        });


    }

    private void expand(View view, LinearLayout card, LinearLayout details){
        int v=(details.getVisibility() == View.GONE)? View.VISIBLE: View.GONE;

        TransitionManager.beginDelayedTransition(card, new AutoTransition());
        details.setVisibility(v);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
        } else {
            Log.d("hehe", "onActivityResult: NOO");
        }
    }
}