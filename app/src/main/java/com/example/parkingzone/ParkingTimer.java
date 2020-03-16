package com.example.parkingzone;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javaFiles.CheckOutDetails;
import javaFiles.NewCar;

public class ParkingTimer extends AppCompatActivity {

    ProgressBar progressBar;
    TextView textCounter, carName;
    MyCountDownTimer myCountDownTimer;
    Button end;
    CheckOutDetails checkOutDetails;
    NewCar newCar;
    long maxId;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReferenceFromUrl("https://parking-zone-8ce19.firebaseio.com");
    private long timeLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_timer);
        retrieveData();
        retrieveCarData();
        initialise();

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ParkingTimer.this, "Thank you Please Visit Again", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ParkingTimer.this, HomePage.class));
                finish();
            }
        });
    }

    private void retrieveCarData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final DatabaseReference usersRef = ref.child("Parker").child(user.getUid()).child("Vehicle Details");
            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                        carName.setText(dataSnapshot.child("carType").getValue().toString().trim());
                    else
                        Toast.makeText(ParkingTimer.this, "No Car found Please register", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ParkingTimer.this, "U have failed this City " + databaseError, Toast.LENGTH_SHORT).show();
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
                                    checkOutDetails.setFinal_time((String) dataSnapshot.child("final_time").getValue());
                                    //textCounter.setText(checkOutDetails.getFinal_time());
                                    //text.setText(checkOutDetails.getFinal_time());
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
                                    try {
                                        Date date1 = simpleDateFormat.parse(checkOutDetails.getFinal_time());
                                        Log.d("Tejas", "A" + checkOutDetails.getFinal_time());
                                        Calendar cal = Calendar.getInstance();
                                        cal.setTime(date1);
                                        long hours = (long) cal.get(Calendar.HOUR);
                                        long min = (long) cal.get(Calendar.MINUTE);
                                        Log.d("Tejas", "A" + hours + "B" + min);
                                        calculateSeconds(hours, min);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(ParkingTimer.this, "No Booking Found", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(ParkingTimer.this, "U have failed this City " + databaseError, Toast.LENGTH_SHORT).show();
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

    private void calculateSeconds(long hours, long min) {
        long totalSeconds = (hours * 3600 + min * 60) * 1000;
        myCountDownTimer = new MyCountDownTimer(totalSeconds, 1000);
        myCountDownTimer.start();

    }

    private void initialise() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Black)));
        actionBar.setTitle("Park Zone");
        progressBar = findViewById(R.id.barTimer);
        textCounter = findViewById(R.id.time);
        end = findViewById(R.id.end_parking);
        checkOutDetails = new CheckOutDetails();
        newCar = new NewCar();
        carName = findViewById(R.id.car_name);

        progressBar.setProgress(100);
    }

    private void updateTimer() {
        int hours = (int) (timeLeft / 1000) / 3600;
        int min = (int) ((timeLeft / 1000) % 3600) / 60;
        int sec = (int) (timeLeft / 1000) % 60;
        String timeFormat;
        if (hours > 0)
            timeFormat = String.format(Locale.getDefault(), "%d:%02d:%02d", hours, min, sec);
        else
            timeFormat = String.format(Locale.getDefault(), "%02d:%02d", min, sec);

        textCounter.setText(timeFormat);
    }

    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            timeLeft = millisUntilFinished;
            updateTimer();
            //textCounter.setText(String.valueOf(millisUntilFinished));
            int progress = (int) (millisUntilFinished / 100);
            progressBar.setProgress(progress);
        }

        @Override
        public void onFinish() {
            textCounter.setText("Task completed");
            progressBar.setProgress(0);
        }

    }

}
