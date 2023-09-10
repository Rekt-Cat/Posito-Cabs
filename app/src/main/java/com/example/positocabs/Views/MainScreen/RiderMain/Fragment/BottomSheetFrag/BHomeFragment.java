package com.example.positocabs.Views.MainScreen.RiderMain.Fragment.BottomSheetFrag;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.positocabs.R;

public class BHomeFragment extends Fragment {

    private CardView bottomSheetBtn,rideBtn,rentalBtn,intercityBtn;
    private Fragment fragment;
    private String location;

    public BHomeFragment(String location) {
        this.location = location;
    }

    public BHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_b_home, container, false);

        //casting views
        //bottomSheetBtn=view.findViewById(R.id.bottom_sheet_btn);
        rideBtn=view.findViewById(R.id.ride_btn);
        rentalBtn=view.findViewById(R.id.rental_btn);
        intercityBtn=view.findViewById(R.id.intercity_btn);

        rideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFrag(new BLocationFragment());

            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragment=getChildFragmentManager().findFragmentById(R.id.autoComplete_fragment);



    }

    private void replaceFrag(Fragment newFragment){
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.container_bottom_sheet, newFragment);
        transaction.addToBackStack(null); // Optional, allows you to navigate back
        transaction.commit();
    }


}