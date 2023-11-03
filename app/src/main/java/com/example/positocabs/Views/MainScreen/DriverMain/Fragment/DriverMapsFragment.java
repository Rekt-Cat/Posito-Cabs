package com.example.positocabs.Views.MainScreen.DriverMain.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.positocabs.Models.DataModel.Booking;
import com.example.positocabs.Models.DataModel.DriverDoc;
import com.example.positocabs.Models.Event.DriverRequestReceived;
import com.example.positocabs.R;
import com.example.positocabs.Remote.RiderRemote.IGoogleAPI;
import com.example.positocabs.Remote.RiderRemote.RetrofitClient;
import com.example.positocabs.Services.Common;
import com.example.positocabs.ViewModel.RideViewModel;
import com.example.positocabs.ViewModel.SaveUserDataViewModel;
import com.example.positocabs.Views.MainScreen.DriverMain.Fragment.BottomSheetFrag.BRiderRequestFragment;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DriverMapsFragment extends Fragment implements BRiderRequestFragment.BRiderRequestMap {

    private static final String TAG = "lol";
    private static final long UPDATE_INTERVAL = 15000; // 2 seconds
    private static final long MIN_UPDATE_INTERVAL = 10000;
    private boolean isFirstTime = true;
    private DriverDoc driverDoc;
    private SaveUserDataViewModel saveUserDataViewModel;
    private RideViewModel rideViewModel;
    private String carType;

    private LinearLayout requestWindow,requestLayout;
    private ProgressBar requestProgressBar;

    private Handler handler;
    private Runnable runnable;
    private AlertDialog.Builder builder;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    GoogleMap mMap;
    SupportMapFragment mapFragment;

    DatabaseReference onlineRef, currentUserRef, driverLocationRef, riderTripRef;
    GeoFire geoFire;

    private TextView destinationLocation,pickUpLocation;
    private Button confirm;

    private FrameLayout bottomSheet;
    private BottomSheetBehavior<FrameLayout> behavior;
    private BottomSheetListener bottomSheetListener;
    private BRiderRequestFragment bRiderRequestFragment;

    View mMapView;
    private LinearLayout riderRequestLinearLayout;

    //routes
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private IGoogleAPI iGoogleAPI;
    private Polyline blackPolyline, greyPolyline;
    private PolylineOptions polylineOptions, blackPolylineOption;
    private List<LatLng> polyLineList;
    private LatLng origin, destination;
    private DriverRequestReceived accessEvents;

    private String driverLocation;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof BottomSheetListener){
            bottomSheetListener = (BottomSheetListener) context;
        }else {
            throw new ClassCastException(context.toString() + "must implement BottomSheetListener");
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onDriverRequestReceive(DriverRequestReceived event) throws IOException {

        Log.d("ulele", "string and int are and duration and price are: "+event.getDistanceInt()+" "+event.getDistanceString()+" "+
                event.getDuration()+" "+event.getRiderPrice());

        accessEvents=event;
        int distance = Integer.parseInt(event.getDistanceInt().toString());
        int price = Integer.parseInt(event.getRiderPrice().toString());

        Booking booking = new Booking(event.getPickupLocationString(), event.getDropLocationString(), distance, price);
        bRiderRequestFragmentInstance(booking, event);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(requireView(),e.getMessage(),Snackbar.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        driverLocation= "" +
                                location.getLatitude() +
                                "," +
                                location.getLongitude();

                        Log.d("lotion", "is : "+driverLocation);


                        compositeDisposable.add(iGoogleAPI.getDirections("driving",
                                        "less_driving",
                                        new StringBuilder().append(location.getLatitude())
                                                .append(",")
                                                .append(location.getLongitude())
                                                .toString(),
                                        event.getPickupLocation(),
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

                                        LatLng origin = new LatLng(location.getLatitude(),location.getLongitude());
                                        LatLng destination = new LatLng(Double.parseDouble(event.getPickupLocation().split(",")[0]),
                                                Double.parseDouble(event.getPickupLocation().split(",")[1]));
                                        Log.d("loca", "loca is : "+destination);

                                        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                                .include(origin)
                                                .include(destination)
                                                .build();
                                        //add Car Icon
                                        JSONObject object = jsonArray.getJSONObject(0);
                                        JSONArray legs = object.getJSONArray("legs");
                                        JSONObject legObjects = legs.getJSONObject(0);

                                        JSONObject time = legObjects.getJSONObject("duration");
                                        String duration = time.getString("text");

                                        JSONObject distanceEstimate = legObjects.getJSONObject("distance");
                                        String distance = time.getString("text");

                                        Log.d("estimate", "Estimate time is : "+duration);
                                        Log.d("estimate", "Estimate time is : "+distance);
                                        mMap.addMarker(new MarkerOptions().position(destination)
                                                .icon(BitmapDescriptorFactory.defaultMarker())
                                                .title("Pickup Location"));


                                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 160));
                                        mMap.moveCamera(CameraUpdateFactory.zoomTo(mMap.getCameraPosition().zoom - 1));

                                    } catch (Exception e) {
                                        Log.d("points", ""+e.getMessage());
                                        Snackbar.make(requireView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
                                    }
                                })
                        );

                    }
                });
    }

    private void tripConfirmedDBStore(DriverRequestReceived event, Booking booking) {
        SharedPreferences preferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String token = preferences.getString(Common.TOKEN_REFERENCE, "NOT AVAILABLE");

        rideViewModel.createRiderTrip(token, event, booking);
    }


    ValueEventListener onlineValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists() && currentUserRef != null) {
                currentUserRef.onDisconnect().removeValue();
                isFirstTime = true;
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Snackbar.make(requireView(), error.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_driver_maps, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("userIs", "User is" + FirebaseAuth.getInstance().getCurrentUser().getUid());

        //casting views
        confirm=view.findViewById(R.id.confirm_btn);
        requestLayout=view.findViewById(R.id.request_layout);
        requestProgressBar=view.findViewById(R.id.request_progress_bar);
        destinationLocation=view.findViewById(R.id.drop_location);
        bottomSheet=view.findViewById(R.id.bottom_sheet);
        pickUpLocation=view.findViewById(R.id.pickup_location);
        builder=new AlertDialog.Builder(getActivity());


        //riderRequestLinearLayout=view.findViewById(R.id.riderRequestWindow);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.driver_map);
        if(mapFragment!=null) {
            mapFragment.getMapAsync(callback);
        }
        mMapView=mapFragment.getView();

        mapFragment.onResume();
        mapFragment.onCreate(savedInstanceState);

        //getting carType
        SharedPreferences preferences = getActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        carType = preferences.getString("carType", "");
        Log.d("typeCar", "the car is : "+preferences.getString("carType", ""));

        //checkRides
        rideViewModel = new ViewModelProvider(this).get(RideViewModel.class);
        rideViewModel.checkRides().observe(getViewLifecycleOwner(), new Observer<Booking>() {
            @Override
            public void onChanged(Booking booking) {
                showDialogBox();
            }
        });

        bottomSheet.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        init(view,carType);

    }

    private void init(View view,String carType) {

        iGoogleAPI = RetrofitClient.getInstance().create(IGoogleAPI.class);
        //driver docs(cartype)

        onlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected");

        riderTripRef = FirebaseDatabase.getInstance().getReference().child(Common.RIDER_TRIP);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(view, getString(R.string.PERMISSION_REQUIRED), Snackbar.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(getActivity(), carType, Toast.LENGTH_SHORT).show();

        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setIntervalMillis(UPDATE_INTERVAL)
                .setMinUpdateIntervalMillis(MIN_UPDATE_INTERVAL)
                .setWaitForAccurateLocation(false)
                .setMaxUpdateDelayMillis(3000)
                .setMinUpdateDistanceMeters(50f) //50 meters
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {



                        LatLng newPosition = new LatLng(locationResult.getLastLocation().getLatitude(),
                                locationResult.getLastLocation().getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPosition, 18f));

                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        List<Address> addressList;

                        try {
                            addressList = geocoder.getFromLocation(locationResult.getLastLocation().getLatitude(),
                                    locationResult.getLastLocation().getLongitude(), 1);
                            String cityName = addressList.get(0).getLocality();

                            driverLocationRef = FirebaseDatabase.getInstance().getReference(Common.DRIVER_LOCATION_REFERENCE).child(cityName).child(carType);

                            currentUserRef = driverLocationRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            geoFire = new GeoFire(driverLocationRef);

                            geoFire.setLocation(FirebaseAuth.getInstance().getCurrentUser().getUid(), new GeoLocation(locationResult
                                            .getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude()),
                                    (key, error) -> {
                                        if (error != null) {
                                            Snackbar.make(view, error.getMessage(), Snackbar.LENGTH_LONG).show();
                                        } else {
                                            if (isFirstTime) {
                                                Snackbar.make(view, "You're Online!", Snackbar.LENGTH_LONG).show();
                                                isFirstTime = false;
                                            }

                                        }
                                    });
                            registerOnlineSystem();


                        } catch (IOException e) {
                            Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG).show();
                        }

                    }
                }
            }
        };

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(view, getString(R.string.PERMISSION_REQUIRED), Snackbar.LENGTH_LONG).show();
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());


    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            Log.d("mapshehe", "inside");
            mMap = googleMap;
            //googleMap.getUiSettings().setZoomControlsEnabled(true);

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(requireView(), getString(R.string.PERMISSION_REQUIRED), Snackbar.LENGTH_LONG).show();
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return false;
                    }
                    fusedLocationProviderClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 18f));
                        }
                    });
                    return true;

                }
            });
            View locationButton = ((View)mMapView.findViewById(Integer.parseInt("1"))
                    .getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            params.setMargins(0, 0, 0, 50);


            //adding style to the maps
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


    @Override
    public void onDestroy() {
        Log.d("desss", "called!");

        unRegisterOnlineSystem();

        if(EventBus.getDefault().hasSubscriberForEvent(DriverRequestReceived.class)){
            EventBus.getDefault().removeStickyEvent(DriverRequestReceived.class);


        }
        EventBus.getDefault().unregister(this);
        compositeDisposable.clear();

        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        registerOnlineSystem();
    }

    private void registerOnlineSystem() {
        onlineRef.addValueEventListener(onlineValueEventListener);
    }

    private void unRegisterOnlineSystem() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            geoFire.removeLocation(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }
        onlineRef.removeEventListener(onlineValueEventListener);
    }

    private void showRequestCard(){
        requestLayout.setVisibility(View.VISIBLE);
    }

    private void hideRequestCard(){
        requestLayout.setVisibility(View.GONE);
    }


    private void showDialogBox(){
        builder.setTitle("Ride Confirmed")
                .setMessage("Rider is Ready!")
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .show();
    }

    public void openGoogleMaps(DriverRequestReceived event, String driverLocation){
        Uri uri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination="+event.getDropLocation()+"&origin="+driverLocation+"&waypoints="+event.getPickupLocation()+"&travelmode=driving&dir_action=navigate");
        Intent intent =new Intent(Intent.ACTION_VIEW,uri);
        intent.setPackage("com.google.android.apps.maps");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(intent.resolveActivity(getActivity().getPackageManager())!=null){
            startActivity(intent);

        }

    }

    @Override
    public void MapClear() {
        mMap.clear();
    }

    @Override
    public void TripConfirmedDBStore(DriverRequestReceived event, Booking booking) {
        tripConfirmedDBStore(event, booking);
    }

    public interface BottomSheetListener{
        void onBottomSheetOpened(boolean bool);
    }

    public void bRiderRequestFragmentInstance(Booking booking, DriverRequestReceived event){
        if(bRiderRequestFragment == null){

            bRiderRequestFragment = new BRiderRequestFragment(booking, event);

            //setting fragments on bottom sheet
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.container_bottom_sheet, bRiderRequestFragment)
                    .commit();

        }
    }

}