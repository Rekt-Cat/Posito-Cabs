package com.example.positocabs.Services.DriverService;

import androidx.annotation.NonNull;

import com.example.positocabs.Models.Event.DriverRequestReceived;
import com.example.positocabs.Services.Common;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;
import java.util.Random;

public class DriverMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Map<String, String> dataRecv = message.getData();

        if (dataRecv != null) {
            if (dataRecv.get(Common.NOTI_TITLE).equals(Common.REQUEST_DRIVER_TITLE)) {
                EventBus.getDefault().postSticky(new DriverRequestReceived(

                        dataRecv.get(Common.RIDER_ID),
                        dataRecv.get(Common.RIDER_PICKUP_LOCATION),
                        dataRecv.get(Common.RIDER_DESTINATION_LOCATION)

                ));


            }
            else{
                Common.showNotification(this,
                        new Random().nextInt(),
                        dataRecv.get(Common.NOTI_TITLE),
                        dataRecv.get(Common.NOTI_CONTENT),
                        null);
            }

        }
    }

}
