package com.example.positocabs.Views.MainScreen.RiderMain.Fragment.BottomSheetFrag.DriverAdapter;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.positocabs.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class DriversViewHolder extends RecyclerView.ViewHolder {

    CircleImageView pfp;
    TextView name,rating,price;
    LinearLayout driverLayout;


    public DriversViewHolder(@NonNull View itemView) {
        super(itemView);

        pfp=itemView.findViewById(R.id.driver_img);
        name=itemView.findViewById(R.id.driver_name);
        rating=itemView.findViewById(R.id.driver_rating);
        price=itemView.findViewById(R.id.driver_price);
        driverLayout=itemView.findViewById(R.id.driver_layout);
    }
}
