package com.example.parkingzone;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javaFiles.CheckOutDetails;
import javaFiles.NewCar;
import javaFiles.NewOwner;

public class ParkingLot extends AppCompatActivity implements View.OnClickListener {

    ViewGroup slot_layout;
    //    String seats = "__U_U_U_U_U_U/"
//            + "_/"
//            + "__U_U_A_U_U_U/"
//            + "__/"
//            + "__U_P_U_U_U_U/"
//            + "_/"
//            + "__U_U_U_U_U_U/"
//            + "_/"
//            + "__U_U_U_B_U_U/"
//            + "_/"
//            + "__U_U_U_U_U_U/"
//            + "_/"
//            + "__U_U_U_U_U_U/"
//            + "_/";
    String complete_layout = "";

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
    ImageView back;
    Button book;
    TextView place_name, parking_no, lot_name;
    Dialog timeDialog;
    CheckOutDetails checkOutDetails;
    NewCar newCar;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReferenceFromUrl("https://parking-zone-8ce19.firebaseio.com");
    long maxId = 0;
    NewOwner newOwner;
    private String seatNo;
    // long max = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_lot);
        initialise();
        getBookingNo();
        Intent intent = getIntent();
        complete_layout = intent.getStringExtra("layout");
        String nam = intent.getStringExtra("position");
        String area = intent.getStringExtra("area");
        //Toast.makeText(this, ""+area, Toast.LENGTH_SHORT).show();

        final DatabaseReference reference = ref.child(area).child("OwnerInfo").child(nam);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    getOwnerDataBaseFromFirebase(dataSnapshot);
                } else {
                    Toast.makeText(ParkingLot.this, "No Layout Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ParkingLot.this, "A"+nam, Toast.LENGTH_LONG).show();
                startActivity(new Intent(ParkingLot.this,HomePage.class));
                finish();
            }
        });
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seatNo = parking_no.getText().toString();
                checkOutDetails.setParking_slot(parking_no.getText().toString());
                display_Time();
            }
        });

    }

    private void getBookingNo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final DatabaseReference bookingId = ref.child("New Booking").child(user.getUid());
            bookingId.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                        maxId = dataSnapshot.getChildrenCount();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }

    }

    private void getOwnerDataBaseFromFirebase(DataSnapshot dataSnapshot) {
        newOwner.setLayout1((String) dataSnapshot.child("layout1").getValue());
        newOwner.setParking_name((String) dataSnapshot.child("parking_name").getValue());
        newOwner.setArea((String) dataSnapshot.child("area").getValue());
        newOwner.setAddress((String) dataSnapshot.child("address").getValue());
        retrieveCarData();
        complete_layout = newOwner.getLayout1();
        lot_name.setText(newOwner.getParking_name());
        displayLayout();
        newOwner.setLayout1(null);
    }

    private void retrieveCarData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final DatabaseReference usersRef = ref.child("Parker").child(user.getUid()).child("Vehicle Details");
            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                        getCarDataFromFirebase(dataSnapshot);
                    else
                        Toast.makeText(ParkingLot.this, "Please Register your Car ", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ParkingLot.this, "U have failed this City " + databaseError, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getCarDataFromFirebase(DataSnapshot dataSnapshot) {
        newCar.setPlate((String) dataSnapshot.child("plate").getValue());
    }


    private void display_Time() {
        final Button checkout;
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
                    difference_time.setText(final_time);
                    checkOutDetails.setFinal_time(final_time);
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
                else if (difference_time.getText().equals("Please Set time difference within 20 HOURS") | difference_time.getText().equals("Please set time difference more than 30 min"))
                    Toast.makeText(ParkingLot.this, "You Have An error please complete that", Toast.LENGTH_SHORT).show();
                else{
                    //Toast.makeText(ParkingLot.this, "Success", Toast.LENGTH_SHORT).show();
                    checkOutDetails.setStart_time(start_time.getText().toString());
                    checkOutDetails.setEnd_time(end_time.getText().toString());
                    checkOutDetails.setLocation(newOwner.getArea());
                    checkOutDetails.setPayment_status("fail");
                    checkOutDetails.setAddress(newOwner.getAddress());
                    checkOutDetails.setPlace_name(newOwner.getParking_name());
                    checkOutDetails.setSeatNo(Integer.parseInt(seatNo.substring(1)));
                    checkOutDetails.setPlate_no(newCar.getPlate());
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM");
                    String currentDate = sdf.format(new Date());
                    checkOutDetails.setCurrent_date(currentDate);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        final DatabaseReference usersRef = ref.child("New Booking").child(user.getUid()).child(String.valueOf(maxId + 1));
                        usersRef.setValue(checkOutDetails);
                        final DatabaseReference request = ref.child(newOwner.getParking_name()).child("Request Generated").child(user.getUid());
                        request.setValue(checkOutDetails);
                        startActivity(new Intent(ParkingLot.this, CheckOutPage.class));
                        timeDialog.dismiss();
                        finish();
                        //Toast.makeText(ParkingLot.this, "Booking completed Please complete payment", Toast.LENGTH_SHORT).show();
                    }
//                   timeDialog.dismiss();
                }
            }


        });
    }
    private String differenceBetweenTime(String start_Time, String end_Time) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
        Date date1 = simpleDateFormat.parse(start_Time);
        Date date2 = simpleDateFormat.parse(end_Time);
        if (date2.before(date1))
            return "Please Set time difference within 20 HOURS";
        else {
            long difference = date2.getTime() - date1.getTime();

            long days = (int) (difference / (1000 * 60 * 60 * 24));
            long hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
            long min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
            hours = (hours < 0 ? -hours : hours);
            if (min < 30 & hours == 0)
                return "Please set time difference more than 30 min";
            else {
                calculatePrice(hours, min);
                return hours + ":" + min;
            }
        }
    }
    private void initialise() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        slot_layout = findViewById(R.id.slots_layout);
        card_view = findViewById(R.id.card_view_scroll);
        back = findViewById(R.id.back);
        // seats = "/" + seats;
        book = findViewById(R.id.book);
        place_name = findViewById(R.id.lot_name);
        parking_no = findViewById(R.id.seat_no);
        lot_name = findViewById(R.id.username);
        timeDialog = new Dialog(this);
        checkOutDetails = new CheckOutDetails();
        newOwner = new NewOwner();
        newCar = new NewCar();
    }
    //Original Display of layout
//    private void displayLayout() {
//        LinearLayout layoutSeat = new LinearLayout(this);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutSeat.setOrientation(LinearLayout.VERTICAL);
//        layoutSeat.setLayoutParams(params);
//        slot_layout.addView(layoutSeat);
//        LinearLayout layout = null;
//        int count = 0;
////        U = Not Selected Border
////        A = Selected gray color
////        P = Your Booked Green Color
////        B = Already Booked Red Color
//
//        for (int index = 0; index < seats.length(); index++){
//            if (seats.charAt(index) == '/') {
//                layout = new LinearLayout(this);
//                layout.setOrientation(LinearLayout.HORIZONTAL);
//                layoutSeat.addView(layout);
//            }else if(seats.charAt(index) == 'U'){
//                count++;
//                TextView view = new TextView(this);
//                view.setId(count);
//                view.setGravity(Gravity.CENTER);
//                view.setBackgroundResource(R.drawable.unselected_parking);
//                view.setTextColor(Color.WHITE);
//                view.setTag(STATUS_UNSELECTED);
//                view.setText("A"+count + "");
//                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
//                layout.addView(view);
//                seatViewList.add(view);
//                view.setOnClickListener(this);
//            }else if (seats.charAt(index) == '_') {
//                TextView view = new TextView(this);
//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
//                view.setLayoutParams(layoutParams);
//                view.setBackgroundColor(Color.TRANSPARENT);
//                view.setText("");
//                layout.addView(view);
//            } else if (seats.charAt(index) == 'A') {
//                count++;
//                TextView view = new TextView(this);
//                view.setId(count);
//                view.setGravity(Gravity.CENTER);
//                view.setBackgroundResource(R.drawable.selected_parking);
//                view.setTextColor(Color.WHITE);
//                view.setTag(STATUS_SELECTED);
//                view.setText("A"+count+"");
//                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
//                layout.addView(view);
//                seatViewList.add(view);
//                view.setOnClickListener(this);
//            }else if (seats.charAt(index) == 'P') {
//                count++;
//                TextView view = new TextView(this);
//                view.setId(count);
//                view.setGravity(Gravity.CENTER);
//                view.setBackgroundResource(R.drawable.paid_parking);
//                view.setTextColor(Color.WHITE);
//                view.setTag(STATUS_PAID);
//                view.setText("A"+count+"");
//                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
//                layout.addView(view);
//                seatViewList.add(view);
//                view.setOnClickListener(this);
//            }else if (seats.charAt(index) == 'B') {
//                count++;
//                TextView view = new TextView(this);
//                view.setId(count);
//                view.setGravity(Gravity.CENTER);
//                view.setBackgroundResource(R.drawable.booked_parking);
//                view.setTextColor(Color.WHITE);
//                view.setTag(STATUS_BOOKED);
//                view.setText("A"+count);
//                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
//                layout.addView(view);
//                seatViewList.add(view);
//                view.setOnClickListener(this);
//            }
//        }
//    }
    //New Display of layout
    public void displayLayout() {
        LinearLayout layoutSeat = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutSeat.setOrientation(LinearLayout.VERTICAL);
        layoutSeat.setLayoutParams(params);
        slot_layout.addView(layoutSeat);
        LinearLayout layout = null;
        int count = 0;

//        U = Not Selected Border
//        A = Selected gray color
//        B = Already Booked Red Color

        for (int index = 0; index < this.complete_layout.length(); index++) {
            if (this.complete_layout.charAt(index) == '/') {
                layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.HORIZONTAL);
                //
                TextView view1 = new TextView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
                layoutParams.setMargins(0, 100, 0, 0);
                view1.setLayoutParams(layoutParams);
                view1.setBackgroundColor(Color.TRANSPARENT);
                view1.setText("");
                //
                layout.addView(view1);
                layoutSeat.addView(layout);
            } else if (this.complete_layout.charAt(index) == 'U') {
                count++;
                TextView view = new TextView(this);
                view.setId(count);
                view.setGravity(Gravity.CENTER);
                view.setBackgroundResource(R.drawable.unselected_parking);
                view.setTextColor(Color.WHITE);
                view.setTag(STATUS_UNSELECTED);
                view.setText("A"+count + "");
                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                view.setTypeface(null, Typeface.BOLD);
                //
                TextView view1 = new TextView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
                view1.setLayoutParams(layoutParams);
                view1.setBackgroundColor(Color.TRANSPARENT);
                view1.setText("");
                layout.addView(view1);
                //
                layout.addView(view);
                seatViewList.add(view);
                view.setOnClickListener(this);
            } else if (this.complete_layout.charAt(index) == 'A') {
                count++;
                TextView view = new TextView(this);
                view.setId(count);
                view.setGravity(Gravity.CENTER);
                view.setBackgroundResource(R.drawable.selected_parking);
                view.setTextColor(Color.WHITE);
                view.setTag(STATUS_SELECTED);
                view.setText("A"+count+"");
                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                view.setTypeface(null, Typeface.BOLD);
                //
                TextView view1 = new TextView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
                view1.setLayoutParams(layoutParams);
                view1.setBackgroundColor(Color.TRANSPARENT);
                view1.setText("");
                //
                layout.addView(view);
                seatViewList.add(view);
                view.setOnClickListener(this);
            } else if (this.complete_layout.charAt(index) == 'B') {
                count++;
                TextView view = new TextView(this);
                view.setId(count);
                view.setGravity(Gravity.CENTER);
                view.setBackgroundResource(R.drawable.booked_parking);
                view.setTextColor(Color.WHITE);
                view.setTag(STATUS_BOOKED);
                view.setText("A"+count);
                view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                view.setTypeface(null, Typeface.BOLD);
                //
                TextView view1 = new TextView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
                view1.setLayoutParams(layoutParams);
                view1.setBackgroundColor(Color.TRANSPARENT);
                view1.setText("");
                layout.addView(view1);
                //
                layout.addView(view);
                seatViewList.add(view);
                view.setOnClickListener(this);
            }
        }
    }
    private void calculatePrice(long hours, long min) {
        checkOutDetails.setTotal_amount(String.valueOf(((hours * 60 + min) / 30) * 5));
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
        editText.setText(new StringBuilder().append(hour).append(':').append(min).append(" ").append(timeSet).toString());
    }
    @Override
    public void onClick(View view) {

        if((int)view.getTag() == STATUS_UNSELECTED) {
                if (selectedIds.contains(view.getId() + ",") & flag == 0) {
                    flag = 2;
                    selectedIds = selectedIds.replace(+view.getId() + ",", "");
                    view.setBackgroundResource(R.drawable.paid_parking);
                    card_view.setVisibility(View.GONE);
                } else if (flag == 1) {
                    Toast.makeText(this, "Only 1 Car Can be booked", Toast.LENGTH_SHORT).show();
                } else {
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ParkingLot.this, HomePage.class));
        finish();
    }
}
