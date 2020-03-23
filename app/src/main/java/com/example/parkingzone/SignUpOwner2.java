package com.example.parkingzone;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javaFiles.NewOwner;

public class SignUpOwner2 extends AppCompatActivity {

    String row = "/UUUUUU";
    // String spaces = "/";
    String complete_layout = "";

    TextInputLayout parking_name, address;
    TextInputEditText address_edit;
    Spinner slot, area;
    TextView stime, etime, error;
    String time;
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
        //getOwnerNo();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpOwner2.this, Selection.class));
                finish();
            }
        });
        stime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(SignUpOwner2.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        displayTime(hourOfDay, minute, stime);
                    }
                }, 0, 0, false);
                timePickerDialog.show();
            }
        });
        etime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(SignUpOwner2.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        displayTime(hourOfDay, minute, etime);
                    }
                }, 0, 0, false);
                timePickerDialog.show();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    time = differenceBetweenTime(stime.getText().toString().trim(), etime.getText().toString().trim());
                    if (!validateParkName(parking_name.getEditText().getText().toString()) | !validateAddress(address.getEditText().getText().toString())
                            | time.contains("Please") | stime.getText().equals("00 : 00") | etime.getText().equals("00 : 00"))
                        Toast.makeText(SignUpOwner2.this, "Invalid Details", Toast.LENGTH_SHORT).show();
                    else {
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
                        newOwner.setParking_Status("Open");
                        getOwnerNo();
                        Log.d("Riyaa", "A" + newOwner);
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            final DatabaseReference usersRef = ref.child("Owner").child(user.getUid()).child("Parking Details");
                            usersRef.setValue(newOwner);
                            startActivity(new Intent(SignUpOwner2.this, OwnerHomePage.class));
                            finish();
                            saveDataToRespectiveNumber(newOwner);
                            //Toast.makeText(SignUpOwner2.this, "New Car Added", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void saveDataToRespectiveNumber(NewOwner newOwner) {
        Log.d("Shraddha", "A" + noOwner);
        final DatabaseReference owners = ref.child(newOwner.getArea()).child("OwnerInfo").child(String.valueOf(noOwner + 1));
        owners.setValue(newOwner);
    }
//    private final LocationListener mLocationListener = new LocationListener() {
//        @Override
//        public void onLocationChanged(final Location location) {
//            Log.d("Om", String.valueOf(location.getLatitude()));
//            String add = getCurrentAddress(SignUpOwner2.this,location.getLatitude(),location.getLongitude());
//            address_edit.setText(add);
//            DatabaseReference owner_location = ref.child("Owner Location");
//            owner_location.setValue(location);
//        }
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//
//        }
//
//        @Override
//        public void onProviderEnabled(String provider) {
//
//        }
//
//        @Override
//        public void onProviderDisabled(String provider) {
//
//        }
//    };
//
//
//    public String getCurrentAddress(Context ctx, double lat, double lng) {
//        String full_address = null;
//        try {
//            Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
//            List<Address> addresses;
//            addresses = geocoder.getFromLocation(lat, lng, 1);
//            if (addresses.size() > 0) {
//                Address address = addresses.get(0);
//                full_address = address.getAddressLine(0);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return full_address;
//    }


    private void initialise() {
        parking_name = (TextInputLayout) findViewById(R.id.lot_name);
        address = (TextInputLayout) findViewById(R.id.address);
        address_edit = findViewById(R.id.address_edit);
        area = findViewById(R.id.area);
        slot = (Spinner) findViewById(R.id.slots);
        stime = (TextView) findViewById(R.id.stime);
        etime = (TextView) findViewById(R.id.etime);
        register = (Button) findViewById(R.id.register);
        back = (ImageView) findViewById(R.id.back);
        error = findViewById(R.id.error);
        newOwner = new NewOwner();
    }
    private void constructString() {
        long number_rows = Long.parseLong(slot.getSelectedItem().toString()) / 6;
        for (int i = 1; i <= number_rows; i++) {
            complete_layout = complete_layout + row;
        }
    }

    private void getOwnerNo() {
        final DatabaseReference owners = ref.child(area.getSelectedItem().toString().trim()).child("OwnerInfo");
        owners.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noOwner = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

    private String differenceBetweenTime(String start_Time, String end_Time) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
        Date date1 = simpleDateFormat.parse(start_Time);
        Date date2 = simpleDateFormat.parse(end_Time);
        if (date2.before(date1)) {
            error.setVisibility(View.VISIBLE);
            error.setText("Please Set time difference within 20 HOURS");
            return "Please Set time difference within 20 HOURS";
        } else {
            error.setVisibility(View.GONE);
            long difference = date2.getTime() - date1.getTime();
            long days = (int) (difference / (1000 * 60 * 60 * 24));
            long hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
            long min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
            hours = (hours < 0 ? -hours : hours);
            if (min < 30 & hours == 0) {
                error.setVisibility(View.VISIBLE);
                error.setText("Please set time difference more than 30 min");
                return "Please set time difference more than 30 min";
            } else {
                error.setVisibility(View.GONE);
                return hours + " Hour " + min + " Min";
            }
        }
    }

}
