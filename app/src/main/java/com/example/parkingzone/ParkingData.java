package com.example.parkingzone;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parkingzone.Adapters.Admin_Booking_History;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import javaFiles.CheckOutDetails;
import javaFiles.NewOwner;

public class ParkingData extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout mDraw;
    NavigationView navigationView;
    ActionBarDrawerToggle mToggle;
    Dialog myDialog;
    View headerView;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReferenceFromUrl("https://parking-zone-8ce19.firebaseio.com");
    Admin_Booking_History booking_progress_adpater;
    CheckOutDetails checkOutDetails;
    RecyclerView recyclerView;
    TextView textView;
    ArrayList<CheckOutDetails> list;
    NewOwner newOwner;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_data);
        initiate();
        retrieveData();
    }

    private void initiate() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Black)));
        actionBar.setTitle("Park Zone");

        mDraw = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDraw, R.string.open, R.string.close);
        mDraw.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.navigation);
        headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        myDialog = new Dialog(this);

        ref.keepSynced(true);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        list = new ArrayList<CheckOutDetails>();
        textView = findViewById(R.id.no_booking);
        newOwner = new NewOwner();

    }

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
                        Toast.makeText(ParkingData.this, "No database Found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ParkingData.this, "U have failed this City " + databaseError, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getDataFromFirebase(DataSnapshot dataSnapshot) {
        newOwner.setAddress((String) dataSnapshot.child("address").getValue());
        getRequest();
    }

    private void getRequest() {
        final DatabaseReference reference = ref.child(newOwner.getAddress()).child("Request Generated");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        checkOutDetails = dataSnapshot1.getValue(CheckOutDetails.class);
                        list.add(checkOutDetails);
                    }
                    booking_progress_adpater = new Admin_Booking_History(ParkingData.this, list);
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


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.parking) {
            Intent intent = new Intent(ParkingData.this, ParkingData.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.setting) {
            Intent intent = new Intent(ParkingData.this, SettingOwnerPage.class);
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
                    Intent intent = new Intent(ParkingData.this, Selection.class);
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
            Intent intent = new Intent(ParkingData.this, OwnerHomePage.class);
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

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Returning to Home Page", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(ParkingData.this, OwnerHomePage.class));
        finish();
    }
}

