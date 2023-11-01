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
import com.example.positocabs.Services.Common;

public class BBookFragment extends Fragment {

    private int opt;
    private TextView ride_type,ride_time,ride_distance,ride_price,ride_desc;
    private AppCompatButton bookRideBtn;
    private ImageView backBtn;
    private String pickupLocation,dropLocation,distanceString,duration;
    private int distanceInt, priceInt;

    private BookRide bBookRide;

    public BBookFragment() {
        // Required empty public constructor
    }

    public BBookFragment(int opt, String dropLocation, String pickupLocation,int distanceInt,String distanceString,String duration,int priceInt) {
        this.opt = opt;
        this.pickupLocation = pickupLocation;
        this.dropLocation = dropLocation;
        this.distanceInt=distanceInt;
        this.distanceString=distanceString;
        this.duration=duration;
        this.priceInt=priceInt;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(getParentFragment() instanceof BookRide){
            bBookRide = (BookRide) getParentFragment();
        }
        else{
            throw new ClassCastException(context.toString()
                    + " must implement MyInterface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_b_book, container, false);

        //casting views
        ride_type=view.findViewById(R.id.ride_type);
        ride_price=view.findViewById(R.id.ride_price);
//        ride_distance=view.findViewById(R.id.ride_distance);
//        ride_time=view.findViewById(R.id.ride_time);
        ride_desc=view.findViewById(R.id.ride_desc);
        bookRideBtn=view.findViewById(R.id.book_ride_btn);
        backBtn=view.findViewById(R.id.back_btn);

        Log.d("zee", "in Book Str: "+distanceString);
        Log.d("zee", "in Book int : "+distanceInt);

        //ride info
        if(opt==1){
            ride_type.setText("Micro");
            ride_desc.setText("Comfortable micro, affordable-price");
        }
        else if(opt==2){
            ride_type.setText("Prime Sedan");
            ride_desc.setText("Comfortable sedan, top-quality");
        }
        else {
            ride_type.setText("Prime SUV");
            ride_desc.setText("Comfortable suv, premium-feel");
        }

        ride_price.setText(String.valueOf(priceInt));

        //book ride btn
        bookRideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bBookRide.bookRideClicked();
                replaceFrag(new BAddressFragment(dropLocation,pickupLocation,
                        distanceInt,distanceString,duration,priceInt));
            }
        });

        //back
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
                Common.driverFound.clear();
                Common.markerList.clear();
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

    public interface BookRide{
        void bookRideClicked();
    }


}
