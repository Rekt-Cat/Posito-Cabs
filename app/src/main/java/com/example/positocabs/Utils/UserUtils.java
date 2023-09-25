package com.example.positocabs.Utils;

import android.content.Context;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.example.positocabs.Models.DriverGeoModel;
import com.example.positocabs.Models.FCMResponse;
import com.example.positocabs.Models.FCMSendData;
import com.example.positocabs.Models.TokenModel;
import com.example.positocabs.R;
import com.example.positocabs.Remote.RiderRemote.IFCMService;
import com.example.positocabs.Remote.RiderRemote.RetrofitFCMClient;
import com.example.positocabs.Services.Common;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UserUtils {

    public static void updateToken(Context context, String token) {
        TokenModel tokenModel= new TokenModel(token);
        FirebaseDatabase.getInstance().getReference().child(Common.TOKEN_REFERENCE).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(tokenModel)
                .addOnFailureListener(e ->
                        Log.d("hase", ""+e.getMessage())).addOnSuccessListener(unused -> {
                });


    }

    public static void sendRequest(Context context, LinearLayout layout, String key, DriverGeoModel value, LatLng target,LatLng destination) {

        CompositeDisposable compositeDisposable= new CompositeDisposable();
        IFCMService ifcmService= RetrofitFCMClient.getInstance().create(IFCMService.class);


        FirebaseDatabase.getInstance().getReference(Common.TOKEN_REFERENCE).child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    TokenModel tokenModel= snapshot.getValue(TokenModel.class);
                    Map<String,String> notificationData= new HashMap<>();
                    notificationData.put(Common.NOTI_TITLE,Common.REQUEST_DRIVER_TITLE);
                    notificationData.put(Common.RIDER_ID,FirebaseAuth.getInstance().getCurrentUser().getUid());
                    notificationData.put(Common.NOTI_CONTENT,"This Message represents for request driver location!");
                    notificationData.put(Common.RIDER_PICKUP_LOCATION, new StringBuilder("")
                            .append(target.latitude)
                            .append(",")
                            .append(target.longitude)
                            .toString());
//                    notificationData.put(Common.RIDER_DESTINATION_LOCATION, new StringBuilder("")
//                            .append(destination.latitude)
//                            .append(",")
//                            .append(destination.longitude)
//                            .toString());
                    FCMSendData fcmSendData = new FCMSendData(tokenModel.getToken(),notificationData);
                    compositeDisposable.add(ifcmService.sendNotification(fcmSendData)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<FCMResponse>() {
                                @Override
                                public void accept(FCMResponse fcmResponse) throws Throwable {
                                    if(fcmResponse.getSuccess()==0){
                                        compositeDisposable.clear();
                                        Snackbar.make(layout, context.getString(R.string.request_driver_failed),Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Throwable {
                                    Log.d("tokenKey", "yolo called");
                                    compositeDisposable.clear();
                                    Snackbar.make(layout, throwable.getMessage(),Snackbar.LENGTH_LONG).show();
                                    Log.e("throwError", ""+throwable.getMessage());
                                }
                            }));





                }
                else{
                    Log.d("tokenKey", "else called");
                    Snackbar.make(layout, context.getString(R.string.token_not_found),Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(layout, error.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
