package com.example.positocabs.Repository;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.positocabs.Models.ReadWriteUserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AuthRepo {
    Application application;

    FirebaseAuth firebaseAuth;
    DatabaseReference ref;
    MutableLiveData<FirebaseUser> firebaseUserMutableLiveData;
    MutableLiveData<Boolean> userLoggedInStatusMutableLiveData;

    public MutableLiveData<FirebaseUser> getFirebaseUserMutableLiveData() {
        return firebaseUserMutableLiveData;
    }

    public MutableLiveData<Boolean> getUserLoggedInStatusMutableLiveData() {
        return userLoggedInStatusMutableLiveData;
    }

    public AuthRepo(Application application) {

        this.application = application;
        this.firebaseAuth = FirebaseAuth.getInstance();
        firebaseUserMutableLiveData = new MutableLiveData<>();
        if (firebaseAuth.getCurrentUser() != null) {
            firebaseUserMutableLiveData.postValue(firebaseAuth.getCurrentUser());
        }
    }

    public void registerUser(String email, String password) {
        Log.d("lol", "inside authrepo registerUser: ");
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //getting the user
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    //important line as this data will be observed by the view classes
                    firebaseUserMutableLiveData.postValue(firebaseAuth.getCurrentUser());
                    //writing in the database.
                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(email, password, firebaseUser.getUid());
                    ref = FirebaseDatabase.getInstance().getReference("Users");
                    ref.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(application, "User Registered successfully, Please verify your email!", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });


                }
                else{
                    try{
                        throw task.getException();
                    }
                    catch (FirebaseAuthUserCollisionException e){
                        Toast.makeText(application, "User already registered!", Toast.LENGTH_LONG).show();
                    }
                    catch (FirebaseAuthInvalidCredentialsException e){
                        Toast.makeText(application, "Please enter a valid email!", Toast.LENGTH_LONG).show();
                    } catch (Exception e){
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(application, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("lol", "sheesh! "+e.getMessage());
            }
        });

    }


}
