package com.example.parkingzone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Selection extends AppCompatActivity {

    Button user,owner;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    int flag = 0;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    ProgressDialog progressDialog;
    DatabaseReference ref = database.getReferenceFromUrl("https://parking-zone-8ce19.firebaseio.com");

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        //hasActiveInternetConnection(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        user = (Button) findViewById(R.id.user);
        owner = (Button) findViewById(R.id.parking_owner);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(Selection.this);
        progressDialog.setMessage("Checking if user already exist..."); // Setting Message
        progressDialog.setTitle("Please Wait"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() !=null)
                    checkTypeOfUser();

            }
        };
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 1;
                Intent intent = new Intent(Selection.this,LoginActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("flag", String.valueOf(flag));
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
        owner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 2;
                Intent intent = new Intent(Selection.this,LoginActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("flag", String.valueOf(flag));
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

    }
    private void checkTypeOfUser() {
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference check_user = ref.child("Parker").child(user.getUid()).child("Parker Details");
        check_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    //Toast.makeText(Selection.this, "Authentication Successful", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Selection.this, HomePage.class));
                    progressDialog.dismiss();
                    finish();
                } else {
                    progressDialog.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        DatabaseReference check_owner = ref.child("Owner").child(user.getUid()).child("Owner Details");
        check_owner.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    //Toast.makeText(Selection.this, "Authentication Successful", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Selection.this, OwnerHomePage.class));
                    progressDialog.dismiss();
                    finish();
                } else {
                    progressDialog.dismiss();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    //    public  boolean hasActiveInternetConnection(Context context) {
//        if (isNetworkAvailable()) {
//            try {
//                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
//                urlc.setRequestProperty("User-Agent", "Test");
//                urlc.setRequestProperty("Connection", "close");
//                urlc.setConnectTimeout(1500);
//                urlc.connect();
//                return (urlc.getResponseCode() == 200);
//            } catch (IOException e) {
//                Toast.makeText(context, "Error in checking internet connectivity", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(context, "No internet connectivity can be fetched", Toast.LENGTH_SHORT).show();
//        }
//        return false;
//    }
//    private boolean isNetworkAvailable() {
//        ConnectivityManager connectivityManager
//                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//        return activeNetworkInfo != null;
//    }
    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }
}
