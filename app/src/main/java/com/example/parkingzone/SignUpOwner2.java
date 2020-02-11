package com.example.parkingzone;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javaFiles.NewOwner;

public class SignUpOwner2 extends AppCompatActivity {
    TextInputLayout parking_name,address,area,stiming,parking_slots,etiming;
    Spinner slot;
    TextInputEditText stime,etime;
    ImageView back;
    Button register;
    NewOwner newOwner;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReferenceFromUrl("https://parking-zone-8ce19.firebaseio.com");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up__owner2);
        initialise();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpOwner2.this,SignUpOwner.class));
                finish();
            }
        });
        stiming.setOnClickListener(new View.OnClickListener() {
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
        etiming.setOnClickListener(new View.OnClickListener() {
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
                if(!validateParkName(parking_name.getEditText().getText().toString()) | !validateAddress(address.getEditText().getText().toString())
                | !validateArea(area.getEditText().getText().toString()) |!validateTime(stime.getText().toString(),etime.getText().toString())
                | !validateSlots(slot.getSelectedItem().toString().trim()))
                    Toast.makeText(SignUpOwner2.this, "Invalid Details", Toast.LENGTH_SHORT).show();
                else{
                    parking_name.setError(null);
                    address.setError(null);
                    area.setError(null);
                    stiming.setError(null);
                    etiming.setError(null);
                    parking_slots.setError(null);
                    newOwner.setParking_name(parking_name.getEditText().getText().toString().trim());
                    newOwner.setAddress(address.getEditText().getText().toString().trim());
                    newOwner.setArea(area.getEditText().getText().toString().trim());
                    newOwner.setEtiming(etime.getText().toString().trim());
                    newOwner.setStiming(stime.getText().toString().trim());
                    newOwner.setSlots(slot.getSelectedItem().toString().trim());
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user!=null){
                        final DatabaseReference usersRef = ref.child("Owner").child(user.getUid()).child("Parking Details");
                        usersRef.setValue(newOwner);
                        startActivity(new Intent(SignUpOwner2.this,OwnerHomePage.class));
                        finish();
                        Toast.makeText(SignUpOwner2.this, "New Car Added", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void initialise() {
        parking_name = (TextInputLayout) findViewById(R.id.lot_name);
        address = (TextInputLayout) findViewById(R.id.address);
        area = (TextInputLayout) findViewById(R.id.area);
        stiming = (TextInputLayout) findViewById(R.id.stiming);
        etiming = (TextInputLayout) findViewById(R.id.etiming);
        parking_slots = (TextInputLayout) findViewById(R.id.slot_layout);
        slot = (Spinner) findViewById(R.id.slots);
        stime = (TextInputEditText) findViewById(R.id.stime);
        etime = (TextInputEditText) findViewById(R.id.etime);
        register = (Button) findViewById(R.id.register);
        newOwner = new NewOwner();
    }

    public void displayTime(int hourOfDay,int minute,TextInputEditText editText){
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
    private boolean validateArea(String Area) {
        if(Area.isEmpty()){
            area.setError("This Field can't be Empty");
            return false;
        }
        else {
            area.setError(null);
            return true;
        }
    }
    private boolean validateTime(String stime,String etime) {
        if(stime.isEmpty() | etime.isEmpty()){
            stiming.setError("This Field can't be Empty");
            etiming.setError("This Field cant be Empty");
            return false;
        }
        else {
            stiming.setError(null);
            etiming.setError(null);
            return true;
        }
    }
    private boolean validateSlots(String slots) {
        if(slots.isEmpty()){
            parking_slots.setError("This Field can't be Empty");
            return false;
        }
        else {
            parking_slots.setError(null);
            return true;
        }
    }
}
