package com.example.parkingzone;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ParkingLot extends AppCompatActivity implements View.OnClickListener {

    ViewGroup slot_layout;
    String seats = "__U_U_U_U_U_U/"
            + "_/"
            + "__U_U_A_U_U_U/"
            + "__/"
            + "__U_P_U_U_U_U/"
            + "_/"
            + "__U_U_U_U_U_U/"
            + "_/"
            + "__U_U_U_B_U_U/"
            + "_/"
            + "__U_U_U_U_U_U/"
            + "_/"
            + "__U_U_U_U_U_U/"
            + "_/";
    List<TextView> seatViewList = new ArrayList<>();
    int width = 40;
    int height = 35;
    int STATUS_UNSELECTED = 1;
    int STATUS_SELECTED = 2;
    int STATUS_BOOKED = 3;
    int STATUS_PAID = 4;
    String selectedIds = "";
    int flag = 0;

    CardView card_view;
    ImageView bringUp,back;
    Button book;
    TextView place_name,parking_no;
    Dialog timeDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_lot);
        initialise();
        displayLayout();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ParkingLot.this, "Booking Failed", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ParkingLot.this,HomePage.class));
                finish();
            }
        });
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                display_Time();
            }
        });
    }

    private void display_Time() {
        Button checkout;
        final TextView start_time,end_time,difference_time;
        timeDialog.setContentView(R.layout.time_registration);
        checkout = timeDialog.findViewById(R.id.checkout);
        start_time = timeDialog.findViewById(R.id.start_time);
        end_time = timeDialog.findViewById(R.id.end_time);
        difference_time = timeDialog.findViewById(R.id.difference);

        timeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timeDialog.show();

        start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ParkingLot.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        displayTime(hourOfDay,minute,start_time);
                    }
                },0,0,false);
                timePickerDialog.show();
            }
        });
        end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ParkingLot.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        displayTime(hourOfDay,minute,end_time);
                    }
                },0,0,false);
                timePickerDialog.show();
            }
        });
        end_time.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String final_time = differenceBetweenTime(start_time.getText().toString(),end_time.getText().toString());
                    Toast.makeText(ParkingLot.this, final_time, Toast.LENGTH_LONG).show();
                    difference_time.setText(final_time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(start_time.getText().equals("00 : 00") | end_time.getText().equals("00 : 00"))
                    Toast.makeText(ParkingLot.this, "Please specify the time", Toast.LENGTH_SHORT).show();
                else{
                    Toast.makeText(ParkingLot.this, "Success", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(ParkingLot.this,CheckOutPage.class));
//                    finish();
                    timeDialog.dismiss();
                }
            }

        });
    }
    private String differenceBetweenTime(String start_Time, String end_Time) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
        Date date1 = simpleDateFormat.parse(start_Time);
        Date date2 = simpleDateFormat.parse(end_Time);
        long difference = date2.getTime() - date1.getTime();
        long days = (int) (difference / (1000*60*60*24));
        long hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
        long min = (int) (difference - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);
        hours = (hours < 0 ? -hours : hours);
        return hours+":"+min;
    }
    private void initialise() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        slot_layout = findViewById(R.id.slots_layout);
        card_view = findViewById(R.id.card_view_scroll);
        bringUp = findViewById(R.id.drag);
        back = findViewById(R.id.back);
        seats = "/" + seats;
        book = findViewById(R.id.book);
        place_name = findViewById(R.id.lot_name);
        parking_no = findViewById(R.id.seat_no);
        timeDialog = new Dialog(this);

    }
    private void displayLayout() {
        LinearLayout layoutSeat = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutSeat.setOrientation(LinearLayout.VERTICAL);
        layoutSeat.setLayoutParams(params);
        slot_layout.addView(layoutSeat);
        LinearLayout layout = null;
        int count = 0;
//        U = Not Selected Border
//        A = Selected gray color
//        P = Your Booked Green Color
//        B = Already Booked Red Color

        for (int index = 0; index < seats.length(); index++){
            if (seats.charAt(index) == '/') {
                layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.HORIZONTAL);
                layoutSeat.addView(layout);
            }else if(seats.charAt(index) == 'U'){
                count++;
                TextView view = new TextView(this);
                view.setId(count);
                view.setGravity(Gravity.CENTER);
                view.setBackgroundResource(R.drawable.unselected_parking);
                view.setTextColor(Color.WHITE);
                view.setTag(STATUS_UNSELECTED);
                view.setText("A"+count + "");
                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
                layout.addView(view);
                seatViewList.add(view);
                view.setOnClickListener(this);
            }else if (seats.charAt(index) == '_') {
                TextView view = new TextView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
                view.setLayoutParams(layoutParams);
                view.setBackgroundColor(Color.TRANSPARENT);
                view.setText("");
                layout.addView(view);
            } else if (seats.charAt(index) == 'A') {
                count++;
                TextView view = new TextView(this);
                view.setId(count);
                view.setGravity(Gravity.CENTER);
                view.setBackgroundResource(R.drawable.selected_parking);
                view.setTextColor(Color.WHITE);
                view.setTag(STATUS_SELECTED);
                view.setText("A"+count+"");
                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
                layout.addView(view);
                seatViewList.add(view);
                view.setOnClickListener(this);
            }else if (seats.charAt(index) == 'P') {
                count++;
                TextView view = new TextView(this);
                view.setId(count);
                view.setGravity(Gravity.CENTER);
                view.setBackgroundResource(R.drawable.paid_parking);
                view.setTextColor(Color.WHITE);
                view.setTag(STATUS_PAID);
                view.setText("A"+count+"");
                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
                layout.addView(view);
                seatViewList.add(view);
                view.setOnClickListener(this);
            }else if (seats.charAt(index) == 'B') {
                count++;
                TextView view = new TextView(this);
                view.setId(count);
                view.setGravity(Gravity.CENTER);
                view.setBackgroundResource(R.drawable.booked_parking);
                view.setTextColor(Color.WHITE);
                view.setTag(STATUS_BOOKED);
                view.setText("A"+count);
                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
                layout.addView(view);
                seatViewList.add(view);
                view.setOnClickListener(this);
            }
        }
    }

    public void displayTime(int hourOfDay,int minute,TextView editText){
        int hour = hourOfDay;
        int minutes = minute;
        String timeSet = "";
        if (hour > 12) {
            hour -= 12;
            timeSet = "PM";
        } else if (hour == 0) {
            hour += 12;
            timeSet = "AM";
        } else if (hour == 12){
            timeSet = "PM";
        }else{
            timeSet = "AM";
        }
        String min = "";
        if (minutes < 10)
            min = "0" + minutes ;
        else
            min = String.valueOf(minutes);
        editText.setText(new StringBuilder().append(hour).append(':').append(min ).append(" ").append(timeSet).toString());
    }
    @Override
    public void onClick(View view) {

        if((int)view.getTag() == STATUS_UNSELECTED) {
                if (selectedIds.contains(view.getId() + ",") & flag == 0) {
                    flag = 2;
                    selectedIds = selectedIds.replace(+view.getId() + ",", "");
                    view.setBackgroundResource(R.drawable.paid_parking);
                    card_view.setVisibility(View.GONE);
                }else if(flag == 1)
                    Toast.makeText(this, "Only 1 Car Can be booked", Toast.LENGTH_SHORT).show();
                else{
                    flag = 1;
                   // selectedIds = selectedIds + view.getId() + ",";
                    view.setBackgroundResource(R.drawable.selected_parking);
                    card_view.setVisibility(View.VISIBLE);
                    parking_no.setText("A"+view.getId());
                }
            } else if ((int) view.getTag() == STATUS_BOOKED) {
                Toast.makeText(this, "Seat " + view.getId() + " is Booked", Toast.LENGTH_SHORT).show();
            } else if ((int) view.getTag() == STATUS_PAID) {
                Toast.makeText(this, "Seat " + view.getId() + " is Reserved", Toast.LENGTH_SHORT).show();
            }
        }
    }
