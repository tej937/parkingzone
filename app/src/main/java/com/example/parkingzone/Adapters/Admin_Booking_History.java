package com.example.parkingzone.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parkingzone.R;

import java.util.ArrayList;

import javaFiles.CheckOutDetails;

public class Admin_Booking_History extends RecyclerView.Adapter<Admin_Booking_History.MyViewHolder> {
    private Context context;
    private ArrayList<CheckOutDetails> checkOutDetails;

    public Admin_Booking_History(Context context, ArrayList<CheckOutDetails> checkOutDetails) {
        this.context = context;
        this.checkOutDetails = checkOutDetails;
    }

    @NonNull
    @Override
    public Admin_Booking_History.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Admin_Booking_History.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.parking_progress_layout, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.booking_seat.setText(checkOutDetails.get(position).getParking_slot());
        holder.total_time.setText(checkOutDetails.get(position).getFinal_time());
        holder.user_name.setText(checkOutDetails.get(position).getPlate_no());
        holder.car_name.setText(checkOutDetails.get(position).getCurrent_date());
    }

    @Override
    public int getItemCount() {
        return checkOutDetails.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView booking_seat, car_name, user_name, total_time;

        public MyViewHolder(View inflate) {
            super(inflate);
            booking_seat = inflate.findViewById(R.id.booking_no);
            car_name = inflate.findViewById(R.id.car_name);
            user_name = inflate.findViewById(R.id.username);
            total_time = inflate.findViewById(R.id.total_time);
        }
    }

}
