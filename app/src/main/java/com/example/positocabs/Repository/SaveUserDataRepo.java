package com.example.positocabs.Repository;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.positocabs.Callback.TaskCallback;
import com.example.positocabs.Models.User;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;


public class SaveUserDataRepo {
    private Application application;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private MutableLiveData<Boolean> isDone;
    private String myUrl = null;

    public MutableLiveData<Boolean> getIsDone() {
        return isDone;
    }

    boolean done;

    public SaveUserDataRepo(Application application) {
        this.application = application;
        isDone = new MutableLiveData<>();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());
    }

    public void saveUserData(User user, String userType, Uri imageUri, TaskCallback taskCallback) {

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Users and drivers profile pics").child(mUser.getUid());
        DatabaseReference databaseReference = mRef.child(userType+"Id");

        databaseReference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    uploadImage(storageReference, imageUri, databaseReference, "userPfp", taskCallback);
                    FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid()).child("is"+userType).setValue(true);
                    isDone.postValue(true);
                }
                else{
                    taskCallback.onFailure(task.getException().getMessage());
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                taskCallback.onFailure(e.getMessage());
                isDone.postValue(false);
            }
        });

        isDone.postValue(false);

    }

    public void saveDriverDocs(Uri dl, Uri vehicleInsurance, Uri pan, Uri vehiclePermit,TaskCallback taskCallback){

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Drivers docs").child(mUser.getUid());
        DatabaseReference databaseReference = mRef.child("DriverId").child("Docs");

        uploadImage(storageReference, dl, databaseReference, "dlDoc", taskCallback);
        uploadImage(storageReference, vehicleInsurance, databaseReference, "vehicleInsuranceDoc", taskCallback);
        uploadImage(storageReference, pan, databaseReference, "panDoc", taskCallback);
        uploadImage(storageReference, vehiclePermit, databaseReference, "vehiclePermitDoc", taskCallback);

    }

    public LiveData<User> readUserData(String userType){
        MutableLiveData<User> mutableLiveData = new MutableLiveData<>();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Users").child(mUser.getUid()).child(userType);
        DatabaseReference databaseReference = mRef.child(userType+"Id");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                mutableLiveData.setValue(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return mutableLiveData;
    }

    public void updateUserData(User user, Uri imgUri, String userType, TaskCallback taskCallback){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Users and drivers profile pics").child(mUser.getUid());
        DatabaseReference databaseReference = mRef.child(userType+"Id");

        databaseReference.setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            // Update was successful
                            if(!user.getUserPfp().toString().equals(imgUri.toString())){
                                uploadImage(storageReference, imgUri, databaseReference,"userPfp", taskCallback);
                            }
                            else {
                                taskCallback.onSuccess();
                            }
                        }else{
                            taskCallback.onFailure(task.getException().getMessage());
                        }
                    }

                });
    }


    public void uploadImage(StorageReference storageReference, Uri imageUri, DatabaseReference reference, String dirName, TaskCallback taskCallback) {

        StorageReference fileReference = storageReference.child(UUID.randomUUID().toString());

        fileReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if(task.isSuccessful()){
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    reference.child(dirName).setValue(String.valueOf(uri));
                                    taskCallback.onSuccess();
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
                else {
                    taskCallback.onFailure("Picture upload failed!");
                    taskCallback.onSuccess();
                }

            }
        });
    }
}
