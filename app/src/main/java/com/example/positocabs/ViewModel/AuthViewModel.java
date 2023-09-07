package com.example.positocabs.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.positocabs.Repository.AuthRepo;
import com.example.positocabs.Repository.LoginCallback;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends AndroidViewModel {

    private AuthRepo authRepo;
    private MutableLiveData<FirebaseUser> userData;
    private MutableLiveData<Boolean> loggedStatus;

    public MutableLiveData<FirebaseUser> getUserData() {
        return userData;
    }

    public MutableLiveData<Boolean> getLoggedStatus() {
        return loggedStatus;
    }

    public AuthViewModel(@NonNull Application application) {
        super(application);
        authRepo = new AuthRepo(application);
        userData = authRepo.getFirebaseUserMutableLiveData();
        loggedStatus = authRepo.getUserLoggedInStatusMutableLiveData();
    }

    public void logginInUser(String userType, String phoneNo, String verificationId, String otp, LoginCallback loginCallback){

         authRepo.logginInUser(userType,phoneNo, verificationId, otp, loginCallback);
         userData=authRepo.getFirebaseUserMutableLiveData();

    }

    public void signOut(){
        authRepo.signOut();
    }


}
