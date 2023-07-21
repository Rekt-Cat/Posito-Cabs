package com.example.positocabs.Repository;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.positocabs.Models.ReadWriteUserDetails;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;


public class SaveUserDataRepo {
    private Application application;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    ReadWriteUserDetails readWriteUserDetails;

    private MutableLiveData<Boolean> isDone;
    String myUrl = null;

    public MutableLiveData<Boolean> getIsDone() {
        return isDone;
    }

    boolean done;

    public SaveUserDataRepo(Application application) {
        this.application = application;
        isDone = new MutableLiveData<>();
        mAuth = FirebaseAuth.getInstance();
    }

    public void saveUserData(int userType, String name, String email, String gender, String dob, StorageReference storageReference, Uri imageUri) {
        //saving users data
        if (userType == 1) {
            mUser = mAuth.getCurrentUser();
            readWriteUserDetails = new ReadWriteUserDetails(name, email, gender, dob);
            mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid());

            uploadImage(storageReference, imageUri, userType, mRef);
            mRef.setValue(readWriteUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    isDone.postValue(true);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    isDone.postValue(false);
                }
            });

            isDone.postValue(false);

        }
        //saving drivers data
        else if (userType == 2) {
            mUser = mAuth.getCurrentUser();
            readWriteUserDetails = new ReadWriteUserDetails(name, email, gender, dob);
            mRef = FirebaseDatabase.getInstance().getReference().child("Drivers").child(mUser.getUid());


            Log.d("uploadDone", "" + done);

            mRef.setValue(readWriteUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    uploadImage(storageReference, imageUri, userType, mRef);
                    isDone.postValue(true);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    isDone.postValue(false);
                }
            });

            isDone.postValue(false);


        } else {
            Log.d("DataSaveError", "invalid UserType");
        }

    }

    public void saveDriverDocs(int userType, StorageReference storageReference, Uri dl, Uri vehicleInsurance, Uri pan, Uri vehiclePermit){
        mUser=mAuth.getCurrentUser();
        mRef=FirebaseDatabase.getInstance().getReference().child("Drivers").child(mUser.getUid()).child("Docs");

        uploadImage(storageReference, dl, userType, mRef);
        uploadImage(storageReference, vehicleInsurance, userType, mRef);
        uploadImage(storageReference, pan, userType, mRef);
        uploadImage(storageReference, vehiclePermit, userType, mRef);

    }


    public void uploadImage(StorageReference storageReference, Uri imageUri, int userType, DatabaseReference reference) {


        StorageReference fileReference = storageReference.child(UUID.randomUUID().toString());
        fileReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                reference.child("ProfilePicture").setValue(String.valueOf(uri));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("uploadImageError", databaseError.getMessage());
                                throw databaseError.toException();
                            }
                        });

                    }
                });
            }
        });


    }

}
