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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javaFiles.NewOwner;
import javaFiles.NewUser;

public class SettingOwnerPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout mDraw;
    NavigationView navigationView;
    ActionBarDrawerToggle mToggle;
    Dialog myDialog;
    View headerView;

    NewUser newUser;
    NewOwner newOwner;
    ElegantNumberButton elegantNumberButton;
    TextView username, phone_number, closed, admin_name, tnc_text;
    EditText admin;
    ImageView tnc;
    SwitchCompat status, dark_Mode;
    Button button;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReferenceFromUrl("https://parking-zone-8ce19.firebaseio.com");
    int flag = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_owner_page);
        initiate();
        retrieveData();
        retrieveCarData();

        admin_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                admin_name.setVisibility(View.GONE);
                tnc_text.setVisibility(View.GONE);
                tnc.setVisibility(View.GONE);
                admin.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (admin.getText().toString().isEmpty()) {
                    admin.setError("Please enter the admin name");
                } else {
                    admin_name.setVisibility(View.VISIBLE);
                    tnc_text.setVisibility(View.VISIBLE);
                    tnc.setVisibility(View.VISIBLE);
                    admin_name.setText(admin.getText().toString());
                    admin.setVisibility(View.GONE);
                    button.setVisibility(View.GONE);
                }
            }
        });


        status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    closed.setText("Opened");
                    flag = 1;
                    checkActiveStatus();
                } else {
                    closed.setText("Closed");
                }
            }
        });
        dark_Mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(SettingOwnerPage.this, "Light Mode Upadate coming soon", Toast.LENGTH_SHORT).show();
                    dark_Mode.setChecked(false);
                }
            }
        });

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

        username = (TextView) findViewById(R.id.username);
        phone_number = (TextView) findViewById(R.id.phone_number);
        closed = findViewById(R.id.closed);
        status = findViewById(R.id.status_switch);
        dark_Mode = findViewById(R.id.dark);
        elegantNumberButton = findViewById(R.id.slots);
        admin_name = findViewById(R.id.admin_name);
        tnc_text = findViewById(R.id.question_text);
        tnc = findViewById(R.id.tnc);
        button = findViewById(R.id.submit);
        admin = findViewById(R.id.admin_name_edit);


        newUser = new NewUser();
        newOwner = new NewOwner();
    }

    private void checkActiveStatus() {
        if (flag == 1) {
            startActivity(new Intent(SettingOwnerPage.this, OwnerHomePage.class));
            finish();
            Toast.makeText(this, "Parking Opened", Toast.LENGTH_SHORT).show();
        }
    }

    private void retrieveCarData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final DatabaseReference usersRef = ref.child("Owner").child(user.getUid()).child("Parking Details");
            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                        getCarDataFromFirebase(dataSnapshot);
                    else
                        Toast.makeText(SettingOwnerPage.this, "Please Register your Car ", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(SettingOwnerPage.this, "U have failed this City " + databaseError, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void retrieveData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final DatabaseReference usersRef = ref.child("Owner").child(user.getUid()).child("Owner Details");
            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        getDataFromFirebase(dataSnapshot);
                    } else {
                        Toast.makeText(SettingOwnerPage.this, "Chutiya kut gaya ", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(SettingOwnerPage.this, "U have failed this City " + databaseError, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void getDataFromFirebase(DataSnapshot dataSnapshot) {
        newUser.setPhone((String) dataSnapshot.child("phone").getValue());
        phone_number.setText(newUser.getPhone());
    }
    private void getCarDataFromFirebase(DataSnapshot dataSnapshot) {
        newOwner.setParking_name((String) dataSnapshot.child("parking_name").getValue());
        newOwner.setSlots((String) dataSnapshot.child("slots").getValue());
        username.setText(newOwner.getParking_name());
        elegantNumberButton.setNumber(newOwner.getSlots());
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.parking) {
            Intent intent = new Intent(SettingOwnerPage.this, ParkingData.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.setting) {
            Intent intent = new Intent(SettingOwnerPage.this, SettingOwnerPage.class);
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
                    Intent intent = new Intent(SettingOwnerPage.this, Selection.class);
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
            Intent intent = new Intent(SettingOwnerPage.this, OwnerHomePage.class);
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

}
