package com.example.parkingzone.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parkingzone.ParkingLot;
import com.example.parkingzone.R;

import java.util.ArrayList;

import javaFiles.NewOwner;

public class Owner_Adapters extends RecyclerView.Adapter<Owner_Adapters.MyViewHolder> {

    private Context context;
    private ArrayList<NewOwner> newOwners;
    private String layout;
    private String nam;


    public Owner_Adapters(Context context, ArrayList<NewOwner> newOwners) {
        this.context = context;
        this.newOwners = newOwners;
    }

    @NonNull
    @Override
    public Owner_Adapters.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.owner_adapter, parent, false);
        return new Owner_Adapters.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.mall_name.setText(newOwners.get(position).getParking_name());
        holder.location_name.setText(newOwners.get(position).getArea());
        nam = newOwners.get(position).getParking_name();
        holder.layout = newOwners.get(position).getLayout1();
        layout = holder.layout;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ParkingLot.class);
                intent.putExtra("layout", layout);
                intent.putExtra("position", String.valueOf(position + 1));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newOwners.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mall_name, location_name;
        String layout;

        MyViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            mall_name = itemView.findViewById(R.id.mall_name);
            location_name = itemView.findViewById(R.id.mall_location);
        }
    }
}
