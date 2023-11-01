package com.example.positocabs.Views.MainScreen.DriverMain.Fragment.BottomSheetFrag;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.positocabs.R;

public class BRiderConfirmationFragment extends Fragment {

    public BRiderConfirmationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_b_rider_confirmation, container, false);


        return view;
    }
}