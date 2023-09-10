package com.example.positocabs.Views.MainScreen.RiderMain.Fragment.BottomSheetFrag;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.positocabs.R;

public class BHomeFragment extends Fragment {

    private CardView bottomSheetBtn, rideBtn, rentalBtn, intercityBtn;
    private Fragment fragment;
    private String location;
    public BHomeEnd bHomeEnd;

    public BHomeFragment(String location) {
        this.location = location;
    }

    public BHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(getParentFragment() instanceof BHomeEnd){
            bHomeEnd = (BHomeEnd) getParentFragment();
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement MyInterface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_b_home, container, false);

        //casting views
        rideBtn = view.findViewById(R.id.ride_btn);
        rentalBtn = view.findViewById(R.id.rental_btn);
        intercityBtn = view.findViewById(R.id.intercity_btn);

        bHomeEnd.bHomeEndFun(false);

        rideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bHomeEnd.bHomeEndFun(true);

                replaceFrag(new BLocationFragment());
            }
        });

        return view;
    }

    private void replaceFrag(Fragment newFragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.container_bottom_sheet, newFragment);
        transaction.addToBackStack(null); // Optional, allows you to navigate back
        transaction.commit();
    }

    public interface BHomeEnd{
        void bHomeEndFun(boolean bool);
    }
}