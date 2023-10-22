package com.example.positocabs.Views.MainScreen.DriverMain.Fragment.BotoomSheetFrag;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.positocabs.Models.DataModel.Booking;
import com.example.positocabs.R;

public class BRiderRequestFragment extends Fragment {

    private TextView pickUpLocation,dropLocation,distance,price;
    private AppCompatButton lowPriceBtn,mediumPriceBtn,highPriceBtn,confirmBtn;
    private ProgressBar lowPriceProgressBar,mediumPriceProgressBar,highPriceProgressBar
            ,confirmBtnProgressBar, requestProgressBar;

    private Booking booking;

    public BRiderRequestFragment(Booking booking) {
        this.booking = booking;
    }

    public BRiderRequestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_b_rider_request, container, false);

        //casting views
        pickUpLocation=view.findViewById(R.id.pickup_location);
        dropLocation=view.findViewById(R.id.drop_location);
        distance=view.findViewById(R.id.distance);
        price=view.findViewById(R.id.base_price);
        lowPriceBtn=view.findViewById(R.id.low_price);
        mediumPriceBtn=view.findViewById(R.id.medium_price);
        highPriceBtn=view.findViewById(R.id.high_price);
        lowPriceProgressBar=view.findViewById(R.id.low_price_progress_bar);
        mediumPriceProgressBar=view.findViewById(R.id.medium_price_progress_bar);
        highPriceProgressBar=view.findViewById(R.id.high_price_progress_bar);
        confirmBtn=view.findViewById(R.id.confirm_btn);
        confirmBtnProgressBar=view.findViewById(R.id.confirm_progress_bar);

        //init
        setData();

        return view;
    }

    private void setData(){
        if(booking!=null){
            int basePrice= booking.getPrice();

            dropLocation.setText(booking.getDropLocation());
            pickUpLocation.setText(booking.getPickUpLocation());
            distance.setText(String.valueOf(booking.getDistance() + " km"));
            price.setText(String.valueOf(basePrice));

            lowPriceBtn.setText(String.valueOf(basePrice - ( (basePrice*25) / 100 )));
            mediumPriceBtn.setText(String.valueOf(basePrice + ( (basePrice*15) / 100 )));
            highPriceBtn.setText(String.valueOf(basePrice + ( (basePrice*35) / 100 )));
        }
    }
}