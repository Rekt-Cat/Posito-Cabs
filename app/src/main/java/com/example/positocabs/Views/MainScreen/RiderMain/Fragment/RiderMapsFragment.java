package com.example.positocabs.Views.MainScreen.RiderMain.Fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;


import android.Manifest;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.positocabs.Callback.IFirebaseDriverInfoListener;
import com.example.positocabs.Callback.IFirebaseFailedListener;
import com.example.positocabs.Models.AnimationModel;
import com.example.positocabs.Models.DriverGeoModel;
import com.example.positocabs.Models.DriverInfoModel;
import com.example.positocabs.Models.GeoQueryModel;
import com.example.positocabs.R;
import com.example.positocabs.Remote.IGoogleAPI;
import com.example.positocabs.Remote.RetrofitClient;
import com.example.positocabs.Services.Common;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
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
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RiderMapsFragment extends Fragment implements IFirebaseFailedListener, IFirebaseDriverInfoListener {
    private static final long UPDATE_INTERVAL = 2000; // 2 seconds
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private double distance = 1.0;
    private static final double LIMIT_RANGE = 10.0; //10 km
    private Location previousLocation, currentLocation;

    IFirebaseDriverInfoListener iFirebaseDriverInfoListener;
    IFirebaseFailedListener iFirebaseFailedListener;

    private boolean firstTime = true;
    private String cityName;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private IGoogleAPI iGoogleAPI;
    private List<LatLng> polylineList;
    private Handler handler;
    private int index, next;
    private LatLng start, end;
    private float v;
    private double lat, lng;

    View mMapView;


    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rider_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.rider_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        mapFragment.onResume();
        mapFragment.onCreate(savedInstanceState);

        mMapView=mapFragment.getView();

        init(view);
    }

    private void init(View view) {
        iGoogleAPI = RetrofitClient.getInstance().create(IGoogleAPI.class);


        iFirebaseFailedListener = this;
        iFirebaseDriverInfoListener = this;

        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setIntervalMillis(UPDATE_INTERVAL)
                .setMinUpdateIntervalMillis(UPDATE_INTERVAL)
                .setWaitForAccurateLocation(false)
                .setMaxUpdateDelayMillis(3000)
                .setMinUpdateDistanceMeters(10f)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    LatLng newPosition = new LatLng(locationResult.getLastLocation().getLatitude(),
                            locationResult.getLastLocation().getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPosition, 18f));

                    //if user has changed location, cal and load driver again

                    if (firstTime) {
                        previousLocation = currentLocation = locationResult.getLastLocation();
                        firstTime = false;
                    } else {
                        previousLocation = currentLocation;
                        currentLocation = locationResult.getLastLocation();
                    }
                    if (previousLocation.distanceTo(currentLocation) / 1000 <= LIMIT_RANGE) {
                        loadAvailableDrivers(view);
                    } else {
                        //do nothing!
                    }
                }
            }
        };
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        loadAvailableDrivers(view);

    }

    private void loadAvailableDrivers(View view) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(view, getString(R.string.PERMISSION_REQUIRED), Snackbar.LENGTH_LONG).show();
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(requireView(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                //load all driver in city
                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                List<Address> addressList;
                try {
                    addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    cityName = addressList.get(0).getLocality();

                    //query
                    DatabaseReference driver_location_ref = FirebaseDatabase.getInstance().getReference(Common.DRIVER_LOCATION_REFERENCE)
                            .child(cityName);
                    GeoFire gf = new GeoFire(driver_location_ref);
                    GeoQuery geoQuery = gf.queryAtLocation(new GeoLocation(location.getLatitude(), location.getLongitude()), distance);
                    geoQuery.removeAllListeners();

                    geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                        @Override
                        public void onKeyEntered(String key, GeoLocation location) {
                            Common.driveFound.add(new DriverGeoModel(key, location));

                        }

                        @Override
                        public void onKeyExited(String key) {

                        }

                        @Override
                        public void onKeyMoved(String key, GeoLocation location) {

                        }

                        @Override
                        public void onGeoQueryReady() {
                            if (distance <= LIMIT_RANGE) {
                                distance++;
                                loadAvailableDrivers(view); //continue search in new distance

                            } else {
                                distance = 1.0;
                                addDriverMarker(view);
                            }
                        }

                        @Override
                        public void onGeoQueryError(DatabaseError error) {
                            Snackbar.make(mapFragment.getView(), error.getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    });

                    driver_location_ref.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            GeoQueryModel geoQueryModel = snapshot.getValue(GeoQueryModel.class);
                            GeoLocation geoLocation = new GeoLocation(geoQueryModel.getL().get(0), geoQueryModel.getL().get(1));
                            DriverGeoModel driverGeoModel = new DriverGeoModel(snapshot.getKey(), geoLocation);
                            Location newDriverLocation = new Location("");
                            newDriverLocation.setLatitude(geoLocation.latitude);
                            newDriverLocation.setLongitude(geoLocation.longitude);
                            float newDistance = location.distanceTo(newDriverLocation) / 1000;
                            if (newDistance <= LIMIT_RANGE) {
                                findDriverByKey(driverGeoModel,view);
                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } catch (IOException e) {
                    Log.d("hasee", "" + e.getMessage());
                    Snackbar.make(view, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void addDriverMarker(View view) {
        if (Common.driveFound.size() > 0) {

            Observable.fromIterable(Common.driveFound).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(driverGeoModel -> {
                        //on next
                        findDriverByKey(driverGeoModel,view);

                    }, throwable -> {
                        Snackbar.make(requireView(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }, () -> {
                    });
        } else {
            Snackbar.make(view, getString(R.string.Driver_Not_Found), Snackbar.LENGTH_LONG).show();
        }
    }

    private void findDriverByKey(DriverGeoModel driverGeoModel,View view) {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(driverGeoModel.getKey()).child("DriverId")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            driverGeoModel.setDriverInfoModel(snapshot.getValue(DriverInfoModel.class));
                            iFirebaseDriverInfoListener.onDriverInfoLoadSuccess(driverGeoModel,view);
                        } else {
                            iFirebaseFailedListener.onFirebaseLoadFailed(getString(R.string.not_found_key) + driverGeoModel.getKey());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        iFirebaseFailedListener.onFirebaseLoadFailed(error.getMessage());
                    }
                });
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            Dexter.withContext(getContext())
                    .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            mMap.setMyLocationEnabled(true);
                            mMap.getUiSettings().setMyLocationButtonEnabled(true);
                            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                                @Override
                                public boolean onMyLocationButtonClick() {
                                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                            ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        return false;
                                    }
                                    fusedLocationProviderClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Snackbar.make(requireView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
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

                            View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent())
                                    .findViewById(Integer.parseInt("2"));
                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                            params.setMargins(0, 0, 0, 250);

                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                            Snackbar.make(requireView(), permissionDeniedResponse.getPermissionName() + " need enable.", Snackbar.LENGTH_LONG).show();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                        }
                    }).check();
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

    @Override
    public void onDestroy() {
        Log.d("desss", "called!");
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        loadAvailableDrivers(requireView());
        super.onResume();
    }

    @Override
    public void onFirebaseLoadFailed(String message) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onDriverInfoLoadSuccess(DriverGeoModel driverGeoModel,View view) {


        //if already have marker with this key, do not set it again.
        if (!Common.markerList.containsKey(driverGeoModel.getKey())) {

            Common.markerList.put(driverGeoModel.getKey(), mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(driverGeoModel.getGeoLocation().latitude,
                            driverGeoModel.getGeoLocation().longitude))
                    .flat(true)
                    .title(Common.buildName(driverGeoModel.getDriverInfoModel().getName(),
                            driverGeoModel.getDriverInfoModel().getGender()))
                    .snippet(String.valueOf(driverGeoModel.getDriverInfoModel().getRating()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car))));


            if (!TextUtils.isEmpty(cityName)) {

                DatabaseReference driverLocation = FirebaseDatabase.getInstance()
                        .getReference(Common.DRIVER_LOCATION_REFERENCE)
                        .child(cityName)
                        .child(driverGeoModel.getKey());

                driverLocation.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.hasChildren()) {

                            if (Common.markerList.get(driverGeoModel.getKey()) != null) {

                                Common.markerList.get(driverGeoModel.getKey()).remove(); // marker remove
                            }
                            Common.markerList.remove(driverGeoModel.getKey()); //remove marker info from hash map
                            Common.driverLocationSubscribe.remove(driverGeoModel.getKey()); //remove driver info
                            driverLocation.removeEventListener(this); //remove event listener

                        }
                        else {
                            if (Common.markerList.get(driverGeoModel.getKey()) != null) {
                                GeoQueryModel geoQueryModel = snapshot.getValue(GeoQueryModel.class);
                                AnimationModel animationModel = new AnimationModel(false, geoQueryModel);

                                if (Common.driverLocationSubscribe.get(driverGeoModel.getKey()) != null) {
                                    Marker currentMarker = Common.markerList.get(driverGeoModel.getKey());
                                    AnimationModel oldPosition = Common.driverLocationSubscribe.get(driverGeoModel.getKey());

                                    String from = new StringBuilder()
                                            .append(oldPosition.getGeoQueryModel().getL().get(0))
                                            .append(",")
                                            .append(oldPosition.getGeoQueryModel().getL().get(1))
                                            .toString();
                                    String to = new StringBuilder()
                                            .append(animationModel.getGeoQueryModel().getL().get(0))
                                            .append(",")
                                            .append(animationModel.getGeoQueryModel().getL().get(1))
                                            .toString();
                                    moveMarkerAnimation(driverGeoModel.getKey(), animationModel, currentMarker, from, to,view);

                                } else {
                                    Common.driverLocationSubscribe.put(driverGeoModel.getKey(), animationModel);

                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Snackbar.make(requireView(), error.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    private void moveMarkerAnimation(String key, AnimationModel animationModel, Marker currentMarker, String from, String to,View view) {
        polylineList=new ArrayList<>();
        if (!animationModel.isRun()) {
            compositeDisposable.add(iGoogleAPI.getDirections("driving",
                            "less_driving",
                            from, to,
                            view.getContext().getString(R.string.REAL_API_KEY))
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

                                polylineList = Common.decodePoly(polyline);


                            }

                            handler = new Handler();
                            index = -1;
                            next = 1;
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    if (polylineList.size() > 1) {
                                        if (index < polylineList.size() - 2) {
                                            index++;
                                            next = index + 1;
                                            start = polylineList.get(index);
                                            end = polylineList.get(next);

                                        }
                                        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 1);
                                        valueAnimator.setDuration(3000);
                                        valueAnimator.setInterpolator(new LinearInterpolator());
                                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                            @Override
                                            public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator) {
                                                v = valueAnimator.getAnimatedFraction();
                                                lat = v * end.latitude + (1 - v) * start.latitude;
                                                lng = v * end.longitude + (1 - v) * start.longitude;
                                                LatLng newPos = new LatLng(lat, lng);
                                                currentMarker.setPosition(newPos);
                                                currentMarker.setAnchor(0.5f, 0.5f);
                                                currentMarker.setRotation(Common.getBearing(start, newPos));
                                            }
                                        });
                                        valueAnimator.start();
                                        if (index < polylineList.size() - 2) {
                                            handler.postDelayed(this, 1500);

                                        } else if (index < polylineList.size() - 1) {
                                            animationModel.setRun(false);
                                            Common.driverLocationSubscribe.put(key, animationModel);

                                        }
                                    }
                                }
                            };

                            handler.postDelayed(runnable, 1500);


                        } catch (Exception e) {
                            Snackbar.make(requireView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    })
            );
        }
    }
}