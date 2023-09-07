package com.example.positocabs.ViewModel;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.positocabs.Models.User;
import com.example.positocabs.Repository.SaveUserDataRepo;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class SaveUserDataViewModel extends AndroidViewModel {

    private SaveUserDataRepo saveUserDataRepo;
    private MutableLiveData<Boolean> isDone;

    public SaveUserDataViewModel(Application application) {
        super(application);
        this.saveUserDataRepo = new SaveUserDataRepo(application);
        this.isDone = new MutableLiveData<>();
    }
    public void saveUserData(String userType, String name, String email, String gender, String dob, Uri imageUri){
        saveUserDataRepo.saveUserData(userType,name,email,gender,dob,imageUri);
        isDone=saveUserDataRepo.getIsDone();

    }

    public void saveDriverDocs(Uri dl, Uri vehicleInsurance, Uri pan, Uri vehiclePermit){
        saveUserDataRepo.saveDriverDocs(dl,vehicleInsurance,pan,vehiclePermit);
        isDone=saveUserDataRepo.getIsDone();
    }

    public LiveData<User> readUserData(String userType){
        return saveUserDataRepo.readUserData(userType);
    }

    public MutableLiveData<Boolean> getIsDone() {
        return isDone;
    }
}
