package com.example.positocabs.Views.MainScreen.RiderMain.Fragment.BottomSheetFrag;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.positocabs.Models.DataModel.User;
import com.example.positocabs.R;
import com.example.positocabs.ViewModel.RideViewModel;
import com.example.positocabs.Views.MainScreen.RiderMain.Fragment.BottomSheetFrag.DriverAdapter.DriversAdapter;

import java.util.List;

public class BDriverFragment extends Fragment {

    private ImageView backBtn;
    private TextView noDriverFound;
    private RecyclerView recyclerView;
    private DriversAdapter driversAdapter;

    private RideViewModel rideViewModel;
    private List<User> driverList;

    public BDriverFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_b_driver, container, false);

        //casting views
        backBtn=view.findViewById(R.id.back_btn);
        noDriverFound=view.findViewById(R.id.no_driver_text);
        recyclerView=view.findViewById(R.id.recycler_view);

        //vertical Recycler Adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        //init
        showNoDriverFound();
        rideViewModel=new ViewModelProvider(this).get(RideViewModel.class);
        rideViewModel.getDrivers().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                Toast.makeText(getActivity(), "fetching...", Toast.LENGTH_SHORT).show();
                driverList = users;
                setAdapter();
            }
        });


        //back
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();

                //skiping the connecting page
                getParentFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void setAdapter(){
        if(driverList!=null){
            hideNoDriverFound();
            driversAdapter = new DriversAdapter(getContext(), driverList, new DriversAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    Toast.makeText(getActivity(), position + " clicked!", Toast.LENGTH_SHORT).show();
                }
            });

            recyclerView.setAdapter(driversAdapter);
        }
        else{
            showNoDriverFound();
        }
    }

    private void showNoDriverFound(){
        noDriverFound.setVisibility(View.VISIBLE);
    }

    private void hideNoDriverFound(){
        noDriverFound.setVisibility(View.GONE);
    }

    private void replaceFrag(Fragment newFragment){
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.container_bottom_sheet, newFragment);
        transaction.addToBackStack(null); // Optional, allows you to navigate back
        transaction.commit();
    }
}