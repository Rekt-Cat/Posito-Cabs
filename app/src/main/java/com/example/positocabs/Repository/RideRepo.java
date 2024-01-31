package com.example.positocabs.Repository;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.positocabs.Models.DataModel.Booking;
import com.example.positocabs.Models.DataModel.Driver;
import com.example.positocabs.Models.DataModel.Trip;
import com.example.positocabs.Models.DataModel.User;
import com.example.positocabs.Models.Event.DriverRequestReceived;
import com.example.positocabs.Services.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RideRepo {

    private Application application;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private DatabaseReference mRef;

    public RideRepo(Application application) {
        this.application = application;

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null){
            currentUser = firebaseAuth.getCurrentUser();

            mRef = FirebaseDatabase.getInstance().getReference();
        }
    }


    public LiveData<List<Driver>> getDrivers(){
        MutableLiveData<List<Driver>> mutableLiveData = new MutableLiveData(new ArrayList<>());

        DatabaseReference databaseReference = mRef.child("Users");
        DatabaseReference databaseRef = mRef.child("Ride Requests").child(currentUser.getUid());

        Log.d("Valx", "Adding");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("Valx", "Adding2");
                List<Driver> driverList = new ArrayList<>();

                if(snapshot.exists()){
                    Log.d("Valx", "exists");
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        String childNodeKey = childSnapshot.getKey();

                        databaseReference.child(childNodeKey).child("DriverId").child("PersonalInfo")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                        User user = userSnapshot.getValue(User.class);

                                        DataSnapshot bookingSnapshot = childSnapshot.child("BookingDetails");
                                        Booking booking = bookingSnapshot.getValue(Booking.class);

                                        driverList.add(new Driver(user,booking));

                                        mutableLiveData.postValue(driverList);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                }
                else {
                    Log.d("Valx", "else");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return mutableLiveData;
    }

    public void bookRide(Trip trip){

        mRef.child("Ride Requests").child(currentUser.getUid())
                .child(trip.getDriverId()).child("Status").setValue("Confirmed");

        trip.setRiderId(currentUser.getUid());
        trip.setStatus("Confirmed");

        DatabaseReference databaseReference = mRef.child("Trips").child(trip.getTripId());

        databaseReference.setValue(trip).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(application, "Ride Booked!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public LiveData<Trip> getTripDetails(String tripId){

        DatabaseReference databaseReference = mRef.child("Trips").child(tripId);
        MutableLiveData<Trip> mutableLiveData = new MutableLiveData<>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("status").getValue().equals("Confirmed")){
                        driverAvailability(false);
                    }
                    else{
                        driverAvailability(true);
                    }

                    DatabaseReference databaseReference2;
                    if(currentUser.getUid().equals(snapshot.child("riderId").getValue())){
                        databaseReference2 = mRef.child("Users").child(snapshot.child("driverId").getValue().toString())
                                .child("DriverId").child("PersonalInfo");
                    }
                    else{
                        databaseReference2 = mRef.child("Users").child(snapshot.child("riderId").getValue().toString())
                                .child("RiderId").child("PersonalInfo");
                    }

                    Trip trip = snapshot.getValue(Trip.class);

                    databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot childSnapshot) {
                            User user = childSnapshot.getValue(User.class);
                            trip.setUser(user);
                            mutableLiveData.postValue(trip);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return mutableLiveData;
    }

    public void createRiderTrip(String token, DriverRequestReceived event, Booking booking){
        DatabaseReference riderTripRef = FirebaseDatabase.getInstance().getReference().child("Ride Requests");

        riderTripRef.child(event.getKey()).child(FirebaseAuth.getInstance().getUid()).child(Common.TOKEN_REFERENCE).setValue(token);
        riderTripRef.child(event.getKey()).child(FirebaseAuth.getInstance().getUid()).child("UID").setValue(FirebaseAuth.getInstance().getUid());
        riderTripRef.child(event.getKey()).child(FirebaseAuth.getInstance().getUid()).child("Status").setValue("Pending");
        riderTripRef.child(event.getKey()).child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("BookingDetails").setValue(booking);

    }

    public LiveData<Booking> checkRides(String riderId){
        MutableLiveData<Booking> mutableLiveData = new MutableLiveData<>();

        DatabaseReference databaseReference = mRef.child("Ride Requests").child(riderId)
                .child(currentUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child("Status").exists()){
                    if (snapshot.child("Status").getValue().equals("Confirmed")){
                        driverAvailability(false);

                        Booking booking = snapshot.child("BookingDetails").getValue(Booking.class);
                        mutableLiveData.postValue(booking);
                    }
                    else if(snapshot.child("Status").getValue().equals("Cancelled")){
                        driverAvailability(true);
                        mutableLiveData.postValue(new Booking());
                    }
                }
                else{
                    driverAvailability(true);
                    mutableLiveData.postValue(new Booking());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("DriverStatus", "Can't read");
            }
        });

        return mutableLiveData;
    }

    public void cancelTrip(String tripId){

        DatabaseReference databaseReference = mRef.child("Trips").child(tripId);

        databaseReference.child("status").setValue("Cancelled")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            databaseReference.child("riderId").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String riderId = String.valueOf(snapshot.getValue());
                                    freeDrivers(riderId);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.d("RiderId in trip",  String.valueOf(error.getMessage().toString()));
                                }
                            });
                        }

                        Log.d("ride cancellation",  String.valueOf(task.isSuccessful()));
                    }
                });
    }

    private void driverAvailability(boolean bool){
        DatabaseReference databaseReference = mRef.child("Users").child(currentUser.getUid())
                .child("DriverId").child("isAvailable");

        databaseReference.setValue(bool);
    }

    private void freeDrivers(String riderId){
        DatabaseReference databaseReference = mRef.child("Ride Requests").child(riderId);

        databaseReference.removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("Free Drivers",  String.valueOf(task.isSuccessful()));
                    }
                });

    }
}
