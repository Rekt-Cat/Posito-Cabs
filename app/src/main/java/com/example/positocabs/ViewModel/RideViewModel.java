package com.example.positocabs.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.positocabs.Models.DataModel.Booking;
import com.example.positocabs.Models.DataModel.User;
import com.example.positocabs.Repository.RideRepo;

import java.util.List;

public class RideViewModel extends AndroidViewModel {

    private RideRepo rideRepo;

    public RideViewModel(@NonNull Application application) {
        super(application);

        rideRepo = new RideRepo(application);
    }

    public LiveData<List<User>> getDrivers(){
        return rideRepo.getDrivers();
    }

    public void bookRide(String driverId, Booking booking){
        rideRepo.bookRide(driverId,booking);
    }

    public LiveData<Booking> checkRides(){
        return rideRepo.checkRides();
    }
}
