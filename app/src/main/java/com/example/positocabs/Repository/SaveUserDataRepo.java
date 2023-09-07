package com.example.positocabs.Repository;

import android.app.Application;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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

import java.util.List;
import java.util.UUID;


public class SaveUserDataRepo {
    private Application application;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

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
        mUser = mAuth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());
    }

    public void saveUserData(String userType, String name, String email, String gender, String dob, Uri imageUri) {

        mUser = mAuth.getCurrentUser();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Users and drivers profile pics").child(mUser.getUid());
        DatabaseReference databaseReference = mRef.child(userType+"Id");
        User user = new User(name, email, gender, dob);

        databaseReference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                uploadImage(storageReference, imageUri, databaseReference, "userPfp");
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

    public void saveDriverDocs(Uri dl, Uri vehicleInsurance, Uri pan, Uri vehiclePermit){

        mUser=mAuth.getCurrentUser();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Drivers docs").child(mUser.getUid());
        DatabaseReference databaseReference = mRef.child("DriverId").child("Docs");

        uploadImage(storageReference, dl, databaseReference, "dlDoc");
        uploadImage(storageReference, vehicleInsurance, databaseReference, "vehicleInsuranceDoc");
        uploadImage(storageReference, pan, databaseReference, "panDoc");
        uploadImage(storageReference, vehiclePermit, databaseReference, "vehiclePermitDoc");

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
