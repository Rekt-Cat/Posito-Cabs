package com.example.positocabs.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.positocabs.Models.DataModel.Booking;
import com.example.positocabs.Models.DataModel.Driver;
import com.example.positocabs.Models.DataModel.User;
import com.example.positocabs.Models.Event.DriverRequestReceived;
import com.example.positocabs.Repository.RideRepo;

import java.util.List;

public class RideViewModel extends AndroidViewModel {

    private RideRepo rideRepo;

    public RideViewModel(@NonNull Application application) {
        super(application);

        rideRepo = new RideRepo(application);
    }

    public LiveData<List<Driver>> getDrivers(){
        return rideRepo.getDrivers();
    }

    public void bookRide(String driverId, Booking booking){
        rideRepo.bookRide(driverId,booking);
    }

    public void createRiderTrip(String token, DriverRequestReceived event, Booking booking){
        rideRepo.createRiderTrip(token, event, booking);
    }

    public LiveData<Booking> checkRides(){
        return rideRepo.checkRides();
    }
}
