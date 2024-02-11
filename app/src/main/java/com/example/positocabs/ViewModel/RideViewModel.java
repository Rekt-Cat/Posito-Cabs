package com.example.positocabs.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.positocabs.Models.DataModel.Booking;
import com.example.positocabs.Models.DataModel.Driver;
import com.example.positocabs.Models.DataModel.RideCheckResult;
import com.example.positocabs.Models.DataModel.Trip;
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

    public void bookRide(Trip trip){
        rideRepo.bookRide(trip);
    }

    public void createRiderTrip(String token, DriverRequestReceived event, Booking booking){
        rideRepo.createRiderTrip(token, event, booking);
    }

    public LiveData<RideCheckResult> checkRides(String riderId){
        return rideRepo.checkRides(riderId);
    }

    public LiveData<Trip> getTripDetails(String tripId){
        return rideRepo.getTripDetails(tripId);
    }

    public void cancelTrip(String tripId){

        rideRepo.cancelTrip(tripId);
    }
}
