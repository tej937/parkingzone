package com.example.parkingzone;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class PaymentsOption extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout mDraw;
    NavigationView navigationView;
    ActionBarDrawerToggle mToggle;
    Dialog myDialog;
    View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments_option);
        initialise();
    }

    private void initialise() {
        mDraw = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDraw, R.string.open, R.string.close);
        mDraw.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = (NavigationView) findViewById(R.id.navigation);
        headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        myDialog = new Dialog(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Black)));
        actionBar.setTitle("Payment Option");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.parking) {
            Intent intent = new Intent(PaymentsOption.this, MyParking.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.setting) {
            Intent intent = new Intent(PaymentsOption.this, SettingPage.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.payment) {
            Intent intent = new Intent(PaymentsOption.this, PaymentsOption.class);
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
                    Intent intent = new Intent(PaymentsOption.this, Selection.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            });
            return true;
        } else if (id == R.id.home) {
            Intent intent = new Intent(PaymentsOption.this, HomePage.class);
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
        startActivity(new Intent(PaymentsOption.this, HomePage.class));
        finish();
    }
}
