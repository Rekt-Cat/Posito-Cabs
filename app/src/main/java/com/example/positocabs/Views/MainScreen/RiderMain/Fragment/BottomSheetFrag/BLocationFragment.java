package com.example.positocabs.Views.MainScreen.RiderMain.Fragment.BottomSheetFrag;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.positocabs.R;
import com.example.positocabs.Remote.RiderRemote.IGoogleAPI;
import com.example.positocabs.Remote.RiderRemote.RetrofitClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BLocationFragment extends Fragment {

    private CardView microBtn, sedanBtn, suvBtn;
    private ImageView backBtn;
    private BLocationOpt bLocationOpt;
    private String dropLocation, pickupLocation;
    public LatLng pickupLocationLatLng, dropLocationLatLng;
    private CompositeDisposable compositeDisposable;

    private IGoogleAPI iGoogleAPI;

    private Handler handler;

    private int distanceInt;
    private String distanceString;

    private ExecutorService service;

    private LinearLayout layout;

    private ProgressBar progressBar;


    public BLocationFragment(String dropLocation, String pickupLocation, LatLng pickupLocationLatLng, LatLng dropLocationLatLng) {
        this.dropLocation = dropLocation;
        this.pickupLocation = pickupLocation;
        this.pickupLocationLatLng = pickupLocationLatLng;
        this.dropLocationLatLng = dropLocationLatLng;
    }

    public BLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (getParentFragment() instanceof BLocationOpt) {
            bLocationOpt = (BLocationOpt) getParentFragment();
        } else {
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
        microBtn = view.findViewById(R.id.micro_btn);
        sedanBtn = view.findViewById(R.id.sedan_btn);
        suvBtn = view.findViewById(R.id.suv_btn);
        backBtn = view.findViewById(R.id.back_btn);

        progressBar=view.findViewById(R.id.location_progress_bar);
        layout=view.findViewById(R.id.locationLayout);

        layout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);






        microBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bLocationOpt.selectedCar("Micro");
                replaceFrag(new BBookFragment(1, dropLocation, pickupLocation, distanceInt, distanceString));
            }
        });


        sedanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bLocationOpt.selectedCar("Sedan");
                replaceFrag(new BBookFragment(2, dropLocation, pickupLocation, distanceInt, distanceString));
            }
        });

        suvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bLocationOpt.selectedCar("SUV");
                replaceFrag(new BBookFragment(3, dropLocation, pickupLocation, distanceInt, distanceString));
            }
        });

        //back
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bLocationOpt.selectedCar(null);
                getParentFragmentManager().popBackStack();
            }
        });


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        compositeDisposable = new CompositeDisposable();
        iGoogleAPI = RetrofitClient.getInstance().create(IGoogleAPI.class);
        getDistance(pickupLocationLatLng, dropLocationLatLng);

        Log.d("zee", "is : " + distanceInt + "  " + distanceString);


    }

    private void replaceFrag(Fragment newFragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.container_bottom_sheet, newFragment);
        transaction.addToBackStack(null); // Optional, allows you to navigate back
        transaction.commit();
    }

    public interface BLocationOpt {
        void selectedCar(String str);
    }

    public void getDistance(LatLng pickupLocationLatLng, LatLng dropLocationLatLng) {
        compositeDisposable.add(iGoogleAPI.getDirections("driving",
                        "less_driving",
                        new StringBuilder().append(pickupLocationLatLng.latitude)
                                .append(",")
                                .append(pickupLocationLatLng.longitude)
                                .toString(),
                        new StringBuilder().append(dropLocationLatLng.latitude)
                                .append(",")
                                .append(dropLocationLatLng.longitude)
                                .toString(),
                        getContext().getString(R.string.REAL_API_KEY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(returnResult -> {
                    progressBar.setVisibility(View.GONE);
                    layout.setVisibility(View.VISIBLE);
                    Log.d("apiReturn", "" + returnResult.toString());
                    JSONObject jsonObject = new JSONObject(returnResult);
                    JSONArray jsonArray = jsonObject.getJSONArray("routes");

                    JSONObject object = jsonArray.getJSONObject(0);
                    JSONArray legs = object.getJSONArray("legs");
                    JSONObject legObjects = legs.getJSONObject(0);

                    JSONObject time = legObjects.getJSONObject("duration");
                    String duration = time.getString("text");

                    JSONObject distanceEstimate = legObjects.getJSONObject("distance");
                    distanceInt = distanceEstimate.getInt("value");
                    distanceString = distanceEstimate.getString("text");
                    set(distanceInt, distanceString);

                    Log.d("estimate", "Estimate time is : " + duration);
                    Log.d("estimate", "Estimate distanceInt is : " + distanceInt);
                    Log.d("estimate", "Estimate distanceString is : " + distanceString);


                }, error -> {
                    progressBar.setVisibility(View.GONE);
                    layout.setVisibility(View.VISIBLE);
                })
        );

    }


    public void set(int distanceInt, String distanceString) {
        this.distanceInt = distanceInt;
        this.distanceString = distanceString;
    }


}