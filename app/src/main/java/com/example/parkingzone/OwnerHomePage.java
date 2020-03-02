package com.example.parkingzone;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import javaFiles.NewOwner;

public class OwnerHomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    ViewGroup slot_layout;
    //String row = "/UUUUUU";
    // String spaces = "/";
    String complete_layout = "";
    // String complete_lay;
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

    //int total_slots;

    List<TextView> seatViewList = new ArrayList<>();
    int width = 40;
    int height = 35;
    int STATUS_UNSELECTED = 1;
    int STATUS_SELECTED = 2;
    int STATUS_BOOKED = 3;
    String selectedIds = "";
    // int[] arr;
    //int flag = 0;

    DrawerLayout mDraw;
    NavigationView navigationView;
    ActionBarDrawerToggle mToggle;
    Dialog myDialog;
    View headerView;

    Button update_details, book, delete;
    NewOwner newOwner;
    RelativeLayout update_layout, main_layout;
    TextView temp, tem;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReferenceFromUrl("https://parking-zone-8ce19.firebaseio.com");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_home_page);
        initialise();
        checkDetails();
        retrieveData();
        //displayLayout();
        //constructString();
        //displayLayout();
        update_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OwnerHomePage.this, SignUpOwner2.class));
                finish();
            }
        });

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    final DatabaseReference usersRef = ref.child("Owner").child(user.getUid()).child("Parking Details").child("layout1");
                    usersRef.setValue(complete_layout);
//                    startActivity(new Intent(OwnerHomePage.this,OwnerHomePage.class));
//                    finish();
                    //tem.setText(complete_lay);
                    //Toast.makeText(this, "Layout Updated", Toast.LENGTH_SHORT).show();
                }
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OwnerHomePage.this, "Update Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private void constructString() {
//        long number_rows = total_slots/6;
//        for(int i = 1;i<=number_rows;i++){
//            complete_layout = complete_layout + row;
//        }
//       }

    private void retrieveData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final DatabaseReference usersRef = ref.child("Owner").child(user.getUid()).child("Parking Details");
            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        getDataFromFirebase(dataSnapshot);
                    } else {
                        Toast.makeText(OwnerHomePage.this, "Chutiya kut gaya", Toast.LENGTH_SHORT).show();
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
        //temp.setText(newOwner.getSlots());
        complete_layout = newOwner.getLayout1();
        displayLayout();
        //total_slots = Integer.parseInt(((temp.getText().toString())));
        //constructString();
    }

    private void checkDetails() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference check_details = ref.child("Owner").child(user.getUid()).child("Parking Details");
        check_details.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() & dataSnapshot.getChildrenCount() == 7)
                    Toast.makeText(OwnerHomePage.this, "All required data uploaded", Toast.LENGTH_SHORT).show();
                else{
                    update_layout.setVisibility(View.VISIBLE);
                    main_layout.setVisibility(View.GONE);
                }
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
        temp = findViewById(R.id.temp);
        slot_layout = findViewById(R.id.slots_layout);
        book = findViewById(R.id.book_spot);
        delete = findViewById(R.id.delete_spot);
        //complete_layout = "/" + complete_layout;
        tem = findViewById(R.id.temp1);

        mDraw = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDraw, R.string.open, R.string.close);
        mDraw.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.navigation);
        headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        myDialog = new Dialog(this);

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
        } else if (id == R.id.payment) {
            Intent intent = new Intent(OwnerHomePage.this, TransactionPage.class);
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
        }
        return super.onOptionsItemSelected(item);
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
            Toast.makeText(this, "Seat No." + a, Toast.LENGTH_SHORT).show();
            //temp.setText(String.valueOf(complete_layout));
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
            //            temp.setText(complete_layout);
            //            complete_lay = complete_layout;
        } else if ((int) view.getTag() == STATUS_BOOKED)
            Toast.makeText(this, "Seat " + view.getId() + " is Booked", Toast.LENGTH_SHORT).show();
    }
}