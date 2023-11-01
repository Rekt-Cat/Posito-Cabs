package com.example.positocabs.Views.MainScreen.DriverMain.Fragment.BottomSheetFrag;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.positocabs.Models.DataModel.Booking;
import com.example.positocabs.Models.Event.DriverRequestReceived;
import com.example.positocabs.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;

public class BRiderRequestFragment extends Fragment {

    private TextView pickUpLocation,dropLocation,distance,price;
    private AppCompatButton lowPriceBtn,mediumPriceBtn,highPriceBtn,confirmBtn;
    private ProgressBar lowPriceProgressBar,mediumPriceProgressBar,highPriceProgressBar
            ,confirmBtnProgressBar, requestProgressBar;

    private Booking booking;
    private DriverRequestReceived event;
    private BRiderRequestMap bRiderRequestMap;

    public BRiderRequestFragment(Booking booking, DriverRequestReceived event) {
        this.booking = booking;
        this.event = event;
    }

    public BRiderRequestFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(getParentFragment() instanceof BRiderRequestMap){
            bRiderRequestMap = (BRiderRequestMap) getParentFragment();
        }
        else{
            throw new ClassCastException(context.toString() + "must implement BRiderRequestMap");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_b_rider_request, container, false);

        //casting views
        pickUpLocation=view.findViewById(R.id.pickup_location);
        dropLocation=view.findViewById(R.id.drop_location);
        distance=view.findViewById(R.id.distance);
        price=view.findViewById(R.id.base_price);
        lowPriceBtn=view.findViewById(R.id.low_price);
        mediumPriceBtn=view.findViewById(R.id.medium_price);
        highPriceBtn=view.findViewById(R.id.high_price);
        lowPriceProgressBar=view.findViewById(R.id.low_price_progress_bar);
        mediumPriceProgressBar=view.findViewById(R.id.medium_price_progress_bar);
        highPriceProgressBar=view.findViewById(R.id.high_price_progress_bar);
        confirmBtn=view.findViewById(R.id.confirm_btn);
        confirmBtnProgressBar=view.findViewById(R.id.confirm_progress_bar);
        requestProgressBar=view.findViewById(R.id.request_progress_bar);

        //init
        setData();

        Observable.interval(100, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(x->{
                    requestProgressBar.setProgress(requestProgressBar.getProgress()+1);

                }).takeUntil(aLong -> aLong==100)//10sec
                .doOnComplete(()->{
                    requestProgressBar.setProgress(0);
                    bRiderRequestMap.MapClear();
                }).subscribe();

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bRiderRequestMap.MapClear();
                bRiderRequestMap.TripConfirmedDBStore(event);
                replaceFrag(new BRiderConfirmationFragment());
            }
        });

        return view;
    }

    private void setData(){

        try {
            convertAddress(event);
        }
        catch (IOException ex){
            Log.d("convertAddress Exception", ex.getMessage().toString());
        }

        if(booking!=null){
            int basePrice= booking.getPrice();
            dropLocation.setText(booking.getDropLocation());
            pickUpLocation.setText(booking.getPickUpLocation());
            distance.setText(String.valueOf(booking.getDistance() + " km"));
            price.setText(String.valueOf(basePrice));

            lowPriceBtn.setText(String.valueOf(basePrice - ( (basePrice*25) / 100 )));
            mediumPriceBtn.setText(String.valueOf(basePrice + ( (basePrice*15) / 100 )));
            highPriceBtn.setText(String.valueOf(basePrice + ( (basePrice*35) / 100 )));
        }
    }

    private void convertAddress(DriverRequestReceived event) throws IOException {

        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addressList;

        addressList = geocoder.getFromLocation(Double.parseDouble(event.getPickupLocation().split(",")[0]),
                Double.parseDouble(event.getPickupLocation().split(",")[1]), 1);


        if (addressList.size() > 0) {
            booking.setPickUpLocation(addressList.get(0).getAddressLine(0));
            Log.d("driverEvent", "pickup :  "+ booking.getPickUpLocation());
            addressList.clear();

        }

        addressList = geocoder.getFromLocation(Double.parseDouble(event.getDropLocation().split(",")[0]),
                Double.parseDouble(event.getDropLocation().split(",")[1]), 1);

        if (addressList.size() > 0) {
            booking.setDropLocation(addressList.get(0).getAddressLine(0));
            Log.d("driverEvent", "drop :  "+ booking.getDropLocation());
            addressList.clear();
        }
    }


    public interface BRiderRequestMap{
        void MapClear();
        void TripConfirmedDBStore(DriverRequestReceived event);
    }

    private void replaceFrag(Fragment newFragment){
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.container_bottom_sheet, newFragment);
        transaction.addToBackStack(null); // Optional, allows you to navigate back
        transaction.commit();
    }
}