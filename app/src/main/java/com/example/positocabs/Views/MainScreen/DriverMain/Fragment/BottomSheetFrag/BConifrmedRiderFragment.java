package com.example.positocabs.Views.MainScreen.DriverMain.Fragment.BottomSheetFrag;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.positocabs.Models.DataModel.Trip;
import com.example.positocabs.R;


public class BConifrmedRiderFragment extends Fragment {

    private Trip trip;
    private TextView riderName, riderPrice, riderPfp, riderRating;

    public BConifrmedRiderFragment(Trip trip) {
        this.trip = trip;
    }

    public BConifrmedRiderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_b_conifrmed_rider, container, false);

        //casting views


        return view;
    }

    private void setViewsData(){


    }
}