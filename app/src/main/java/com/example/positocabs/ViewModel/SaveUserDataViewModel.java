package com.example.positocabs.ViewModel;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.positocabs.Callback.TaskCallback;
import com.example.positocabs.Models.User;
import com.example.positocabs.Repository.SaveUserDataRepo;

public class SaveUserDataViewModel extends AndroidViewModel {

    private SaveUserDataRepo saveUserDataRepo;
    private MutableLiveData<Boolean> isDone;

    public SaveUserDataViewModel(Application application) {
        super(application);
        this.saveUserDataRepo = new SaveUserDataRepo(application);
        this.isDone = new MutableLiveData<>();
    }
    public void saveUserData(User user, String userType, Uri imageUri, TaskCallback taskCallback){
        saveUserDataRepo.saveUserData(user,userType,imageUri,taskCallback);
        isDone=saveUserDataRepo.getIsDone();

    }

    public void saveDriverDocs(Uri dl, Uri vehicleInsurance, Uri pan, Uri vehiclePermit, TaskCallback taskCallback){
        saveUserDataRepo.saveDriverDocs(dl,vehicleInsurance,pan,vehiclePermit,taskCallback);
        isDone=saveUserDataRepo.getIsDone();
    }

    public void updateUserData(User user, Uri imgUri, String userType, TaskCallback taskCallback){
        saveUserDataRepo.updateUserData(user,imgUri,userType,taskCallback);
    }

    public LiveData<User> readUserData(String userType){
        return saveUserDataRepo.readUserData(userType);
    }

    public MutableLiveData<Boolean> getIsDone() {
        return isDone;
    }
}
