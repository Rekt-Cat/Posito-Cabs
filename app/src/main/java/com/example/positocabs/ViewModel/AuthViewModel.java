package com.example.positocabs.ViewModel;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.positocabs.Repository.AuthRepo;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends AndroidViewModel {

    AuthRepo authRepo;
    MutableLiveData<FirebaseUser> userData;
    MutableLiveData<Boolean> loggedStatus;

    public MutableLiveData<FirebaseUser> getUserData() {
        return userData;
    }

    public MutableLiveData<Boolean> getLoggedStatus() {
        return loggedStatus;
    }

    public AuthViewModel(@NonNull Application application) {
        super(application);
        authRepo=new AuthRepo(application);
    }

    public void register(String email,String password){
        Log.d("lol", "inside authVM registerUser: ");
        authRepo.registerUser(email,password);
        userData= authRepo.getFirebaseUserMutableLiveData();

    }

}
