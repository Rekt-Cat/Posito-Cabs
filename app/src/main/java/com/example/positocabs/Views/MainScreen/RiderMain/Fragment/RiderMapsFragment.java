package com.example.positocabs.Views.MainScreen.RiderMain.Fragment;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.positocabs.Callback.IFirebaseDriverInfoListener;
import com.example.positocabs.Callback.IFirebaseFailedListener;
import com.example.positocabs.Models.AnimationModel;
import com.example.positocabs.Models.DriverGeoModel;
import com.example.positocabs.Models.DriverInfoModel;
import com.example.positocabs.Models.Event.SelectPlaceEvent;
import com.example.positocabs.Models.GeoQueryModel;
import com.example.positocabs.R;
import com.example.positocabs.Remote.RiderRemote.IGoogleAPI;
import com.example.positocabs.Remote.RiderRemote.RetrofitClient;
import com.example.positocabs.Services.Common;
import com.example.positocabs.Views.MainScreen.RiderMain.Fragment.BottomSheetFrag.BAddressFragment;
import com.example.positocabs.Views.MainScreen.RiderMain.Fragment.BottomSheetFrag.BBookFragment;
import com.example.positocabs.Views.MainScreen.RiderMain.Fragment.BottomSheetFrag.BHomeFragment;
import com.example.positocabs.Views.MainScreen.RiderMain.Fragment.BottomSheetFrag.BLocationFragment;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.Status;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
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

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RiderMapsFragment extends Fragment implements IFirebaseFailedListener, IFirebaseDriverInfoListener, BHomeFragment.BHomeEnd, BLocationFragment.BLocationOpt, BBookFragment.BookRide, BAddressFragment.FragmentCommunication {
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
    private BHomeFragment bHomeFragment;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private IGoogleAPI iGoogleAPI;
    private String carType;

    private View mMapView;
    private CardView dropLocationBtn, pickupLocationBtn;
    private FrameLayout bottomSheet;
    private LinearLayout fragLayout, pickupLocationLayout;
    private BottomSheetListener bottomSheetListener;
    private TextView dropLocationText, pickupLocationText;
    private ImageView dropClearBtn, pickupClearBtn;
    private BottomSheetBehavior<FrameLayout> behavior;

    private AutocompleteSupportFragment autocompleteSupportFragment;
    private AutocompleteSupportFragment autocompleteSupportFragment2;
    private View resumeView;

    private LatLng destinationLatLng;
    private LatLng originLatLng;


    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof BottomSheetListener) {
            bottomSheetListener = (BottomSheetListener) context;
        } else {
            throw new ClassCastException(context.toString() + "must implement BottomSheetListener");
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rider_maps, container, false);

        //casting views
        bottomSheet = view.findViewById(R.id.bottom_sheet);
        dropLocationText = view.findViewById(R.id.drop_location_text);
        pickupLocationText = view.findViewById(R.id.pickup_location_text);
        fragLayout = view.findViewById(R.id.frag_layout);
        dropClearBtn = view.findViewById(R.id.drop_clear_btn);
        pickupClearBtn = view.findViewById(R.id.pickup_clear_btn);
        pickupLocationLayout = view.findViewById(R.id.pickup_layout);

        // Customize the bottom sheet behavior
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setPeekHeight(350);
        behavior.setSkipCollapsed(true); // Prevent collapsing to a smaller state


        dropClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetListener.onBottomSheetOpened(true);
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetListener.onBottomSheetOpened(false);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


        bottomSheet.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // Consume the touch event to prevent it from propagating to the background view.
                return true;
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        resumeView = view;
        Log.d("riderCar", "car is : " + carType);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.rider_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        mapFragment.onResume();
        mapFragment.onCreate(savedInstanceState);

        mMapView = mapFragment.getView();

        //init(view);
    }

    private void init(View view) {
        //setup of google places api for pickup location.
        Log.d("cartype", "the car is : " + carType);
        Places.initialize(getContext(), getString(R.string.REAL_API_KEY));

        autocompleteSupportFragment = (AutocompleteSupportFragment) getChildFragmentManager()
                .findFragmentById(R.id.autoComplete_fragment);

        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.NAME,
                Place.Field.LAT_LNG));
        autocompleteSupportFragment.setHint(getString(R.string.drop_search_message));

        //AutoCompleteTextView inside the AutocompleteSupportFragment
        AppCompatEditText autoCompleteTextView = autocompleteSupportFragment.getView()
                .findViewById(com.google.android.libraries.places.R.id.places_autocomplete_search_input);


        //TextWatcher to detect text changes
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() == 0) {
                    dropLocationText.setText("Where would you go?");
                    dropClearBtn.setImageResource(R.drawable.destination_ic);

                    //empty
                    bHomeFragment.dropLocation = "";


                } else {
                    dropClearBtn.setImageResource(R.drawable.cancel_ic);
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    bottomSheetListener.onBottomSheetOpened(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {
                Snackbar.make(requireView(), status.getStatusMessage(), Snackbar.LENGTH_LONG).show();

            }

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                dropLocationText.setText(place.getAddress());
                destinationLatLng= new LatLng(Objects.requireNonNull(place.getLatLng()).latitude,place.getLatLng().longitude);

                if (!dropLocationText.getText().toString().isEmpty()) {
                    bHomeFragInstance();
                    bHomeFragment.dropLocation = place.getAddress();
                    bHomeFragment.dropLocationLatLng=place.getLatLng();
                    Toast.makeText(getActivity(), "choosed", Toast.LENGTH_SHORT).show();

                    //showing pickuplocation layout
                    pickupLocationLayout.setVisibility(View.VISIBLE);
                }
            }
        });


        //pickup search
        autocompleteSupportFragment2 = (AutocompleteSupportFragment) getChildFragmentManager()
                .findFragmentById(R.id.autoComplete_fragment2);

        autocompleteSupportFragment2.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.NAME,
                Place.Field.LAT_LNG));
        autocompleteSupportFragment2.setHint(getString(R.string.pickup_search_message));

        //AutoCompleteTextView inside the AutocompleteSupportFragment
        AppCompatEditText autoCompleteTextView2 = autocompleteSupportFragment2.getView()
                .findViewById(com.google.android.libraries.places.R.id.places_autocomplete_search_input);


        //TextWatcher to detect text changes
        autoCompleteTextView2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() == 0) {
                    pickupLocationText.setText("Pickup Location");
                    pickupClearBtn.setImageResource(R.drawable.destination_ic);

                    //empty
                    bHomeFragment.pickupLocation = "";
                } else {
                    pickupClearBtn.setImageResource(R.drawable.cancel_ic);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        autocompleteSupportFragment2.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {
                Snackbar.make(requireView(), status.getStatusMessage(), Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                pickupLocationText.setText(place.getAddress());
                originLatLng= new LatLng(Objects.requireNonNull(place.getLatLng()).latitude,place.getLatLng().longitude);
                if (!pickupLocationText.getText().toString().isEmpty()) {
                    bHomeFragInstance();
                    bHomeFragment.pickupLocationLatLng=place.getLatLng();
                    bHomeFragment.pickupLocation = place.getAddress();
                }
            }
        });


        //till here


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


                        setRestrictPlacesInCountry(locationResult.getLastLocation(), autocompleteSupportFragment);
                        setRestrictPlacesInCountry(locationResult.getLastLocation(), autocompleteSupportFragment2);
                    } else {
                        previousLocation = currentLocation;
                        currentLocation = locationResult.getLastLocation();
                    }
                    if (previousLocation.distanceTo(currentLocation) / 1000 <= LIMIT_RANGE) {
                        //loadAvailableDrivers(view);
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

    private void setRestrictPlacesInCountry(Location location, AutocompleteSupportFragment autocompleteSupportFragment) {
        try {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (!addressList.isEmpty()) {
                autocompleteSupportFragment.setCountries(addressList.get(0).getCountryCode());
            } else {

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
                    if (addressList.size() > 0) {
                        cityName = addressList.get(0).getLocality();
                    }
                    if (!TextUtils.isEmpty(cityName) && carType != null) {


                        //query
                        DatabaseReference driver_location_ref = FirebaseDatabase.getInstance().getReference(Common.DRIVER_LOCATION_REFERENCE)
                                .child(cityName).child(carType);
                        GeoFire gf = new GeoFire(driver_location_ref);
                        GeoQuery geoQuery = gf.queryAtLocation(new GeoLocation(location.getLatitude(), location.getLongitude()), distance);
                        geoQuery.removeAllListeners();

                        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                            @Override
                            public void onKeyEntered(String key, GeoLocation location) {
                                //Common.driveFound.add(new DriverGeoModel(key, location));
                                if(!Common.driverFound.containsKey(key)){
                                    Common.driverFound.put(key, new DriverGeoModel(key,location));
                                }
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
                                    findDriverByKey(driverGeoModel, view);
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
                    } else {
                        //Snackbar.make(requireView(), getString(R.string.city_name_empty), Snackbar.LENGTH_LONG).show();
                    }

                } catch (IOException e) {
                    Log.d("hasee", "" + e.getMessage());
                    Snackbar.make(view, e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void addDriverMarker(View view) {
        if (Common.driverFound.size() > 0) {

            Observable.fromIterable(Common.driverFound.keySet()).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(key -> {
                        //on next
                        findDriverByKey(Common.driverFound.get(key),view);

                    }, throwable -> {
                        Snackbar.make(requireView(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }, () -> {
                    });
        } else {
            Snackbar.make(view, getString(R.string.Driver_Not_Found), Snackbar.LENGTH_LONG).show();
        }
    }

    private void findDriverByKey(DriverGeoModel driverGeoModel, View view) {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(driverGeoModel.getKey()).child("DriverId").child("PersonalInfo")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            driverGeoModel.setDriverInfoModel(snapshot.getValue(DriverInfoModel.class));
                            Common.driverFound.get(driverGeoModel.getKey()).setDriverInfoModel(snapshot.getValue(DriverInfoModel.class));
                            iFirebaseDriverInfoListener.onDriverInfoLoadSuccess(driverGeoModel, view);
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
    public void onStart() {
        Log.d("strrr", "str called!");
        init(resumeView);
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d("strrr", "res called!");
        super.onResume();
    }

    @Override
    public void onFirebaseLoadFailed(String message) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onDriverInfoLoadSuccess(DriverGeoModel driverGeoModel, View view) {


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


            if (!TextUtils.isEmpty(cityName) && carType != null) {

                DatabaseReference driverLocation = FirebaseDatabase.getInstance()
                        .getReference(Common.DRIVER_LOCATION_REFERENCE)
                        .child(cityName).child(carType)
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

                        } else {
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
                                    moveMarkerAnimation(driverGeoModel.getKey(), animationModel, currentMarker, from, to, view);

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

    private void moveMarkerAnimation(String key, AnimationModel animationModel, Marker currentMarker, String from, String to, View view) {
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

                                        //polylineList = Common.decodePoly(polyline);
                                        animationModel.setPolylineList(Common.decodePoly(polyline));


                                    }

//                            handler = new Handler();
//                            index = -1;
//                            next = 1;
                                    animationModel.setIndex(-1);
                                    animationModel.setNext(1);

                                    Runnable runnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            if (animationModel.getPolylineList() != null && animationModel.getPolylineList().size() > 1) {

                                                if (animationModel.getIndex() < animationModel.getPolylineList().size() - 2) {
                                                    // index++;
                                                    animationModel.setIndex(animationModel.getIndex() + 1);
//                                            next = index + 1;
//                                            start = polylineList.get(index);
//                                            end = polylineList.get(next);
                                                    animationModel.setNext(animationModel.getIndex() + 1);
                                                    animationModel.setStart(animationModel.getPolylineList().get(animationModel.getIndex()));
                                                    animationModel.setEnd(animationModel.getPolylineList().get(animationModel.getNext()));

                                                }
                                                ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 1);
                                                valueAnimator.setDuration(3000);
                                                valueAnimator.setInterpolator(new LinearInterpolator());
                                                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                                    @Override
                                                    public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator) {
                                                        //v = valueAnimator.getAnimatedFraction();
                                                        animationModel.setV(valueAnimator.getAnimatedFraction());
//                                                lat = v * end.latitude + (1 - v) * start.latitude;
//                                                lng = v * end.longitude + (1 - v) * start.longitude;
                                                        animationModel.setLat(animationModel.getV() * animationModel.getEnd().latitude + (1 - animationModel.getV())
                                                                * animationModel.getStart().latitude);

                                                        animationModel.setLng(animationModel.getV() * animationModel.getEnd().longitude + (1 - animationModel.getV())
                                                                * animationModel.getStart().longitude);


                                                        LatLng newPos = new LatLng(animationModel.getLat(), animationModel.getLng());
                                                        currentMarker.setPosition(newPos);
                                                        currentMarker.setAnchor(0.5f, 0.5f);
                                                        currentMarker.setRotation(Common.getBearing(animationModel.getStart(), newPos));
                                                    }
                                                });
                                                valueAnimator.start();
                                                if (animationModel.getIndex() < animationModel.getPolylineList().size() - 2) {
                                                    animationModel.getHandler().postDelayed(this, 1500);

                                                } else if (animationModel.getIndex() < animationModel.getPolylineList().size() - 1) {
                                                    animationModel.setRun(false);
                                                    Common.driverLocationSubscribe.put(key, animationModel);

                                                }
                                            }
                                        }
                                    };

                                    animationModel.getHandler().postDelayed(runnable, 1500);


                                } catch (Exception e) {
                                    Snackbar.make(requireView(), e.getMessage(), Snackbar.LENGTH_LONG).show();
                                }
                            })
            );
        }
    }

    @Override
    public void bHomeEndFun(boolean bool) {
        if (bool) {
            fragLayout.setVisibility(View.GONE);
        } else {
            fragLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void selectedCar(String str) {
        carType = str;
        init(resumeView);
        Log.d("thecar", "is :" + str);
        Toast.makeText(getActivity(), str + " selected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void bookRideClicked() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           Snackbar.make(requireView(),"Please allow all the permissions in the app",Snackbar.LENGTH_LONG).show();
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        LatLng origin = originLatLng;
                        LatLng destination = destinationLatLng;
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.rider_map, new RequestDriverFragment()).commit();
                        EventBus.getDefault().postSticky(new SelectPlaceEvent(origin,destination));

                    }
                });
    }

    @Override
    public void invokeFunction(int distanceInt, String distanceString) {
         RequestDriverFragment requestDriverFragment =(RequestDriverFragment) getActivity().getSupportFragmentManager()
                 .findFragmentById(R.id.rider_map);
         if(requestDriverFragment!=null){
//             requestDriverFragment.ggFF();
             requestDriverFragment.sendRequestToDrivers(getActivity(),distanceInt,distanceString);
         }

    }


    public interface BottomSheetListener{
        void onBottomSheetOpened(boolean bool);
    }

    public void bHomeFragInstance(){
        if(bHomeFragment == null){
            bHomeFragment = new BHomeFragment();
            //setting fragments on bottom sheet
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.container_bottom_sheet, bHomeFragment)
                    .commit();
        }
    }

}