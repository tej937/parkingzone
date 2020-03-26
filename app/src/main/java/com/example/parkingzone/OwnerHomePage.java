package com.example.parkingzone;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parkingzone.Adapters.Booking_Progress_Adpater;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import javaFiles.CheckOutDetails;
import javaFiles.NewOwner;

public class OwnerHomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    ViewGroup slot_layout;
    //String row = "/UUUUUU";
    // String spaces = "/";
    String complete_layout = "";
    String qr_result;
    List<TextView> seatViewList = new ArrayList<>();
    int width = 40;
    int height = 35;
    int STATUS_UNSELECTED = 1;
    int STATUS_SELECTED = 2;
    int STATUS_BOOKED = 3;
    String selectedIds = "";
    private long noOwner;
    int flag = 1;
    private long backPressed;
    // int[] arr;
    //int flag = 0;

    DrawerLayout mDraw;
    NavigationView navigationView;
    ActionBarDrawerToggle mToggle;
    Dialog myDialog;
    View headerView;

    RecyclerView recyclerView;
    TextView textView, textView1, closed;
    ArrayList<CheckOutDetails> list;
    ProgressDialog progressDialog;
    CheckOutDetails checkOutDetails;

    Button update_details, book;
    NewOwner newOwner;
    SwitchCompat status;

    RelativeLayout update_layout, main_layout, status_layout;
    TextView tem;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReferenceFromUrl("https://parking-zone-8ce19.firebaseio.com");
    Booking_Progress_Adpater booking_progress_adpater;
    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_home_page);

        initialise();
        checkParkingStatus();
        //getOwnerNo();

        //displayLayout();
        //constructString();

        update_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OwnerHomePage.this, SignUpOwner2.class));
                finish();
            }
        });

        status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        closed.setText("Open");
                        final DatabaseReference usersRef = ref.child("Owner").child(user.getUid()).child("Parking Details").child("parking_Status");
                        usersRef.setValue("Open");
                    }
                } else {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        closed.setText("Close");
                        final DatabaseReference usersRef = ref.child("Owner").child(user.getUid()).child("Parking Details").child("parking_Status");
                        usersRef.setValue("Close");
                    }
                }
            }
        });
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewSeat();
            }
        });
    }

    private void checkParkingStatus() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final DatabaseReference usersRef = ref.child("Owner").child(user.getUid()).child("Parking Details").child("parking_Status");
            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String status = String.valueOf(dataSnapshot.getValue());
                    if (status.equals("Close")) {
                        main_layout.setVisibility(View.GONE);
                        status_layout.setVisibility(View.VISIBLE);
                    } else if (status.equals("Open")) {
                        retrieveData();
                        main_layout.setVisibility(View.VISIBLE);
                        status_layout.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
    public void saveNewSeat() {
        getOwnerNo();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final DatabaseReference usersRef = ref.child("Owner").child(user.getUid()).child("Parking Details").child("layout1");
            usersRef.setValue(complete_layout);
            final DatabaseReference update_Layout = ref.child(tem.getText().toString()).child("OwnerInfo").child(String.valueOf(noOwner + 1)).child("layout1");
            update_Layout.setValue(complete_layout);
            progressDialog = new ProgressDialog(OwnerHomePage.this);
            progressDialog.setMessage("Updating Layout..."); // Setting Message
            progressDialog.setTitle("Please Wait"); // Setting Title
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner

            progressDialog.show(); // Display Progress Dialog
            progressDialog.setCancelable(false);
            new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(2000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(OwnerHomePage.this, OwnerHomePage.class));
                                finish();
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
    }
    private void retrieveData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final DatabaseReference usersRef = ref.child("Owner").child(user.getUid()).child("Parking Details");
            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() & dataSnapshot.getChildrenCount() == 8) {
                        //Toast.makeText(OwnerHomePage.this, "All required data uploaded", Toast.LENGTH_SHORT).show();
                        getDataFromFirebase(dataSnapshot);
                    } else {
                        update_layout.setVisibility(View.VISIBLE);
                        main_layout.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(OwnerHomePage.this, "U have failed this City " + databaseError, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void getDataFromFirebase(DataSnapshot dataSnapshot) {
        newOwner.setLayout1((String) dataSnapshot.child("layout1").getValue());
        newOwner.setSlots((String) dataSnapshot.child("slots").getValue());
        newOwner.setAddress((String) dataSnapshot.child("address").getValue());
        newOwner.setParking_name((String) dataSnapshot.child("parking_name").getValue());
        newOwner.setArea((String) dataSnapshot.child("area").getValue());
        textView1.setText(newOwner.getParking_name());
        tem.setText(newOwner.getArea());
        getRequest();
        complete_layout = newOwner.getLayout1();
        displayLayout();
    }
    private void getRequest() {
        final DatabaseReference reference = ref.child(newOwner.getParking_name()).child("Request Generated");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        checkOutDetails = dataSnapshot1.getValue(CheckOutDetails.class);
                        list.add(checkOutDetails);
                        displayRequest();
                    }
                    booking_progress_adpater = new Booking_Progress_Adpater(OwnerHomePage.this, list);
                    recyclerView.setAdapter(booking_progress_adpater);
                } else {
                    textView.setVisibility(View.VISIBLE);
                    //Toast.makeText(OwnerHomePage.this, "Ooops Sorry !!!!!!!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }
    private void displayRequest() {
        final Button confirm;
        TextView text;
        myDialog.setContentView(R.layout.request_layout);
        confirm = myDialog.findViewById(R.id.yess);
        text = myDialog.findViewById(R.id.question_text);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (!((Activity) this).isFinishing()) {
            myDialog.show();
            text.setText("You have a parking request at " + checkOutDetails.getSeatNo() + ". Please confirm request if available");
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SeatsUpdation(checkOutDetails.getSeatNo());
                    saveNewSeat();
                    DatabaseReference deleteRequest = ref.child(textView1.getText().toString());
                    deleteRequest.removeValue();
                    myDialog.dismiss();
                }
            });
        }
    }

    private void getOwnerNo() {
        final DatabaseReference userNo = ref.child(tem.getText().toString()).child("OwnerInfo");
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
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Black)));
        actionBar.setTitle("Park Zone");

        update_details = (Button) findViewById(R.id.update);
        update_layout = (RelativeLayout) findViewById(R.id.update_layout);
        main_layout = findViewById(R.id.main_layout);
        newOwner = new NewOwner();
        slot_layout = findViewById(R.id.slots_layout);
        status_layout = findViewById(R.id.status_layout);
        book = findViewById(R.id.book_spot);
        textView = findViewById(R.id.no_booking);
        //complete_layout = "/" + complete_layout;
        tem = findViewById(R.id.temp1);
        qrScan = new IntentIntegrator(this);

        mDraw = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDraw, R.string.open, R.string.close);
        mDraw.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.navigation);
        headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        myDialog = new Dialog(this);
        closed = findViewById(R.id.closed);
        status = findViewById(R.id.status_switch);

        ref.keepSynced(true);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        list = new ArrayList<CheckOutDetails>();
        textView1 = findViewById(R.id.text2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scanner, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.parking) {
            Intent intent = new Intent(OwnerHomePage.this, ParkingData.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.setting) {
            Intent intent = new Intent(OwnerHomePage.this, SettingOwnerPage.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.signout) {
            Button signout, cancel;
            myDialog.setContentView(R.layout.signout);
            signout = (Button) myDialog.findViewById(R.id.yess);
            cancel = (Button) myDialog.findViewById(R.id.cancel);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog.dismiss();
                }
            });
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            myDialog.show();
            signout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    myDialog.dismiss();
                    Intent intent = new Intent(OwnerHomePage.this, Selection.class);
                    int flag = 0;
                    Bundle bundle = new Bundle();
                    bundle.putString("flag", String.valueOf(flag));
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                    return;
                }
            });
            return true;
        } else if (id == R.id.home) {
            Intent intent = new Intent(OwnerHomePage.this, OwnerHomePage.class);
            startActivity(intent);
            finish();
            return true;
        } else
            return false;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        } else if (item.getItemId() == R.id.scanner) {
            OpenScanner();
        }
        return super.onOptionsItemSelected(item);
    }

    private void OpenScanner() {
        qrScan.initiateScan();
        Toast.makeText(this, "Scanning QR code Wait", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Invalid QR\nResult Not Found", Toast.LENGTH_SHORT).show();
            } else {
                qr_result = result.getContents();
                Toast.makeText(this, "" + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
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

        for (int index = 0; index < complete_layout.length(); index++) {
            if (complete_layout.charAt(index) == '/') {
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
            } else if (complete_layout.charAt(index) == 'U') {
                count++;
                TextView view = new TextView(this);
                view.setId(count);
                view.setGravity(Gravity.CENTER);
                view.setBackgroundResource(R.drawable.unselected_parking);
                view.setTextColor(Color.WHITE);
                view.setTag(STATUS_UNSELECTED);
                view.setText("A" + count + "");
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
            } else if (complete_layout.charAt(index) == 'A') {
                count++;
                TextView view = new TextView(this);
                view.setId(count);
                view.setGravity(Gravity.CENTER);
                view.setBackgroundResource(R.drawable.selected_parking);
                view.setTextColor(Color.WHITE);
                view.setTag(STATUS_SELECTED);
                view.setText("A" + count + "");
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
            } else if (complete_layout.charAt(index) == 'B') {
                count++;
                TextView view = new TextView(this);
                view.setId(count);
                view.setGravity(Gravity.CENTER);
                view.setBackgroundResource(R.drawable.booked_parking);
                view.setTextColor(Color.WHITE);
                view.setTag(STATUS_BOOKED);
                view.setText("A" + count);
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
    @Override
    public void onClick(View view) {
        if ((int) view.getTag() == STATUS_UNSELECTED) {
            selectedIds = selectedIds + view.getId() + ",";
//          StringBuilder myName = new StringBuilder(complete_layout);
//          myName.setCharAt(complete_layout.indexOf("U"), 'A');
            view.setBackgroundResource(R.drawable.selected_parking);
            int a = view.getId();
            SeatsUpdation(a);

        } else if ((int) view.getTag() == STATUS_BOOKED) {
            if (flag == 1) {
                Toast.makeText(this, "Seat " + view.getId() + " is Booked \n Click again to delete the Spot", Toast.LENGTH_SHORT).show();
                flag = flag + 1;
            } else if (flag == 2) {
                view.setBackgroundResource(R.drawable.unselected_parking);
                view.setTag(1);
                Toast.makeText(this, "Seat " + view.getId() + " is Deleted", Toast.LENGTH_SHORT).show();
                flag = 1;
            }
        }
    }

    private void SeatsUpdation(int a) {
        char[] temp1 = complete_layout.toCharArray();
        if (a >= 1 & a <= 6)
            temp1[a] = 'B';
        else if (a >= 7 & a <= 12)
            temp1[a + 1] = 'B';
        else if (a >= 13 & a <= 18)
            temp1[a + 2] = 'B';
        else if (a >= 19 & a <= 24)
            temp1[a + 3] = 'B';
        else if (a >= 25 & a <= 30)
            temp1[a + 4] = 'B';
        else if (a >= 31 & a <= 36)
            temp1[a + 5] = 'B';
        else if (a >= 37 & a <= 42)
            temp1[a + 6] = 'B';
        else if (a >= 43 & a <= 48)
            temp1[a + 7] = 'B';
        complete_layout = String.valueOf(temp1);
    }

    @Override
    public void onBackPressed() {
        if (backPressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(this, "Please press back again to exit", Toast.LENGTH_SHORT).show();
        }
        backPressed = System.currentTimeMillis();
    }
}