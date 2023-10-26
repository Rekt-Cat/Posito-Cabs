package com.example.positocabs.Views.MainScreen.RiderMain.Fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.positocabs.Models.DriverGeoModel;
import com.example.positocabs.Models.Event.SelectPlaceEvent;
import com.example.positocabs.R;
import com.example.positocabs.Remote.RiderRemote.IGoogleAPI;
import com.example.positocabs.Remote.RiderRemote.RetrofitClient;
import com.example.positocabs.Services.Common;
import com.example.positocabs.Utils.UserUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.ui.IconGenerator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RequestDriverFragment extends Fragment{

    private LinearLayout layout;
    private GoogleMap mMap;
    private View mMapView;
    private SupportMapFragment mapFragment;

    private SelectPlaceEvent selectPlaceEvent;

    //routes
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private IGoogleAPI iGoogleAPI;
    private Polyline blackPolyline, greyPolyline;
    private PolylineOptions polylineOptions, blackPolylineOption;
    private List<LatLng> polyLineList;
    private LatLng origin,destination;

    private Marker originMarker, destinationMarker;


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
        if (EventBus.getDefault().hasSubscriberForEvent(SelectPlaceEvent.class)) {
            EventBus.getDefault().removeStickyEvent(SelectPlaceEvent.class);

        }
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onSelectedPlaceEvent(SelectPlaceEvent event) {
        selectPlaceEvent = event;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_request_driver2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layout=view.findViewById(R.id.request_layout);

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        mapFragment.onResume();
        mapFragment.onCreate(savedInstanceState);
        mMapView = mapFragment.getView();
        init();


    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            origin=selectPlaceEvent.getOrigin();
            destination=selectPlaceEvent.getDestination();

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectPlaceEvent.getOrigin(), 18f));

                    return true;
                }
            });

            drawPath(selectPlaceEvent);

            View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent())
                    .findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            params.setMargins(0, 0, 0, 250);

            mMap.getUiSettings().setZoomControlsEnabled(true);

            try {
                boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.uber_maps_style));
                if (!success) {
                    Log.d("cannotSetStyleError ", "Cannot set the resource");
                }
            } catch (Resources.NotFoundException e) {
                Log.d("cannotSetStyleError", e.getMessage());
            }
        }
    };

    private void drawPath(SelectPlaceEvent selectPlaceEvent) {

        origin=selectPlaceEvent.getOrigin();
        Log.d("ayaya", "The origin is : "+origin);
        compositeDisposable.add(iGoogleAPI.getDirections("driving",
                        "less_driving",
                        selectPlaceEvent.getOriginString(), selectPlaceEvent.getDestinationString(),
                        getContext().getString(R.string.REAL_API_KEY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(returnResult -> {
                    Log.d("apiReturn", "" + returnResult.toString());
                    try {
                        JSONObject jsonObject = new JSONObject(returnResult);
                        JSONArray jsonArray = jsonObject.getJSONArray("routes");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject route = jsonArray.getJSONObject(i);
                            JSONObject poly = route.getJSONObject("overview_polyline");
                            String polyline = poly.getString("points");

                            polyLineList = Common.decodePoly(polyline);


                        }

                        polylineOptions = new PolylineOptions();
                        polylineOptions.color(Color.GRAY);
                        polylineOptions.width(12);
                        polylineOptions.startCap(new SquareCap());
                        polylineOptions.jointType(JointType.ROUND);
                        polylineOptions.addAll(polyLineList);
                        greyPolyline = mMap.addPolyline(polylineOptions);

                        blackPolylineOption = new PolylineOptions();
                        blackPolylineOption.color(Color.BLACK);
                        blackPolylineOption.width(12);
                        blackPolylineOption.startCap(new SquareCap());
                        blackPolylineOption.jointType(JointType.ROUND);
                        blackPolylineOption.addAll(polyLineList);
                        blackPolyline = mMap.addPolyline(blackPolylineOption);

                        //Animator
                        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
                        valueAnimator.setDuration(3000);
                        valueAnimator.setInterpolator(new LinearInterpolator());
                        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator) {
                                List<LatLng> points = greyPolyline.getPoints();
                                int percentValue = (int) valueAnimator.getAnimatedValue();
                                int size = points.size();
                                int newPoints = (int) (size * (percentValue / 100.0f));
                                List<LatLng> p = points.subList(0, newPoints);
                                blackPolyline.setPoints(p);

                            }
                        });
                        valueAnimator.start();
                        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                .include(selectPlaceEvent.getOrigin())
                                .include(selectPlaceEvent.getDestination())
                                .build();
                        //add Car Icon
                        JSONObject object = jsonArray.getJSONObject(0);
                        JSONArray legs = object.getJSONArray("legs");
                        JSONObject legObjects = legs.getJSONObject(0);
                        JSONObject time = legObjects.getJSONObject("duration");
                        String duration = time.getString("text");
                        String start_address = legObjects.getString("start_address");
                        String end_address = legObjects.getString("end_address");

                        addOriginMarker(duration, start_address);
                        addDestinationMarker(end_address);

                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 160));
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(mMap.getCameraPosition().zoom - 1));

                    } catch (Exception e) {
                        Log.d("points", ""+e.getMessage());
                        Snackbar.make(requireView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                })
        );

    }

    private void addDestinationMarker(String end_address) {
        View view = getLayoutInflater().inflate(R.layout.destination_info_window, null);
        TextView txt_destination = view.findViewById(R.id.txt_destination);

        txt_destination.setText(Common.formatAddress(end_address));

        IconGenerator generator = new IconGenerator(getContext());
        generator.setContentView(view);
        generator.setBackground(new ColorDrawable(Color.TRANSPARENT));
        Bitmap icon = generator.makeIcon();

        destinationMarker= mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(icon))
                .position(selectPlaceEvent.getDestination()));


    }

    private void addOriginMarker(String duration, String start_address) {
        View view = getLayoutInflater().inflate(R.layout.origin_info_window, null);

        TextView txt_time = (TextView) view.findViewById(R.id.text_time);
        TextView txt_origin = (TextView) view.findViewById(R.id.txt_origin);
        txt_time.setText(Common.formatDuration(duration));
        txt_origin.setText(Common.formatAddress(start_address));

        IconGenerator generator = new IconGenerator(getContext());
        generator.setContentView(view);
        generator.setBackground(new ColorDrawable(Color.TRANSPARENT));
        Bitmap icon = generator.makeIcon();

        originMarker= mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(icon))
                .position(selectPlaceEvent.getOrigin()));

    }


    private void init() {

        iGoogleAPI = RetrofitClient.getInstance().create(IGoogleAPI.class);

    }
    //this is where to work :
    public void sendRequestToDrivers(Context context,int distanceInt, String distanceString){
        Log.d("ayaya2", "Origin 2 : "+ origin);
        Log.d("ayaya2", "destination 2 : "+ destination);

        Log.d("zee", "is in req 2 : "+ distanceInt);
        Log.d("zee", "is in req 2 : "+ distanceString);

        for (Map.Entry<String, DriverGeoModel> mapElement : Common.driverFound.entrySet()) {
            Log.d("zeex", mapElement.getKey());

            String key = mapElement.getKey();
            DriverGeoModel value = mapElement.getValue();

            UserUtils.sendRequest(getContext(),layout,key,value,origin,destination,selectPlaceEvent,distanceInt,distanceString);

        }

    }

    public void ggFF(){
        Toast.makeText(getActivity(), "ok Working", Toast.LENGTH_SHORT).show();
    }
}