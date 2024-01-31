package com.example.positocabs.Views.MainScreen.DriverMain.Fragment.BottomSheetFrag;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.positocabs.Models.DataModel.Trip;
import com.example.positocabs.Models.DataModel.User;
import com.example.positocabs.R;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class BRiderConfirmedFragment extends Fragment {

    private Trip trip;
    private User user;
    private TextView riderName, riderPrice, riderRating;
    private MaterialButton contactBtn, helpBtn, cancelBtn;
    private CircleImageView riderPfp;

    public BRiderConfirmedFragment(Trip trip) {
        this.trip = trip;
        user = trip.getUser();
    }

    public BRiderConfirmedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_b_rider_confirmed, container, false);

        //casting views
        cancelBtn=view.findViewById(R.id.cancel_btn);
        contactBtn=view.findViewById(R.id.contact_btn);
        helpBtn=view.findViewById(R.id.help_btn);
        riderRating=view.findViewById(R.id.rider_rating);
        riderName=view.findViewById(R.id.rider_name);
        riderPrice=view.findViewById(R.id.ride_price);
        riderPfp=view.findViewById(R.id.rider_img);

        setViewsData();


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFrag(new BRiderCancelFragment());
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

    private void setViewsData(){
        riderName.setText(user.getName());
        riderPrice.setText(String.valueOf(trip.getBooking().getPrice()));

        riderRating.setText(String.valueOf(user.getRating()));

        setPicture(riderPfp, user.getUserPfp());

    }

    private void setPicture(ImageView imageView, String imageUrl){

        Picasso.get()
                .load(imageUrl)
                .error(R.drawable.default_pfp_ico)
                .into(imageView);
    }
}