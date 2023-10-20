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

import com.example.positocabs.Models.AnimationModel;
import com.example.positocabs.Models.DriverGeoModel;
import com.example.positocabs.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Common {
    public static final int LOCATION_SERVICE_ID=175;
    public static final String ACTION_START_LOCATION_SERVICE="startLocationService";

    public static final String ACTION_STOP_LOCATION_SERVICE="stopLocationService";

    public static final int PERMISSION_REQUEST_CODE=100;
    public static final String DRIVER_LOCATION_REFERENCE="Driver's Location";
    public static final String TOKEN_REFERENCE ="Token" ;
    public static final String NOTI_TITLE = "title";
    public static final String NOTI_CONTENT = "body";
    public static final String DRIVER_INFO_REFERENCE = "Drivers";
    public static final String REQUEST_DRIVER_TITLE = "RequestDriver";
    public static final String RIDER_PICKUP_LOCATION = "PickupLocation";
    public static final String RIDER_ID = "RiderId";
    public static final String RIDER_DESTINATION_LOCATION = "DestinationLocation";
    public static final String RIDER_PICKUP_LOCATION_STRING = "PickUpLocationString";
    public static final String RIDER_DESTINATION_LOCATION_STRING = "DestinationLocationString";
    public static final String RIDER_TRIP = "RiderTrip";
    public static final String PRICE = "Price";
    public static final String DISTANCE_INT = "DistanceInt";
    public static final String DISTANCE_STRING = "DistanceString";
    public static Map<String,DriverGeoModel> driverFound= new HashMap<String,DriverGeoModel>();
    public static HashMap<String , Marker> markerList= new HashMap<>();
    public static HashMap<String, AnimationModel> driverLocationSubscribe= new HashMap<String,AnimationModel>();


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

    public static String buildName(String firstName, String lastName) {
        return new StringBuilder(firstName).append(" ").append(lastName).toString();

    }

    public static List<LatLng> decodePoly(String encoded) {
        List poly = new ArrayList();
        int index=0,len=encoded.length();
        int lat=0,lng=0;
        while(index < len)
        {
            int b,shift=0,result=0;
            do{
                b=encoded.charAt(index++)-63;
                result |= (b & 0x1f) << shift;
                shift+=5;

            }while(b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1):(result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do{
                b = encoded.charAt(index++)-63;
                result |= (b & 0x1f) << shift;
                shift +=5;
            }while(b >= 0x20);
            int dlng = ((result & 1)!=0 ? ~(result >> 1): (result >> 1));
            lng +=dlng;

            LatLng p = new LatLng((((double)lat / 1E5)),
                    (((double)lng/1E5)));
            poly.add(p);
        }
        return poly;
    }
    public static float getBearing(LatLng begin, LatLng end) {

        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }

    public static String formatDuration(String duration) {

        if(duration.contains("mins")){
            return duration.substring(0,duration.length()-1); //removes letter "s"

        }
        else{
            return duration;
        }
    }

    public static String formatAddress(String start_address) {

        int firstIndexOfComma = start_address.indexOf(",");
        return start_address.substring(0,firstIndexOfComma);//get only address
    }
}
