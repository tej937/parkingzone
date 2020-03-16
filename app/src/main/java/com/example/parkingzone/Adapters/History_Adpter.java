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

public class History_Adpter extends RecyclerView.Adapter<History_Adpter.MyViewHolder> {

    private Context context;
    private ArrayList<CheckOutDetails> checkOutDetails;

    public History_Adpter(Context context, ArrayList<CheckOutDetails> checkOutDetails) {
        this.context = context;
        this.checkOutDetails = checkOutDetails;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.history_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.date_time.setText(checkOutDetails.get(position).getCurrent_date());
        holder.final_time.setText(checkOutDetails.get(position).getFinal_time());
        holder.amount.setText(checkOutDetails.get(position).getTotal_amount());
        holder.location.setText(checkOutDetails.get(position).getLocation());
    }

    @Override
    public int getItemCount() {
        return checkOutDetails.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView date_time, final_time, amount, location;

        MyViewHolder(View itemView) {
            super(itemView);
            date_time = itemView.findViewById(R.id.time);
            final_time = itemView.findViewById(R.id.final_time);
            amount = itemView.findViewById(R.id.amount);
            location = itemView.findViewById(R.id.location);
        }
    }
}
