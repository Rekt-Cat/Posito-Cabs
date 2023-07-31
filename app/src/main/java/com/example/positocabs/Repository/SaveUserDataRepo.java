package com.example.positocabs.Repository;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import com.example.positocabs.Models.ReadWriteUserDetails;
import com.google.android.gms.auth.api.signin.internal.Storage;
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
import com.google.firebase.storage.FirebaseStorage;
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

    public void saveUserData(String userType, String name, String email, String gender, String dob, Uri imageUri) {

        mUser = mAuth.getCurrentUser();
        StorageReference storageReference1 = FirebaseStorage.getInstance().getReference("Users and drivers profile pics").child(mUser.getUid());

        //saving rider data
        if (userType.equals("Rider")) {
            readWriteUserDetails = new ReadWriteUserDetails(name, email, gender, dob);
            mRef = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid()).child("RiderId");

            mRef.setValue(readWriteUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    uploadImage(storageReference1, imageUri, mRef, "ProfilePicture");
                    FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid()).child("is"+userType).setValue(true);
                    isDone.postValue(true);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(application, "failed make info!", Toast.LENGTH_SHORT).show();
                    isDone.postValue(false);
                }
            });

            isDone.postValue(false);

        }
        else if (userType.equals("Driver")) {
            readWriteUserDetails = new ReadWriteUserDetails(name, email, gender, dob);
            mRef = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid()).child("DriverId");

            mRef.setValue(readWriteUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    uploadImage(storageReference1, imageUri, mRef, "ProfilePicture");
                    FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid()).child("is"+userType).setValue(true);
                    isDone.postValue(true);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(application, "failed make info!", Toast.LENGTH_SHORT).show();
                    isDone.postValue(false);
                }
            });

            isDone.postValue(false);

        }
        else {
            Toast.makeText(application, "Invalid UserType!", Toast.LENGTH_SHORT).show();
        }

    }

    public void saveDriverDocs(Uri dl, Uri vehicleInsurance, Uri pan, Uri vehiclePermit){

        mUser=mAuth.getCurrentUser();
        StorageReference storageReference2 = FirebaseStorage.getInstance().getReference("Drivers docs").child(mUser.getUid());
        mRef=FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid()).child("DriverId").child("Docs");

        uploadImage(storageReference2, dl, mRef, "dlDoc");
        uploadImage(storageReference2, vehicleInsurance, mRef, "vehicleInsuranceDoc");
        uploadImage(storageReference2, pan, mRef, "panDoc");
        uploadImage(storageReference2, vehiclePermit, mRef, "vehiclePermitDoc");

    }


    public void uploadImage(StorageReference storageReference, Uri imageUri, DatabaseReference reference, String dirName) {

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
                                reference.child(dirName).setValue(String.valueOf(uri));
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
