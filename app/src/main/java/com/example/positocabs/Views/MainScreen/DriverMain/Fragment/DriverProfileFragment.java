package com.example.positocabs.Views.MainScreen.DriverMain.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.positocabs.R;
import com.example.positocabs.ViewModel.AuthViewModel;
import com.example.positocabs.ViewModel.SaveUserDataViewModel;
import com.example.positocabs.Views.Auth.OnBoardingActivity;
import com.example.positocabs.Views.Profile.EditProfileActivity;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class DriverProfileFragment extends Fragment {

    //Views
    private AppCompatButton editProfile,logOutBtn;
    private CircleImageView userPfp;
    private TextView userName;
    private AlertDialog.Builder builder;
    private AuthViewModel authViewModel;
    private SaveUserDataViewModel saveUserDataViewModel;

    public DriverProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_driver_profile, container, false);
        Activity currentActivity = getActivity();

        //casting views
        userName=view.findViewById(R.id.user_name);
        userPfp=view.findViewById(R.id.user_pfp);
        editProfile=view.findViewById(R.id.edit_ptofile_btn);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        saveUserDataViewModel = new ViewModelProvider(this).get(SaveUserDataViewModel.class);

        //Casting views
        logOutBtn=view.findViewById(R.id.log_out_btn);
        builder=new AlertDialog.Builder(getActivity());

        saveUserDataViewModel.readUserData("Driver").observe(getViewLifecycleOwner(), user -> {

            // Update UI with user data
            userName.setText(user.getName());
            setPicture(userPfp,user.getUserPfp());
        });

        //logOut logic
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                builder.setTitle("Log Out")
                        .setMessage("Do you want to log out?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                authViewModel.signOut();

                                Intent intent = new Intent(getActivity(), OnBoardingActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                currentActivity.startActivity(intent);

                                SharedPreferences preferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("userType", "Logout");
                                editor.apply();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .show();
            }
        });

        //edit profile logic
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                intent.putExtra("userType","Driver");
                startActivity(intent);
            }
        });


        return view;
    }

    private void setPicture(ImageView imageView, String imgUrl){

        Picasso.get()
                .load(imgUrl)
                .error(R.drawable.default_pfp_ico)
                .into(imageView);
    }
}