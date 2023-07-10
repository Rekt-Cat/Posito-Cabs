package com.example.positocabs.Repository;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.positocabs.Models.ReadWriteUserDetails;
import com.example.positocabs.Views.Auth.OtpActivity;
import com.example.positocabs.Views.Auth.SignupActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class AuthRepo {
    private Application application;
    private FirebaseAuth firebaseAuth;
    private MutableLiveData<FirebaseUser> firebaseUserMutableLiveData;
    private MutableLiveData<Boolean> userLoggedInStatusMutableLiveData;

    public AuthRepo(Application application){
        this.application = application;
        firebaseUserMutableLiveData = new MutableLiveData<>();
        userLoggedInStatusMutableLiveData = new MutableLiveData<>();
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            firebaseUserMutableLiveData.postValue(firebaseAuth.getCurrentUser());
        }
    }

    public MutableLiveData<FirebaseUser> getFirebaseUserMutableLiveData(){
        return firebaseUserMutableLiveData;
    }

    public MutableLiveData<Boolean> getUserLoggedInStatusMutableLiveData() {
        return userLoggedInStatusMutableLiveData;
    }

    public  void registerUser(String phoneNo, String verificationId, String otp,
                              OnCompleteListener<AuthResult> onCompleteListener){

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(onCompleteListener);
    }

    public void signOut(){
        firebaseAuth.signOut();
        userLoggedInStatusMutableLiveData.postValue(true);
    }







    //ayush code
//    private Application application;
//    private FirebaseAuth firebaseAuth;
//    private MutableLiveData<FirebaseUser> firebaseUserMutableLiveData;
//
//    DatabaseReference ref;
//
//    MutableLiveData<Boolean> userLoggedInStatusMutableLiveData;
//
//    public MutableLiveData<FirebaseUser> getFirebaseUserMutableLiveData() {
//        return firebaseUserMutableLiveData;
//    }
//
//    public MutableLiveData<Boolean> getUserLoggedInStatusMutableLiveData() {
//        return userLoggedInStatusMutableLiveData;
//    }
//
//    public AuthRepo(Application application) {
//
//        this.application = application;
//        this.firebaseAuth = FirebaseAuth.getInstance();
//        firebaseUserMutableLiveData = new MutableLiveData<>();
//
//        if (firebaseAuth.getCurrentUser() != null) {
//            firebaseUserMutableLiveData.postValue(firebaseAuth.getCurrentUser());
//        }
//    }
//
//    public void verifyOtp(String phoneNo, String otp){
//
//    }
//    public void registerUser(String phoneNo) {
//        Log.d("lol", "inside authrepo registerUser: ");
//
//    }


}
