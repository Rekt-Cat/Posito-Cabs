package com.example.positocabs.Views.MainScreen.RiderMain.Fragment.BottomSheetFrag;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.positocabs.Models.DataModel.Driver;
import com.example.positocabs.Models.DataModel.User;
import com.example.positocabs.R;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class BConfirmedFragment extends Fragment {

    private MaterialButton helpBtn,cancelBtn,shareBtn;
    private TextView driverName, driverRating;
    private CircleImageView driverPfp;

    private Driver driver;
    private User user;

    public BConfirmedFragment(Driver driver) {
        // Required empty public constructor
        this.driver = driver;
        user = driver.getUser();
    }

    public BConfirmedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_b_confirmed, container, false);

        //casting views
        shareBtn=view.findViewById(R.id.share_btn);
        helpBtn=view.findViewById(R.id.help_btn);
        cancelBtn=view.findViewById(R.id.cancel_btn);
        driverName=view.findViewById(R.id.driver_name);
        driverRating=view.findViewById(R.id.driver_rating);
        driverPfp=view.findViewById(R.id.driver_img);

        //init
        setData();

        //cancel btn logic
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFrag(new BCancelFragment());
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

    private void setData(){
        driverName.setText(user.getName());
        driverRating.setText(String.valueOf(user.getRating()));

        setPicture(driverPfp, user.getUserPfp());
    }

    private void setPicture(ImageView imageView, String imageUrl){

        Picasso.get()
                .load(imageUrl)
                .error(R.drawable.default_pfp_ico)
                .into(imageView);
    }
}