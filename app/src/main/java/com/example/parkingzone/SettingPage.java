package com.example.parkingzone;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javaFiles.NewCar;
import javaFiles.NewUser;

public class SettingPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout mDraw;
    NavigationView navigationView;
    ActionBarDrawerToggle mToggle;
    Dialog myDialog;
    View headerView;

    RelativeLayout profilePageOpener;
    TextView username,phone_number;

    NewUser newUser;
    NewCar newCar;
    SwitchCompat darkMode;


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReferenceFromUrl("https://parking-zone-8ce19.firebaseio.com");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_page);

        initialise();
        retrieveData();
        retrieveCarData();
        profilePageOpener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingPage.this, UserProfile.class));
                finish();
            }
        });

        darkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(SettingPage.this, "Light Mode Upadate coming soon", Toast.LENGTH_SHORT).show();
                    darkMode.setChecked(false);
                }
            }
        });

    }
    private void initialise() {
        mDraw = (DrawerLayout)findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDraw,R.string.open,R.string.close);
        mDraw.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView)findViewById(R.id.navigation);
        headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        myDialog = new Dialog(this);

        username = (TextView) findViewById(R.id.username);
        phone_number = (TextView) findViewById(R.id.phone_number);
        profilePageOpener = (RelativeLayout) findViewById(R.id.profile_page);
        newUser = new NewUser();
        newCar = new NewCar();
        darkMode = findViewById(R.id.drive);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Black)));
        actionBar.setTitle("Setting");
    }

    private void retrieveData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final DatabaseReference usersRef = ref.child("Parker").child(user.getUid()).child("Parker Details");
            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        getDataFromFirebase(dataSnapshot);
                    } else {
                        Toast.makeText(SettingPage.this, "Chutiya kut gaya ", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(SettingPage.this, "U have failed this City " + databaseError, Toast.LENGTH_SHORT).show();
                }
            });
        }
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
                        Toast.makeText(SettingPage.this, "Please Register your Car ", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(SettingPage.this, "U have failed this City " + databaseError, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getCarDataFromFirebase(DataSnapshot dataSnapshot) {
        newCar.setName((String) dataSnapshot.child("name").getValue());
        username.setText(newCar.getName());
    }

    private void getDataFromFirebase(DataSnapshot dataSnapshot) {
        newUser.setPhone((String) dataSnapshot.child("phone").getValue());
        phone_number.setText(newUser.getPhone());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.parking) {
            Intent intent = new Intent(SettingPage.this, MyParking.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.setting) {
            Intent intent = new Intent(SettingPage.this, SettingPage.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.payment) {
            Intent intent = new Intent(SettingPage.this, PaymentMethod.class);
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
                    Intent intent = new Intent(SettingPage.this, Selection.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            });
            return true;
        }else if(id == R.id.home){
            Intent intent = new Intent(SettingPage.this,HomePage.class);
            startActivity(intent);
            finish();
            return true;
        }
        else
            return false;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

}
