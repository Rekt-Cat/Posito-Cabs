package com.example.positocabs.Utils;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

import com.example.positocabs.Callback.StartMainActivity;
import com.example.positocabs.R;
import com.example.positocabs.Services.Common;
import com.example.positocabs.Views.SplashScreen.SplashScreen;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class NetworkChangeListener extends BroadcastReceiver {
    public StartMainActivity startMainActivity;

    @Override
    public void onReceive(Context context, Intent intent) {
        startMainActivity=(SplashScreen)context;
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
            startMainActivity.startMainIfConnected();
        }
    }
}
