package com.example.parkingzone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javaFiles.NewCar;
import javaFiles.NewUser;

public class UserProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout mDraw;
    NavigationView navigationView;
    ActionBarDrawerToggle mToggle;
    Dialog myDialog;
    View headerView;

    TextView name,phone,mail,password,lice_plate,name_text;
    TextInputLayout name_edit,phone_edit,mail_edit,password_edit,lice_plate_edit;
    ImageView edit;
    Button update;
    NewUser newUser;
    NewCar newCar;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReferenceFromUrl("https://parking-zone-8ce19.firebaseio.com");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        initialise();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Black)));
        actionBar.setTitle("User Profile");
        retrieveData();
        retrieveCarData();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setVisibility(View.GONE);
                name_text.setVisibility(View.VISIBLE);
                name_edit.setVisibility(View.VISIBLE);

                phone.setVisibility(View.GONE);
                phone_edit.setVisibility(View.VISIBLE);
                mail.setVisibility(View.GONE);
                mail_edit.setVisibility(View.VISIBLE);
                password.setVisibility(View.GONE);
                password_edit.setVisibility(View.VISIBLE);
                lice_plate.setVisibility(View.GONE);
                lice_plate_edit.setVisibility(View.VISIBLE);
                update.setVisibility(View.VISIBLE);
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateEmail(mail_edit.getEditText().getText().toString().trim()) | !validatephoneno(phone_edit.getEditText().getText().toString().trim())
                        | !validatePassword(password_edit.getEditText().getText().toString().trim()) | !validateName(name_edit.getEditText().getText().toString())
                        | !validatePlate(lice_plate_edit.getEditText().getText().toString()))
                    Toast.makeText(UserProfile.this, "Invalid", Toast.LENGTH_SHORT).show();
                else {
                    mail_edit.setError(null);
                    phone_edit.setError(null);
                    password_edit.setError(null);
                    name_edit.setError(null);
                    lice_plate_edit.setError(null);
                    newUser.setMail(mail_edit.getEditText().getText().toString());
                    newUser.setPass(password_edit.getEditText().getText().toString());
                    newUser.setPhone(phone_edit.getEditText().getText().toString());
                    newCar.setName(name_edit.getEditText().getText().toString());
                    newCar.setPlate(lice_plate_edit.getEditText().getText().toString());

                    name.setVisibility(View.VISIBLE);
                    name_text.setVisibility(View.GONE);
                    name_edit.setVisibility(View.GONE);

                    phone.setVisibility(View.VISIBLE);
                    phone_edit.setVisibility(View.GONE);
                    mail.setVisibility(View.VISIBLE);
                    mail_edit.setVisibility(View.GONE);
                    password.setVisibility(View.VISIBLE);
                    password_edit.setVisibility(View.GONE);
                    lice_plate.setVisibility(View.VISIBLE);
                    lice_plate_edit.setVisibility(View.GONE);
                    update.setVisibility(View.GONE);

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        final DatabaseReference usersRef = ref.child("Parker").child(user.getUid()).child("Parker Details");
                        usersRef.setValue(newUser);
                        final DatabaseReference userCar = ref.child("Parker").child(user.getUid()).child("Vehicle Details");
                        userCar.setValue(newCar);
                        retrieveData();
                        retrieveCarData();
                        Toast.makeText(UserProfile.this, "Success", Toast.LENGTH_SHORT).show();

                        user.updatePassword(newUser.getPass()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                    Toast.makeText(UserProfile.this, "Password Upadated", Toast.LENGTH_SHORT).show(); updateMail();
                            }
                        });
                    }
                }
                }
        });
    }

    private void updateMail()
    {
        FirebaseUser email_user = FirebaseAuth.getInstance().getCurrentUser();
        if(email_user != null) {
            email_user.updateEmail(newUser.getMail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                        Toast.makeText(UserProfile.this, "Mail ID updated", Toast.LENGTH_SHORT).show();
                }
            });
        }else
            Toast.makeText(UserProfile.this, "Failure", Toast.LENGTH_SHORT).show();
    }
    private void retrieveCarData()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final DatabaseReference usersRef = ref.child("Parker").child(user.getUid()).child("Vehicle Details");
            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                        getCarDataFromFirebase(dataSnapshot);
                    else
                        Toast.makeText(UserProfile.this, "Please Register your Car ", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(UserProfile.this, "U have failed this City " + databaseError, Toast.LENGTH_SHORT).show();
                }
            });
        }
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
                    }else{
                        Toast.makeText(UserProfile.this, "Chutiya kut gaya ", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(UserProfile.this, "U have failed this City " + databaseError, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getDataFromFirebase(DataSnapshot dataSnapshot) {
        newUser.setMail((String) dataSnapshot.child("mail").getValue());
        newUser.setPhone((String) dataSnapshot.child("phone").getValue());
        newUser.setPass((String) dataSnapshot.child("pass").getValue());

        mail.setText(newUser.getMail());
        phone.setText(newUser.getPhone());
        password.setText(newUser.getPass());
    }
    private void getCarDataFromFirebase(DataSnapshot dataSnapshot){
        newCar.setPlate((String) dataSnapshot.child("plate").getValue());
        newCar.setName((String) dataSnapshot.child("name").getValue());

        lice_plate.setText(newCar.getPlate());
        name.setText(newCar.getName());
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

        edit = (ImageView) findViewById(R.id.edit);
        update = (Button) findViewById(R.id.update);

        name = (TextView) findViewById(R.id.username);
        name_text = (TextView) findViewById(R.id.username_text);
        mail = (TextView) findViewById(R.id.email_id_text);
        phone = (TextView) findViewById(R.id.mobile_phone_text);
        lice_plate = (TextView) findViewById(R.id.lice_no_text);
        password = (TextView) findViewById(R.id.password_text);

        name_edit = (TextInputLayout) findViewById(R.id.username_edit);
        mail_edit = (TextInputLayout) findViewById(R.id.email_id_edit);
        phone_edit = (TextInputLayout) findViewById(R.id.mobile_phone_edit);
        lice_plate_edit = (TextInputLayout) findViewById(R.id.lice_no_edit);
        password_edit = (TextInputLayout) findViewById(R.id.password_edit);

        newUser = new NewUser();
        newCar = new NewCar();
    }
    private boolean validatePassword(String password) {
        if(password.isEmpty()) {
            password_edit.setError("This Field can't be Empty");
            return false;
        }else{
            password_edit.setError(null);
            return true;
        }
    }
    private boolean validateEmail(String email1)
    {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email1);
        if (email1.isEmpty()) {
            mail_edit.setError("This Field can't be Empty");
            return false;
        } else if (!matcher.matches()) {
            mail_edit.setError("Please Enter a valid email ID");
            return false;
        } else {
            mail_edit.setError(null);
            return true;
        }
    }
    private boolean validatephoneno(String phone_no1)
    {
        if(phone_no1.isEmpty()){
            phone_edit.setError("This Field can't be Empty");
            return false;
        }
        else if(phone_no1.length() != 10){
            phone_edit.setError("Please enter a 10 digit Number");
            return false;
        }
        else {
            phone_edit.setError(null);
            return true;
        }
    }
    private boolean validateName(String name) {
        if(name.isEmpty()){
            name_edit.setError("This Field can't be Empty");
            return false;
        }
        else {
            name_edit.setError(null);
            return true;
        }
    }
    private boolean validatePlate(String plate_no) {
        if(plate_no.isEmpty()){
            lice_plate_edit.setError("This Field can't be Empty");
            return false;
        }
        else {
            lice_plate_edit.setError(null);
            return true;
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.parking) {
            Intent intent = new Intent(UserProfile.this, MyParking.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.setting) {
            Intent intent = new Intent(UserProfile.this, SettingPage.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.payment) {
            Intent intent = new Intent(UserProfile.this, PaymentMethod.class);
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
                    Intent intent = new Intent(UserProfile.this, Selection.class);
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
            Intent intent = new Intent(UserProfile.this,HomePage.class);
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
