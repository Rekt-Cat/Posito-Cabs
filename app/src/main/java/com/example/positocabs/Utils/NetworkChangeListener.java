package com.example.positocabs.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

import com.example.positocabs.Callback.RegisterDriverOnline;
import com.example.positocabs.Callback.StartMainActivity;
import com.example.positocabs.R;
import com.example.positocabs.Services.Common;
import com.example.positocabs.Views.MainScreen.DriverMain.DriverMainActivity;
import com.example.positocabs.Views.SplashScreen.SplashScreen;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class NetworkChangeListener extends BroadcastReceiver {
    public StartMainActivity startMainActivity;
    public RegisterDriverOnline registerDriverOnline;

    public String carType;

    public NetworkChangeListener() {
    }

    public NetworkChangeListener(String carType) {
        this.carType = carType;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("reggzz", "car type is : "+carType);
        if(context instanceof SplashScreen){
            startMainActivity=(SplashScreen)context;
        }
        else if(context instanceof DriverMainActivity){
            registerDriverOnline=(DriverMainActivity)context;
        }

        if (!Common.isConnectedToInternet(context)) {


            View layout_dialogue = LayoutInflater.from(context).inflate(R.layout.check_internet_alert_box, null);
            AppCompatButton retry = layout_dialogue.findViewById(R.id.internet_retry);


            androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(context,R.style.MyRounded_MaterialComponents_MaterialAlertDialog)
                    .setView(layout_dialogue).show();
            dialog.show();
            dialog.setCancelable(false);
            dialog.getWindow().setGravity(Gravity.CENTER);

            retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    onReceive(context, intent);
                }
            });
        }
        else{
            if(context instanceof SplashScreen) {
                Log.d("reggz", "splash fun");
                startMainActivity.startMainIfConnected();
            }
            else if(context instanceof DriverMainActivity){
                Log.d("reggz", "driver fun");
                registerDriverOnline.InitDriverOnlineSystem(carType);
            }
        }
    }
}
