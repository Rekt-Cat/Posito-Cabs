package com.example.positocabs.Repository;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.positocabs.Models.DataModel.Booking;
import com.example.positocabs.Models.DataModel.User;
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

    public LiveData<List<User>> getDrivers(){
        MutableLiveData<List<User>> mutableLiveData = new MutableLiveData(new ArrayList<>());

        DatabaseReference databaseReference = mRef.child("Users");
        DatabaseReference databaseRef = mRef.child("RiderTrip").child(currentUser.getUid());

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> driverList = new ArrayList<>();

                if(snapshot.exists()){
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        String childNodeKey = childSnapshot.getKey();

                        databaseReference.child(childNodeKey).child("DriverId").child("PersonalInfo")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User driver = snapshot.getValue(User.class);
                                driverList.add(driver);
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
