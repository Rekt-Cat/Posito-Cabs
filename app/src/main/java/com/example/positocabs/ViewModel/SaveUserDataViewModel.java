package com.example.positocabs.ViewModel;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.positocabs.Callback.TaskCallback;
import com.example.positocabs.Models.DataModel.DriverDoc;
import com.example.positocabs.Models.DataModel.User;
import com.example.positocabs.Repository.SaveUserDataRepo;

public class SaveUserDataViewModel extends AndroidViewModel {

    private SaveUserDataRepo saveUserDataRepo;

    public SaveUserDataViewModel(Application application) {
        super(application);
        this.saveUserDataRepo = new SaveUserDataRepo(application);
    }
    public void saveUserData(User user, String userType, Uri imageUri, TaskCallback taskCallback){
        saveUserDataRepo.saveUserData(user,userType,imageUri,taskCallback);

    }

    public void saveDriverDocs(DriverDoc driverDoc, TaskCallback taskCallback){
        saveUserDataRepo.saveDriverDocs(driverDoc,taskCallback);
    }

    public void updateUserData(User user, Uri imgUri, String userType, TaskCallback taskCallback){
        saveUserDataRepo.updateUserData(user,imgUri,userType,taskCallback);
    }

    public LiveData<User> readUserData(String userType){
        return saveUserDataRepo.readUserData(userType);
    }

    public LiveData<DriverDoc> readDriverDoc(){
        return saveUserDataRepo.readDriverDoc();
    }
}
