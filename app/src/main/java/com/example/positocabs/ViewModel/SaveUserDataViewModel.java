package com.example.positocabs.ViewModel;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.positocabs.Repository.SaveUserDataRepo;
import com.google.firebase.storage.StorageReference;

public class SaveUserDataViewModel extends AndroidViewModel {

    private SaveUserDataRepo saveUserDataRepo;
    private MutableLiveData<Boolean> isDone;

    public SaveUserDataViewModel(Application application) {
        super(application);
        this.saveUserDataRepo = new SaveUserDataRepo(application);
        this.isDone = new MutableLiveData<>();
    }
    public void saveUserData(int userType, String name, String email, String gender, String dob,
                             StorageReference storageReference, Uri imageUri){
        saveUserDataRepo.saveUserData(userType,name,email,gender,dob,storageReference,imageUri);
        isDone=saveUserDataRepo.getIsDone();

    }

    public MutableLiveData<Boolean> getIsDone() {
        return isDone;
    }
}
