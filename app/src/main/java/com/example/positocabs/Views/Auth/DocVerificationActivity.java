package com.example.positocabs.Views.Auth;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.positocabs.R;

public class DocVerificationActivity extends AppCompatActivity {

    private ImageView backBtn;
    private LinearLayout dlCard,dlDetails,vehicleInsuranceCard,vehicleInsuranceDetails,
            panCard,panDetails,vehiclePermit,vehiclePermitDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_verification);

        //casting view
        backBtn=findViewById(R.id.back_btn);
        dlCard=findViewById(R.id.dl_card);
        dlDetails=findViewById(R.id.dl_details);
        vehicleInsuranceCard=findViewById(R.id.vehicle_insurance_card);
        vehicleInsuranceDetails=findViewById(R.id.vehicle_insurance_details);
        panCard=findViewById(R.id.pan_card);
        panDetails=findViewById(R.id.pan_details);
        vehiclePermit=findViewById(R.id.vehicle_permit_card);
        vehiclePermitDetails=findViewById(R.id.vehicle_permit_details);

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


    }

    private void expand(View view, LinearLayout card, LinearLayout details){
        int v=(details.getVisibility() == View.GONE)? View.VISIBLE: View.GONE;

        TransitionManager.beginDelayedTransition(card, new AutoTransition());
        details.setVisibility(v);
    }
}