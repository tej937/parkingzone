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

import java.util.Arrays;

import javaFiles.NewCar;
import javaFiles.NewUser;
import javaFiles.Ratings;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class SettingPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout mDraw;
    NavigationView navigationView;
    ActionBarDrawerToggle mToggle;
    Dialog myDialog;
    View headerView;

    RelativeLayout profilePageOpener, FAQ_page, TnC_page, feedBack_page;
    TextView username,phone_number;

    NewUser newUser;
    NewCar newCar;
    SwitchCompat darkMode, notification;

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
                    Toast.makeText(SettingPage.this, "Light Mode Update Coming Soon!", Toast.LENGTH_SHORT).show();
                    darkMode.setChecked(false);
                }
            }
        });
        FAQ_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingPage.this, FAQ_Page.class));
                finish();
            }
        });
        TnC_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingPage.this, "Terms and Conditions to roll out soon.", Toast.LENGTH_SHORT).show();
            }
        });
        feedBack_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.setContentView(R.layout.feedback_layout);
                myDialog.show();
                MaterialRatingBar ratingBar;
                final Button submit;
                final EditText feedback;
                TextView feedback_text;
                //final float[] ratingStar = {(float) 0.0};
                final String[] rating1 = new String[1];
                ratingBar = myDialog.findViewById(R.id.rating);
                feedback_text = myDialog.findViewById(R.id.feedback);
                submit = myDialog.findViewById(R.id.submit);
                feedback = myDialog.findViewById(R.id.feedback_edit);
                ratingBar.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
                    @Override
                    public void onRatingChanged(MaterialRatingBar ratingBar, float rating) {
                        rating1[0] = String.valueOf(rating);
                        Ratings rat = new Ratings();
                        rat.setRating(Arrays.toString(rating1));
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        final DatabaseReference usersRef = ref.child("Rating").child("Rating Details").child(user.getUid());
                        usersRef.setValue(rat);
                        Toast.makeText(SettingPage.this, "Thank you for Feedback", Toast.LENGTH_SHORT).show();
                        myDialog.dismiss();
                    }
                });
                feedback_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        submit.setVisibility(View.VISIBLE);
                        feedback.setVisibility(View.VISIBLE);
                        submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Ratings rating = new Ratings();
//                                rating.setRating(Arrays.toString(rating1));
                                rating.setComments(feedback.getText().toString());
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                final DatabaseReference usersRef = ref.child("Rating").child("Rating Details").child(user.getUid());
                                usersRef.setValue(rating);
                                Toast.makeText(SettingPage.this, "Thank you for Comments", Toast.LENGTH_SHORT).show();
                                myDialog.dismiss();
                            }
                        });
                    }
                });
                Toast.makeText(SettingPage.this, "We hope you liked our App!", Toast.LENGTH_SHORT).show();
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

        FAQ_page = findViewById(R.id.faq_layout);
        feedBack_page = findViewById(R.id.question_layout);
        TnC_page = findViewById(R.id.tnc_layout);
        username = (TextView) findViewById(R.id.username);
        phone_number = (TextView) findViewById(R.id.phone_number);
        profilePageOpener = (RelativeLayout) findViewById(R.id.profile_page);
        newUser = new NewUser();
        newCar = new NewCar();
        darkMode = findViewById(R.id.drive);
        notification = findViewById(R.id.notification);


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
                        Toast.makeText(SettingPage.this, "No Data Found", Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(SettingPage.this, PaymentsOption.class);
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
