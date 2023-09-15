package com.example.positocabs.Views.MainScreen.RiderMain.Fragment.BottomSheetFrag;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.positocabs.R;

public class BBookFragment extends Fragment {

    private int opt;
    private TextView ride_type,ride_time,ride_distance,ride_price,ride_desc;
    private AppCompatButton bookRideBtn;
    private ImageView backBtn;

    private BookRide bBookRide;

    public BBookFragment() {
        // Required empty public constructor
    }

    public BBookFragment(int opt) {
        this.opt = opt;
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
        ride_distance=view.findViewById(R.id.ride_distance);
        ride_price=view.findViewById(R.id.ride_price);
        ride_time=view.findViewById(R.id.ride_time);
        ride_desc=view.findViewById(R.id.ride_desc);
        bookRideBtn=view.findViewById(R.id.book_ride_btn);
        backBtn=view.findViewById(R.id.back_btn);

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

        //book ride btn
        bookRideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bBookRide.bookRideClicked();
                replaceFrag(new BAddressFragment());
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

    public interface BookRide{
        void bookRideClicked();
    }


}
