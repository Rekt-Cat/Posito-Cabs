package com.example.positocabs.Repository;

import android.app.Application;
import android.content.Intent;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.positocabs.Models.NtpTimeFetcher;
import com.example.positocabs.Views.Auth.MakeProfileActivity;
import com.example.positocabs.Views.Auth.OtpActivity;
import com.example.positocabs.Views.MainScreen.DriverMain.DriverMainActivity;
import com.example.positocabs.Views.MainScreen.RiderMain.RiderMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.net.InetAddress;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Handler;

public class AuthRepo {
    private Application application;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private MutableLiveData<FirebaseUser> firebaseUserMutableLiveData;
    private MutableLiveData<Boolean> userLoggedInStatusMutableLiveData;
    private boolean isUserRegistered;
    private boolean isRegistered;

    public AuthRepo(Application application){
        this.application = application;
        firebaseUserMutableLiveData = new MutableLiveData<>();
        userLoggedInStatusMutableLiveData = new MutableLiveData<>();
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            firebaseUserMutableLiveData.postValue(firebaseAuth.getCurrentUser());
            userLoggedInStatusMutableLiveData.setValue(true);
        }
    }

    public MutableLiveData<FirebaseUser> getFirebaseUserMutableLiveData(){
        return firebaseUserMutableLiveData;
    }

    public MutableLiveData<Boolean> getUserLoggedInStatusMutableLiveData() {
        return userLoggedInStatusMutableLiveData;
    }

    public void logginInUser(String userType, String phoneNo, String verificationId, String otp, LoginCallback loginCallback){

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    currentUser = firebaseAuth.getCurrentUser();

                    if(currentUser!=null){

                        firebaseUserMutableLiveData.postValue(firebaseAuth.getCurrentUser());
                        Toast.makeText(application, "OTP verified!", Toast.LENGTH_SHORT).show();

                        isUserRegistered(currentUser, userType, loginCallback);
                    }
                    else{
                        Toast.makeText(application, "User is null.", Toast.LENGTH_SHORT).show();
                        // User is not logged in
                        loginCallback.onLoginCompleted(false);
                    }
                }
                else {
                    firebaseUserMutableLiveData.postValue(null);
                    Toast.makeText(application, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    loginCallback.onLoginCompleted(false);
                }
            }
        });
    }

    public void signOut(){
        firebaseAuth.signOut();
        userLoggedInStatusMutableLiveData.postValue(false);
    }

    private void isUserRegistered(FirebaseUser currentUser,String userType, LoginCallback loginCallback) {
        //checking if user is registred
        if (currentUser != null) {
            FirebaseUserMetadata firebaseUserMetadata = currentUser.getMetadata();
            if (firebaseUserMetadata != null) {

                NtpTimeFetcher.fetchNtpTime(new NtpTimeFetcher.NtpTimeListener() {
                    @Override
                    public void onNtpTimeFetched(long ntpTime) {

                        // Check if the user is logging in for the first time
                        long accountCreationTimeStamp = currentUser.getMetadata().getCreationTimestamp();
                        long currentTimeStamp = ntpTime;

                        System.out.println(new Date(accountCreationTimeStamp));
                        System.out.println(new Date(currentTimeStamp));

                        // If the creation timestamp is the same as the current time, it means the user just signed up
                        if (Math.abs(currentTimeStamp - accountCreationTimeStamp) < 5000) {
                            // User is logging in for the first time

                            Toast.makeText(application, "making def nodes", Toast.LENGTH_SHORT).show();
                            FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid()).child("isRider").setValue(false);
                            FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid()).child("isDriver").setValue(false);

                            loginCallback.onLoginCompleted(false);
                        } else {
                            // User has logged in before
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid()).child("is" + userType);
                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    isRegistered = snapshot.getValue(Boolean.class);
                                    loginCallback.onLoginCompleted(isRegistered);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(application, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                    loginCallback.onLoginCompleted(false);
                                }
                            });
                        }
                    }
                });

            } else {
                // User metadata is not available
                // Handle the case where you cannot determine if it's the first login
                Toast.makeText(application, "can't determine if it's the first login", Toast.LENGTH_SHORT).show();
            }
        } else {
            // User is not logged in
            loginCallback.onLoginCompleted(false);
        }
    }

}
