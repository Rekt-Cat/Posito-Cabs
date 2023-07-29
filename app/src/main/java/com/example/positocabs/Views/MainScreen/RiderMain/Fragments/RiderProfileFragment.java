package com.example.positocabs.Views.MainScreen.RiderMain.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import com.example.positocabs.Views.Auth.OnBoardingActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RiderProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RiderProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //Views
    private AppCompatButton logOutBtn;
    private AlertDialog.Builder builder;

    private AuthViewModel authViewModel;

    public RiderProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RiderProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RiderProfileFragment newInstance(String param1, String param2) {
        RiderProfileFragment fragment = new RiderProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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

                authViewModel.signOut();

                Intent intent = new Intent(getActivity(), OnBoardingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                currentActivity.startActivity(intent);

                SharedPreferences preferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("userType", "Logout");
                editor.apply();
            }
        });



        return view;
    }
}