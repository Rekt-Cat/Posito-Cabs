package com.example.positocabs.Repository;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.positocabs.Models.DataModel.Booking;
import com.example.positocabs.Models.DataModel.Driver;
import com.example.positocabs.Models.DataModel.User;
import com.example.positocabs.Models.Event.DriverRequestReceived;
import com.example.positocabs.Services.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
        DatabaseReference databaseRef = mRef.child("RiderTrip").child(currentUser.getUid());

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Driver> driverList = new ArrayList<>();

                if(snapshot.exists()){
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        String childNodeKey = childSnapshot.getKey();

                        databaseReference.child(childNodeKey).child("DriverId").child("PersonalInfo")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);

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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return mutableLiveData;
    }

    public void bookRide(String driverId, Booking booking){
        DatabaseReference databaseReference = mRef.child("DriverTrip").child(driverId);

        databaseReference.child("RiderId").setValue(currentUser.getUid());
        databaseReference.child("BookingDetails").setValue(booking).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(application, "Ride Booked!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void createRiderTrip(String token, DriverRequestReceived event, Booking booking){
        DatabaseReference riderTripRef = FirebaseDatabase.getInstance().getReference().child(Common.RIDER_TRIP);

        riderTripRef.child(event.getKey()).child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("BookingDetails").setValue(booking);
        riderTripRef.child(event.getKey()).child(FirebaseAuth.getInstance().getUid()).child(Common.TOKEN_REFERENCE).setValue(token);
        riderTripRef.child(event.getKey()).child(FirebaseAuth.getInstance().getUid()).child("UID").setValue(FirebaseAuth.getInstance().getUid());
    }

    public LiveData<Booking> checkRides(){
        MutableLiveData<Booking> mutableLiveData = new MutableLiveData<>();

        DatabaseReference databaseReference = mRef.child("DriverTrip").child(currentUser.getUid())
                .child("BookingDetails");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Booking booking = snapshot.getValue(Booking.class);
                    mutableLiveData.postValue(booking);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return mutableLiveData;
    }
}
