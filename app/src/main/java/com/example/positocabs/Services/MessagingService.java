package com.example.positocabs.Services;


import androidx.annotation.NonNull;

import com.example.positocabs.Utils.UserUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            UserUtils.updateToken(this,token);
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
         Map<String,String> dataRecv= message.getData();
         if(dataRecv!=null){
            Common.showNotification(this,new Random().nextInt(),dataRecv.get(Common.NOTI_TITLE),dataRecv.get(Common.NOTI_CONTENT),null);

         }
    }

}
