package com.example.positocabs.Views.MainScreen.RiderMain.Fragment.BottomSheetFrag;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
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
    private String pickupLocation,dropLocation,distanceString,duration;

    private int distanceInt, priceInt;

    private FragmentCommunication communication;

    public BAddressFragment(String dropLocation, String pickupLocation,int distanceInt,String distanceString,String duration,int priceInt){
        this.dropLocation=dropLocation;
        this.pickupLocation=pickupLocation;
        this.distanceInt=distanceInt;
        this.distanceString=distanceString;
        this.duration=duration;
        this.priceInt=priceInt;
    }

    public BAddressFragment() {
        // Required empty public constructor
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(getParentFragment() instanceof FragmentCommunication){
            communication = (FragmentCommunication) getParentFragment();
        }
        else{
            throw new ClassCastException(context.toString()
                    + " must implement Interface");
        }
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

        Log.d("zee", "in Book Str: "+distanceString);
        Log.d("zee", "in Book int : "+distanceInt);
        Log.d("zee", "in Book str : "+duration);

        //init
        dropLocationText.setText(dropLocation);
        pickupLocationText.setText(pickupLocation);

        //confirm ride btn logic
        confirmRideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communication.invokeFunction(distanceInt,distanceString,duration,priceInt);
                replaceFrag(new BDriverFragment());
            }
        });

        //back
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communication.popFrag();
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

    public interface FragmentCommunication {
        void invokeFunction(int distanceInt,String distanceString,String duration,int price);

        void popFrag();
    }

}