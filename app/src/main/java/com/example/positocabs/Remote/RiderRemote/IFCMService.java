package com.example.positocabs.Remote.RiderRemote;

import com.example.positocabs.Models.FCMResponse;
import com.example.positocabs.Models.FCMSendData;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAyPzNfmE:APA91bEOlXNqqphGBJRPzZ5nxSA-_rvB4elNeskEK94VGQ-fx9RAdlV_ZSs-urCEESsrHC45-7jLGt1i3GGo80xDC2NfTolZoDiwRo2tF9aC0hClotTkR1kM2ngQxotVdTZ7eZXisXCf"
    })
    @POST("fcm/send")
    Observable<FCMResponse> sendNotification(@Body FCMSendData Body);

}
