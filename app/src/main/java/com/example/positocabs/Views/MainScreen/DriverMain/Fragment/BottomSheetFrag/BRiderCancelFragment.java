package com.example.positocabs.Views.MainScreen.DriverMain.Fragment.BottomSheetFrag;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.positocabs.R;
import com.example.positocabs.Views.MainScreen.RiderMain.Fragment.BottomSheetFrag.BHomeFragment;
import com.google.android.material.button.MaterialButton;

public class BRiderCancelFragment extends Fragment {

    private CardView yesBtn, noBtn;


    public BRiderCancelFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_b_rider_cancel, container, false);

        //casting views
        yesBtn=view.findViewById(R.id.yes_btn);
        noBtn=view.findViewById(R.id.no_btn);

        //yes
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Ride cancelled", Toast.LENGTH_SHORT).show();
                replaceFrag(new BBlankFragment());
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
}