package com.example.positocabs.Views.MainScreen.RiderMain.Fragment.BottomSheetFrag;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.positocabs.R;
import com.example.positocabs.ViewModel.RideViewModel;

public class BCancelFragment extends Fragment {

    private CardView yesBtn,noBtn;
    private String tripId;
    private RideViewModel rideViewModel;

    public BCancelFragment() {
        // Required empty public constructor
    }

    public BCancelFragment(String tripId) {
        this.tripId = tripId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_b_cancel, container, false);

        //casting views
        yesBtn=view.findViewById(R.id.yes_btn);
        noBtn=view.findViewById(R.id.no_btn);

        //init
        rideViewModel = new ViewModelProvider(this).get(RideViewModel.class);

        //yes
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rideViewModel.cancelTrip(tripId);
                clearTripData();
                replaceFrag(new BHomeFragment());
            }
        });

        //no
        noBtn.setOnClickListener(new View.OnClickListener() {
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

    private void clearTripData(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("rTripPref", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }
}