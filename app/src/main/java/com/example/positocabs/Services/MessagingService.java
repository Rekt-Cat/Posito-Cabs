package com.example.positocabs.Services;


import android.util.Log;

import androidx.annotation.NonNull;

import com.example.positocabs.Models.Event.DriverRequestReceived;
import com.example.positocabs.Utils.UserUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;
import java.util.Random;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            UserUtils.updateToken(this, token);
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Map<String, String> dataRecv = message.getData();

        if (!dataRecv.isEmpty()) {
            if (dataRecv.get(Common.NOTI_TITLE).equals(Common.REQUEST_DRIVER_TITLE)) {

                Log.d("wowo", "in if");
                DriverRequestReceived driverRequestReceived= new DriverRequestReceived();
                driverRequestReceived.setKey(dataRecv.get(Common.RIDER_ID));
                driverRequestReceived.setDropLocation(dataRecv.get(Common.RIDER_DESTINATION_LOCATION));
                driverRequestReceived.setDropLocationString( dataRecv.get(Common.RIDER_DESTINATION_LOCATION_STRING));
                driverRequestReceived.setPickupLocation(dataRecv.get(Common.RIDER_PICKUP_LOCATION));
                driverRequestReceived.setPickupLocationString(dataRecv.get(Common.RIDER_PICKUP_LOCATION_STRING));
                driverRequestReceived.setDistanceInt(dataRecv.get(Common.DISTANCE_INT));
                driverRequestReceived.setDistanceString(dataRecv.get(Common.DISTANCE_STRING));


                EventBus.getDefault().postSticky(driverRequestReceived);


            }
            else{
                Log.d("wowo", "in else");
                Common.showNotification(this,
                        new Random().nextInt(),
                        dataRecv.get(Common.NOTI_TITLE),
                        dataRecv.get(Common.NOTI_CONTENT),
                        null);
            }

        }
    }

}
