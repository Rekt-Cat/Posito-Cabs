package com.example.positocabs.Views.MainScreen.RiderMain.Fragment.BottomSheetFrag;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.positocabs.R;

public class BLocationFragment extends Fragment {

    private CardView microBtn,sedanBtn,suvBtn;
    private ImageView backBtn;
    private BLocationOpt bLocationOpt;

    public BLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(getParentFragment() instanceof BLocationOpt){
            bLocationOpt = (BLocationOpt) getParentFragment();
        }
        else{
            throw new ClassCastException(context.toString()
                    + " must implement MyInterface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_b_location, container, false);

        //casting views
        microBtn=view.findViewById(R.id.micro_btn);
        sedanBtn=view.findViewById(R.id.sedan_btn);
        suvBtn=view.findViewById(R.id.suv_btn);
        backBtn=view.findViewById(R.id.back_btn);

        microBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bLocationOpt.selectedCar("Micro");
                replaceFrag(new BBookFragment(1));
            }
        });

        sedanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bLocationOpt.selectedCar("Sedan");
                replaceFrag(new BBookFragment(2));
            }
        });

        suvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bLocationOpt.selectedCar("SUV");
                replaceFrag(new BBookFragment(3));
            }
        });

        //back
        backBtn.setOnClickListener(new View.OnClickListener() {
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

    public interface BLocationOpt{
        void selectedCar(String str);
    }
}