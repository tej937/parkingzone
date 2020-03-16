package com.example.parkingzone;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parkingzone.Adapters.Owner_Adapters;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import javaFiles.NewOwner;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Button update_profile;
    DrawerLayout mDraw;
    NavigationView navigationView;
    ActionBarDrawerToggle mToggle;
    Dialog myDialog;
    View headerView;
    RelativeLayout main_layout,update_layout;
    HorizontalScrollView news_layout, parking_layout;
    RelativeLayout news1, news2, news3, news4;
    TextView text3, text4;
    Spinner userArea;
    int flag;
    RecyclerView recyclerView;
    TextView textView;
    ArrayList<NewOwner> list;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReferenceFromUrl("https://parking-zone-8ce19.firebaseio.com");
    Owner_Adapters owner_adapters;
    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        initialise();
        checkDetails();



    update_profile.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(HomePage.this,SignUpActivity2.class));
            finish();
        }
    });
    news1.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String url = "https://www.ndtv.com/offbeat/honk-more-wait-more-how-mumbai-police-is-punishing-reckless-honkers-2172723";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
    });
    news2.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String url = "https://www.ndtv.com/mumbai-news/bmc-new-rules-from-today-pay-up-to-rs-23-000-for-illegal-parking-in-mumbai-2065619";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
    });
    news3.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String url = "https://www.ndtv.com/india-news/mumbai-most-traffic-congested-city-in-the-world-delhi-at-fourth-2048666";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
    });
    news4.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String url = "https://www.ndtv.com/topic/mumbai-traffic";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
    });

        userArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(HomePage.this, ""+position, Toast.LENGTH_SHORT).show();
                if (position == 0) {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText("Please select an area to park");
                } else {
                    textView.setVisibility(View.GONE);
                    //list.clear();
                    checkParkingPlaces();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void checkDetails() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference check_details = ref.child("Parker").child(user.getUid()).child("Vehicle Details");
        check_details.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() & dataSnapshot.getChildrenCount() == 4)
                    Log.d("Data Updated", "Yess All data exists");
                else{
                    main_layout.setVisibility(View.GONE);
                    parking_layout.setVisibility(View.GONE);
                    news_layout.setVisibility(View.GONE);
                    text3.setVisibility(View.GONE);
                    text4.setVisibility(View.GONE);
                    update_layout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void checkParkingPlaces() {
        progressDialog.show();
        progressDialog.setCancelable(false);
        final DatabaseReference reference = ref.child(userArea.getSelectedItem().toString()).child("OwnerInfo");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        NewOwner newOwner = dataSnapshot1.getValue(NewOwner.class);
                        list.add(newOwner);
                    }
                    owner_adapters = new Owner_Adapters(HomePage.this, list);
                    recyclerView.setAdapter(owner_adapters);
                    progressDialog.dismiss();
                    textView.setVisibility(View.GONE);
                } else {
                    list.clear();
                    owner_adapters = new Owner_Adapters(HomePage.this, list);
                    recyclerView.setAdapter(owner_adapters);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText("No parking available");
                    //Toast.makeText(HomePage.this, "Ooops Sorry !!!!!!!!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                //list.clear();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void initialise(){
        mDraw = (DrawerLayout)findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDraw,R.string.open,R.string.close);
        mDraw.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView)findViewById(R.id.navigation);
        headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        myDialog = new Dialog(this);

        main_layout = (RelativeLayout) findViewById(R.id.main_layout);
        update_layout = (RelativeLayout) findViewById(R.id.update_layout);
        update_profile = (Button) findViewById(R.id.update);
        news1 = (RelativeLayout) findViewById(R.id.news1);
        news2 = (RelativeLayout) findViewById(R.id.news2);
        news3 = (RelativeLayout) findViewById(R.id.news3);
        news4 = (RelativeLayout) findViewById(R.id.news4);
        news_layout = findViewById(R.id.news_scroll);
        parking_layout = findViewById(R.id.parking_layout);
        text3 = findViewById(R.id.text3);
        text4 = findViewById(R.id.text4);
        userArea = findViewById(R.id.search);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Checking with Area..."); // Setting Message
        progressDialog.setTitle("Please Wait"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// Progress Dialog Style Spinner


        ref.keepSynced(true);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));
        list = new ArrayList<NewOwner>();
        textView = findViewById(R.id.no_booking);

        //first_attempt = (RelativeLayout) findViewById(R.id.first_attempt);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Black)));
        actionBar.setTitle("Park Zone");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.parking) {
            Intent intent = new Intent(HomePage.this, MyParking.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.setting) {
            Intent intent = new Intent(HomePage.this, SettingPage.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.payment) {
            Intent intent = new Intent(HomePage.this, PaymentsOption.class);
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
                    Intent intent = new Intent(HomePage.this, Selection.class);
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
        }else if(id == R.id.home){
            Intent intent = new Intent(HomePage.this,HomePage.class);
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

