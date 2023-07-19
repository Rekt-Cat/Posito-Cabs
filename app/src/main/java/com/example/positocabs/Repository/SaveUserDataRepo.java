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

    public SaveUserDataRepo(Application application) {
        this.application = application;
        isDone = new MutableLiveData<>();
        mAuth = FirebaseAuth.getInstance();
    }

    public void saveUserData(int userType, String name, String phoneNo, String email, String gender, String dob, StorageReference storageReference, Uri imageUri) {
        //saving users data
        if (userType == 1) {
            mUser = mAuth.getCurrentUser();
            readWriteUserDetails = new ReadWriteUserDetails(name, phoneNo, email, gender, dob);
            mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid());

            Boolean done = uploadImage(storageReference, imageUri, userType, mRef);
            if (done) {
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
            } else {
                isDone.postValue(false);
            }
        }
        //saving drivers data
        else if (userType == 2) {
            mUser = mAuth.getCurrentUser();
            readWriteUserDetails = new ReadWriteUserDetails(name, phoneNo, email, gender, dob);
            mRef = FirebaseDatabase.getInstance().getReference().child("Drivers").child(mUser.getUid());

            Boolean done = uploadImage(storageReference, imageUri, userType, mRef);
            if (done) {
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
            } else {
                isDone.postValue(false);
            }

        } else {
            Log.d("DataSaveError", "invalid UserType");
        }

    }

    public boolean uploadImage(StorageReference storageReference, Uri imageUri, int userType, DatabaseReference reference) {

        Boolean[] done = {null};
        StorageReference fileReference = storageReference.child(UUID.randomUUID().toString());
        StorageTask uploadTask = fileReference.putFile(imageUri);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    done[0] = true;
                    // FirebaseUser firebaseUser = auth.getCurrentUser();
                    Uri downloadUri = task.getResult();
                    myUrl = downloadUri.toString();
                    Log.d("hehe", "The url is : " + myUrl);
                    //adding a new key-value pair to an existing node
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                snapshot.getRef().child("ProfilePic").setValue(myUrl);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            throw databaseError.toException();
                        }
                    });

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                done[0] = false;
            }
        });

        return done[0];
    }

}
