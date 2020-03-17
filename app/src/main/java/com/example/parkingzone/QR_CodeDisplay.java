package com.example.parkingzone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.IOException;
import java.util.HashMap;

import javaFiles.CheckOutDetails;
import javaFiles.NewCar;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class QR_CodeDisplay extends AppCompatActivity {
    TextView booking_id, start_time, end_time, parking_spot, address, place_name;
    CheckOutDetails checkOutDetails;
    NewCar newCar;
    String plate_no, user_name, payment_status, car_name;
    Bitmap bitmap;
    ImageView qrCode;
    ProgressDialog progressDialog;
    GifImageView mGigImageView;
    CardView ticket;
    Button start_timer;

    int i = 0;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReferenceFromUrl("https://parking-zone-8ce19.firebaseio.com");
    long maxId;
    String flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r__code_display);
        initialise();
        retrieveData();
        retrieveCarData();
        generateUniqueID();
        Intent intent = getIntent();
        flag = intent.getStringExtra("flag");
        //flag = String.valueOf(1);
        start_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i == 0) {
                    Toast.makeText(QR_CodeDisplay.this, "Request you to start timer only after\n " +
                            "QR code gets scanned" +
                            "\nIf scanning is done then" +
                            "Tap again to start timer", Toast.LENGTH_SHORT).show();
                    i++;
                } else if (i == 1) {
                    startActivity(new Intent(QR_CodeDisplay.this, ParkingTimer.class));
                    finish();
                }
            }
        });
    }

    private void generateUniqueID() {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder uniqueString = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());
            uniqueString.append(AlphaNumericString
                    .charAt(index));
        }
        String text = "Booking ID: ".concat(String.valueOf(uniqueString));
        booking_id.setText(text);
    }

    private void getBitMapImage() throws WriterException {
        String value = newCar.getPlate() + "\n" + newCar.getName() + "\n" + newCar.getCarType();
        bitmap = TextToImageEncode(value);
        qrCode.setImageBitmap(bitmap);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(QR_CodeDisplay.this, HomePage.class));
        finish();
    }

    private void initialise() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Black)));
        actionBar.setTitle("Booking ID");

        booking_id = findViewById(R.id.booking_id);
        checkOutDetails = new CheckOutDetails();
        newCar = new NewCar();
        start_time = findViewById(R.id.start_time);
        end_time = findViewById(R.id.end_time);
        parking_spot = findViewById(R.id.seat_no);
        qrCode = findViewById(R.id.qr_code);
        address = findViewById(R.id.address);
        place_name = findViewById(R.id.place_name);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Checking with Payment status..."); // Setting Message
        progressDialog.setTitle("Please Wait"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// Progress Dialog Style Spinner
        ticket = findViewById(R.id.ticket);
        mGigImageView = findViewById(R.id.completed);
        start_timer = findViewById(R.id.start_timer);

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
                        Toast.makeText(QR_CodeDisplay.this, "No Car found Please register", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(QR_CodeDisplay.this, "U have failed this City " + databaseError, Toast.LENGTH_SHORT).show();
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
                                    getDataFromFirebase(dataSnapshot);
                                } else {
                                    Toast.makeText(QR_CodeDisplay.this, "No Booking Found", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(QR_CodeDisplay.this, "U have failed this City " + databaseError, Toast.LENGTH_SHORT).show();
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
        checkOutDetails.setAddress((String) dataSnapshot.child("address").getValue());
        checkOutDetails.setParking_slot((String) dataSnapshot.child("parking_slot").getValue());
        checkOutDetails.setStart_time((String) dataSnapshot.child("start_time").getValue());
        checkOutDetails.setEnd_time((String) dataSnapshot.child("end_time").getValue());
        checkOutDetails.setPayment_status((String) dataSnapshot.child("payment_status").getValue());
        checkOutDetails.setPlace_name((String) dataSnapshot.child("place_name").getValue());
        payment_status = checkOutDetails.getPayment_status();
        start_time.setText(checkOutDetails.getStart_time());
        end_time.setText(checkOutDetails.getEnd_time());
        parking_spot.setText(checkOutDetails.getParking_slot());
        address.setText(checkOutDetails.getAddress());
        place_name.setText("Parking name: " + checkOutDetails.getPlace_name());

        checkPaymentStatus();

    }

    private void getCarDataFromFirebase(DataSnapshot dataSnapshot) {
        newCar.setCarType((String) dataSnapshot.child("carType").getValue());
        newCar.setPlate((String) dataSnapshot.child("plate").getValue());
        newCar.setName((String) dataSnapshot.child("name").getValue());
        car_name = newCar.getName();
        plate_no = newCar.getPlate();
        user_name = newCar.getName();
        try {
            getBitMapImage();
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    150, 150, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.Black) : getResources().getColor(R.color.White);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 150, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    public void checkPaymentStatus() {
        if (flag.equals("1")) {
            //Toast.makeText(this, "Inside IF", Toast.LENGTH_SHORT).show();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                //Toast.makeText(this, "Inside User", Toast.LENGTH_SHORT).show();
                progressDialog.show(); // Display Progress Dialog
                progressDialog.setCancelable(false);
                final DatabaseReference usersRef = ref.child("New Booking").child(user.getUid()).child(String.valueOf(maxId));
                HashMap<String, Object> result = new HashMap<>();
                result.put("payment_status", "Success");
                usersRef.updateChildren(result);
                final DatabaseReference request = ref.child(checkOutDetails.getPlace_name()).child("Request Generated").child(user.getUid());
                request.updateChildren(result);
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            synchronized (this) {
                                wait(2000);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mGigImageView.setVisibility(View.VISIBLE);
                                        GifDrawable gifDrawable = null;
                                        try {
                                            gifDrawable = new GifDrawable(getResources(), R.drawable.completed);
                                            gifDrawable.setLoopCount(2);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        mGigImageView.setImageDrawable(gifDrawable);
                                        new Thread() {
                                            @Override
                                            public void run() {
                                                try {
                                                    synchronized (this) {
                                                        wait(4000);
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                mGigImageView.setVisibility(View.GONE);
                                                                ticket.setVisibility(View.VISIBLE);
                                                                start_timer.setVisibility(View.VISIBLE);
                                                            }
                                                        });
                                                    }
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            ;
                                        }.start();
                                    }
                                });
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                }.start();
            }
        } else if (flag.equals("2")) {
            //Toast.makeText(this, "Inside IF", Toast.LENGTH_SHORT).show();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                //Toast.makeText(this, "Inside User", Toast.LENGTH_SHORT).show();
                progressDialog.show(); // Display Progress Dialog
                progressDialog.setCancelable(false);
                final DatabaseReference usersRef = ref.child("New Booking").child(user.getUid()).child(String.valueOf(maxId));
                HashMap<String, Object> result = new HashMap<>();
                result.put("payment_status", "Success");
                usersRef.updateChildren(result);
                final DatabaseReference request = ref.child(checkOutDetails.getPlace_name()).child("Request Generated").child(user.getUid());
                request.updateChildren(result);
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            synchronized (this) {
                                wait(2000);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mGigImageView.setVisibility(View.VISIBLE);
                                        GifDrawable gifDrawable = null;
                                        try {
                                            gifDrawable = new GifDrawable(getResources(), R.drawable.completed);
                                            gifDrawable.setLoopCount(2);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        mGigImageView.setImageDrawable(gifDrawable);
                                    }
                                });
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        }
    }
}