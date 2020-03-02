package com.example.parkingzone;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javaFiles.NewOwner;

public class SignUpOwner2 extends AppCompatActivity {

    String row = "/UUUUUU";
    // String spaces = "/";
    String complete_layout = "";

    TextInputLayout parking_name, address;
    Spinner slot, area;
    TextView stime,etime;
    ImageView back;
    Button register;
    NewOwner newOwner;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReferenceFromUrl("https://parking-zone-8ce19.firebaseio.com");
    private long noOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up__owner2);
        initialise();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        getOwnerNo();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpOwner2.this,Selection.class));
                finish();
            }
        });
        stime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(SignUpOwner2.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        displayTime(hourOfDay,minute,stime);
                    }
                },0,0,false);
                timePickerDialog.show();
            }
        });
        etime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(SignUpOwner2.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        displayTime(hourOfDay,minute,etime);
                    }
                },0,0,false);
            timePickerDialog.show();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateParkName(parking_name.getEditText().getText().toString()) | !validateAddress(address.getEditText().getText().toString()))
                    Toast.makeText(SignUpOwner2.this, "Invalid Details", Toast.LENGTH_SHORT).show();
                else{
                    constructString();
                    parking_name.setError(null);
                    address.setError(null);
                    newOwner.setParking_name(parking_name.getEditText().getText().toString().trim());
                    newOwner.setAddress(address.getEditText().getText().toString().trim());
                    newOwner.setArea(area.getSelectedItem().toString().trim());
                    newOwner.setEtiming(etime.getText().toString().trim());
                    newOwner.setStiming(stime.getText().toString().trim());
                    newOwner.setSlots(slot.getSelectedItem().toString().trim());
                    newOwner.setLayout1(complete_layout);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user!=null){
                        final DatabaseReference usersRef = ref.child("Owner").child(user.getUid()).child("Parking Details");
                        usersRef.setValue(newOwner);
                        startActivity(new Intent(SignUpOwner2.this,OwnerHomePage.class));
                        saveDataToRespectiveNumber(newOwner);
                        finish();
                        Toast.makeText(SignUpOwner2.this, "New Car Added", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void saveDataToRespectiveNumber(NewOwner newOwner) {

        Toast.makeText(SignUpOwner2.this, "" + noOwner, Toast.LENGTH_SHORT).show();
        final DatabaseReference owners = ref.child("OwnerInfo").child(String.valueOf(noOwner + 1));
        owners.setValue(newOwner);

    }

    private void getOwnerNo() {
        final DatabaseReference userNo = ref.child("OwnerInfo");
        userNo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noOwner = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void initialise() {
        parking_name = (TextInputLayout) findViewById(R.id.lot_name);
        address = (TextInputLayout) findViewById(R.id.address);
        area = findViewById(R.id.area);
        slot = (Spinner) findViewById(R.id.slots);
        stime = (TextView) findViewById(R.id.stime);
        etime = (TextView) findViewById(R.id.etime);
        register = (Button) findViewById(R.id.register);
        back = (ImageView) findViewById(R.id.back);
        newOwner = new NewOwner();
    }

    private void constructString() {
        long number_rows = Long.parseLong(slot.getSelectedItem().toString()) / 6;
        for (int i = 1; i <= number_rows; i++) {
            complete_layout = complete_layout + row;
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
    private boolean validateParkName(String name) {
        if(name.isEmpty()){
            parking_name.setError("This Field can't be Empty");
            return false;
        }
        else {
            parking_name.setError(null);
            return true;
        }
    }
    private boolean validateAddress(String plate_no) {
        if(plate_no.isEmpty()){
            address.setError("This Field can't be Empty");
            return false;
        }
        else {
            address.setError(null);
            return true;
        }
    }
//    private boolean validateArea(String Area) {
//        if(Area.isEmpty()){
//            area.setError("This Field can't be Empty");
//            return false;
//        }
//        else {
//            area.setError(null);
//            return true;
//        }
//    }
}
