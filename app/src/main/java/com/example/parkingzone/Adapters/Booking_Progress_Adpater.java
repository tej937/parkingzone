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
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.request_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.start_time.setText(checkOutDetails.get(position).getStart_time());
        holder.end_time.setText(checkOutDetails.get(position).getEnd_time());
        holder.seat_no.setText(checkOutDetails.get(position).getParking_slot());
        holder.car_number.setText(checkOutDetails.get(position).getPlate_no());
        //Toast.makeText(context, "Please Book "+holder.seat_no.getText()+"Thank you", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return checkOutDetails.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView start_time, end_time, seat_no, car_number;
        MyViewHolder(View itemView) {
            super(itemView);
            start_time = itemView.findViewById(R.id.start_time);
            end_time = itemView.findViewById(R.id.end_time);
            seat_no = itemView.findViewById(R.id.seat_no);
            car_number = itemView.findViewById(R.id.plate_no);
        }
    }
}
