package com.example.positocabs.Services;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.positocabs.R;

public class Common {
    public static final int LOCATION_SERVICE_ID=175;
    public static final String ACTION_START_LOCATION_SERVICE="startLocationService";
    public static final String ACTION_STOP_LOCATION_SERVICE="stopLocationService";
    public static final int PERMISSION_REQUEST_CODE=100;
    public static final String DRIVER_LOCATION_REFERENCE="Driver's Location";
    public static final String TOKEN_REFERENCE ="Token" ;
    public static final String NOTI_TITLE = "title";
    public static final String NOTI_CONTENT = "body";


    public static void showNotification(Context context, int id, String title, String body, Intent intent) {
        PendingIntent pendingIntent=null;
        if(pendingIntent!=null){
            pendingIntent=PendingIntent.getActivity(context,id,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        }
        String NOTIFICATION_CHANNEL_ID="Posito_Cabs_noti_channel";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel notificationChannel= new NotificationChannel(NOTIFICATION_CHANNEL_ID,"Posito Cabs",NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Posito Cabs");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder= new NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID);

        builder.setContentTitle(title);
        builder.setContentText(body).setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_MAX).setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.drawable.baseline_directions_car_24)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.baseline_directions_car_24));
        if(pendingIntent!=null){
                builder.setContentIntent(pendingIntent);

        }
        Notification notification= builder.build();
        notificationManager.notify(id,notification);

    }
}
