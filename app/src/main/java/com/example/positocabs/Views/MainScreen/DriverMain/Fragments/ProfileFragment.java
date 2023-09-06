package com.example.positocabs.Views.MainScreen.DriverMain.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.positocabs.R;
import com.example.positocabs.ViewModel.AuthViewModel;
import com.example.positocabs.ViewModel.SaveUserDataViewModel;
import com.example.positocabs.Views.Auth.OnBoardingActivity;

public class ProfileFragment extends Fragment {

    //Views
    private AppCompatButton logOutBtn;
    private AlertDialog.Builder builder;

    private AuthViewModel authViewModel;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Activity currentActivity = getActivity();

        authViewModel= new ViewModelProvider(this).get(AuthViewModel.class);

        //Casting views
        logOutBtn=view.findViewById(R.id.log_out_btn);
        builder=new AlertDialog.Builder(getActivity());

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


        return view;


    }
}