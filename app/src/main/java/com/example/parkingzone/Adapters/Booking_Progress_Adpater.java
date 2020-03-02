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

public class Booking_Progress_Adpater extends RecyclerView.Adapter<Booking_Progress_Adpater.MyViewHolder> {

    private Context context;
    private ArrayList<CheckOutDetails> checkOutDetails;


    public Booking_Progress_Adpater(Context context, ArrayList<CheckOutDetails> checkOutDetails) {
        this.context = context;
        this.checkOutDetails = checkOutDetails;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.booking_progress_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.date_time.setText(checkOutDetails.get(position).getCurrent_date());
        holder.final_time.setText(checkOutDetails.get(position).getFinal_time());
        holder.amount.setText(checkOutDetails.get(position).getTotal_amount());

    }

    @Override
    public int getItemCount() {
        return checkOutDetails.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView date_time, final_time, amount;

        MyViewHolder(View itemView) {
            super(itemView);
            date_time = itemView.findViewById(R.id.time);
            final_time = itemView.findViewById(R.id.final_time);
            amount = itemView.findViewById(R.id.amount);
        }
    }
}
