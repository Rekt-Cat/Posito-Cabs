package com.example.positocabs.Views.MainScreen.RiderMain.Fragment.BottomSheetFrag;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.positocabs.R;

public class BAddressFragment extends Fragment {

    private AppCompatButton confirmRideBtn;
    private ImageView backBtn;
    private TextView pickupLocationText, dropLocationText;
    private String pickupLocation,dropLocation;

    public BAddressFragment(String dropLocation, String pickupLocation){
        this.dropLocation=dropLocation;
        this.pickupLocation=pickupLocation;
    }

    public BAddressFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_b_address, container, false);

        //casting views
        confirmRideBtn=view.findViewById(R.id.confirm_ride_btn);
        backBtn=view.findViewById(R.id.back_btn);
        pickupLocationText=view.findViewById(R.id.pickup_location_text);
        dropLocationText=view.findViewById(R.id.drop_location_text);

        //init
        dropLocationText.setText(dropLocation);
        pickupLocationText.setText(pickupLocation);

        //confirm ride btn logic
        confirmRideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFrag(new BConnectingFragment());
            }
        });

        //back
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void replaceFrag(Fragment newFragment){
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.container_bottom_sheet, newFragment);
        transaction.addToBackStack(null); // Optional, allows you to navigate back
        transaction.commit();
    }
}