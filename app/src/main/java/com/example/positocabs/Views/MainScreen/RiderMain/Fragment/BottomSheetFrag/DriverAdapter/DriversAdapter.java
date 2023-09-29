package com.example.positocabs.Views.MainScreen.RiderMain.Fragment.BottomSheetFrag.DriverAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.positocabs.Models.DataModel.User;
import com.example.positocabs.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DriversAdapter extends RecyclerView.Adapter<DriversViewHolder> {

    private Context context;
    private List<User> driverList;
    private OnItemClickListener clickListener;

    public DriversAdapter(Context context, List<User> driverList, OnItemClickListener clickListener) {
        this.context = context;
        this.driverList = driverList;
        this.clickListener = clickListener;

    }

    @NonNull
    @Override
    public DriversViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DriversViewHolder(LayoutInflater.from(context).inflate(R.layout.driver_info_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull DriversViewHolder holder, int position) {
        holder.name.setText(driverList.get(position).getName());
        holder.rating.setText(String.valueOf(driverList.get(position).getRating()));
        setLocalPicture(holder, driverList.get(position).getUserPfp());

        holder.driverLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onItemClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return driverList.size();
    }

    // Interface to define the click listener
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private void setLocalPicture(DriversViewHolder holder,String uri){

        Picasso.get()
                .load(uri)
                .error(R.drawable.default_pfp_ico)
                .into(holder.pfp);

    }
}
