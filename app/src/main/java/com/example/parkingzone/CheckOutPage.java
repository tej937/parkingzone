package com.example.parkingzone;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javaFiles.CheckOutDetails;
import javaFiles.NewCar;
public class CheckOutPage extends AppCompatActivity {
    TextView location, carName, start_time, end_time, parking_slot, total_amount, plate_no, details;
    NewCar newCar;
    TextView temporary;
    Button payment_page;
    Dialog myDialog;
    CheckOutDetails checkOutDetails;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReferenceFromUrl("https://parking-zone-8ce19.firebaseio.com");
    // PaymentsClient paymentsClient;
    long maxId;
    String flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out_page);
        initialise();
        retrieveData();
        retrieveCarData();
        payment_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckOutPage.this, PaymentMethod.class);
                intent.putExtra("amount", total_amount.toString());
                startActivity(intent);
                finish();
            }
        });
    }
    private void initialise() {
        location = findViewById(R.id.location);
        carName = findViewById(R.id.car_name);
        start_time = findViewById(R.id.start_time);
        end_time = findViewById(R.id.end_time);
        parking_slot = findViewById(R.id.parking_spot);
        total_amount = findViewById(R.id.price);
        plate_no = findViewById(R.id.plate_no);
        details = findViewById(R.id.details);
        payment_page = findViewById(R.id.proceed);
        myDialog = new Dialog(this);
        temporary = findViewById(R.id.temp);

        checkOutDetails = new CheckOutDetails();
        newCar = new NewCar();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Black)));
        actionBar.setTitle("Check Out Page");

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
                        Toast.makeText(CheckOutPage.this, "No Car found Please register", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(CheckOutPage.this, "U have failed this City " + databaseError, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void retrieveData() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final DatabaseReference bookingId = ref.child("New Booking").child(user.getUid());
            bookingId.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        maxId = dataSnapshot.getChildrenCount();
                        DatabaseReference userRef = bookingId.child(String.valueOf(maxId));
                        userRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    //Toast.makeText(CheckOutPage.this, maxId+"", Toast.LENGTH_SHORT).show();
                                    getDataFromFirebase(dataSnapshot);
                                } else {
                                    Toast.makeText(CheckOutPage.this, "No Booking Found", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(CheckOutPage.this, "U have failed this City " + databaseError, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
    private void getDataFromFirebase(DataSnapshot dataSnapshot) {
        checkOutDetails.setParking_slot((String) dataSnapshot.child("parking_slot").getValue());
        checkOutDetails.setStart_time((String) dataSnapshot.child("start_time").getValue());
        checkOutDetails.setEnd_time((String) dataSnapshot.child("end_time").getValue());
        checkOutDetails.setTotal_amount((String) dataSnapshot.child("total_amount").getValue());
        checkOutDetails.setPayment_status((String) dataSnapshot.child("payment_status").getValue());
        checkOutDetails.setCurrent_date((String) dataSnapshot.child("current_date").getValue());
        checkOutDetails.setLocation((String) dataSnapshot.child("location").getValue());
        checkOutDetails.setPlace_name((String) dataSnapshot.child("place_name").getValue());

        String start_date_time = checkOutDetails.getCurrent_date() + "," + checkOutDetails.getStart_time();
        String end_date_time = checkOutDetails.getCurrent_date() + "," + checkOutDetails.getEnd_time();
        start_time.setText(start_date_time);
        end_time.setText(end_date_time);
        parking_slot.setText(checkOutDetails.getParking_slot());
        total_amount.setText(checkOutDetails.getTotal_amount());
        location.setText(checkOutDetails.getLocation());
        temporary.setText(checkOutDetails.getPlace_name());

    }
    private void getCarDataFromFirebase(DataSnapshot dataSnapshot) {
        newCar.setCarType((String) dataSnapshot.child("carType").getValue());
        newCar.setPlate((String) dataSnapshot.child("plate").getValue());
        carName.setText(newCar.getCarType());
        plate_no.setText(newCar.getPlate());
    }

    @Override
    public void onBackPressed() {
        Button yes, no;
        myDialog.setContentView(R.layout.cancelrequest_layout);
        yes = (Button) myDialog.findViewById(R.id.yess);
        no = (Button) myDialog.findViewById(R.id.cancel);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    final DatabaseReference deleteusersReq = ref.child("New Booking").child(user.getUid()).child(String.valueOf(maxId));
                    deleteusersReq.removeValue();
                    final DatabaseReference request = ref.child(temporary.getText().toString()).child("Request Generated").child(user.getUid());
                    request.removeValue();
                    myDialog.dismiss();
                    startActivity(new Intent(CheckOutPage.this, HomePage.class));
                    finish();
                    Toast.makeText(CheckOutPage.this, "Request Cancelled", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

}

