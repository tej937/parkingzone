package com.example.parkingzone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OwnerHomePage extends AppCompatActivity {
    Button signout,update_details;
    RelativeLayout update_layout;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReferenceFromUrl("https://parking-zone-8ce19.firebaseio.com");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_home_page);
        initialise();
        checkDetails();

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(OwnerHomePage.this,Selection.class);
                int flag = 0;
                Bundle bundle = new Bundle();
                bundle.putString("flag", String.valueOf(flag));
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
        update_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OwnerHomePage.this,SignUpOwner2.class));
                finish();
            }
        });

    }

    private void checkDetails() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference check_details = ref.child("Parker").child(user.getUid()).child("Vehicle Details");
        check_details.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    Toast.makeText(OwnerHomePage.this, "All required data uploaded", Toast.LENGTH_SHORT).show();
                else{
                    update_layout.setVisibility(View.VISIBLE);
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

        signout = (Button) findViewById(R.id.signout);
        update_details = (Button) findViewById(R.id.update);
        update_layout = (RelativeLayout) findViewById(R.id.update_layout);

    }

}
