package com.example.parkingzone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javaFiles.NewUser;

public class SignUpOwner extends AppCompatActivity {
    TextInputLayout email,pass,phone;
    Button register;
    TextView already;
    NewUser newUser;
    FirebaseAuth mAuth;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReferenceFromUrl("https://parking-zone-8ce19.firebaseio.com/");
    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_owner);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        initialise();

        already.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpOwner.this, Selection.class));
                finish();
            }
        });
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() !=null){
                    startActivity(new Intent(SignUpOwner.this,SignUpOwner2.class));
                    finish();
                }
            }
        };
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String mailid = email.getEditText().getText().toString().trim();
//                String passwo = pass.getEditText().getText().toString().trim();
//                String phon = phone.getEditText().getText().toString().trim();
                if(!validateEmail(email.getEditText().getText().toString().trim()) | !validatephoneno(phone.getEditText().getText().toString().trim()) | !validatePassword(pass.getEditText().getText().toString().trim()))
                    Toast.makeText(SignUpOwner.this, "Invalid", Toast.LENGTH_SHORT).show();
                else{
                    email.setError(null);
                    pass.setError(null);
                    phone.setError(null);
                    newUser.setMail(email.getEditText().getText().toString().trim());
                    newUser.setPass(pass.getEditText().getText().toString().trim());
                    newUser.setPhone(phone.getEditText().getText().toString().trim());
                    mAuth.createUserWithEmailAndPassword(email.getEditText().getText().toString().trim(),pass.getEditText().getText().toString().trim())
                            .addOnCompleteListener(SignUpOwner.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(!task.isSuccessful())
                                        Toast.makeText(SignUpOwner.this, "Email ID already Exist", Toast.LENGTH_SHORT).show();
                                    else{
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        if (user != null) {
                                            final DatabaseReference usersRef = ref.child("Owner").child(user.getUid()).child("Owner Details");
                                            usersRef.setValue(newUser);
                                        }
                                    }
                                }
                            });
                }
            }
        });
    }

    private void initialise() {
        register = (Button) findViewById(R.id.submit);
        email = (TextInputLayout) findViewById(R.id.mail_id);
        phone = (TextInputLayout) findViewById(R.id.phone_number);
        pass = (TextInputLayout) findViewById(R.id.password);
        already = (TextView) findViewById(R.id.already);
        newUser = new NewUser();
        mAuth = FirebaseAuth.getInstance();
    }
    private boolean validatePassword(String password) {
        if(password.isEmpty()) {
            pass.setError("Please enter password");
            return false;
        }else{
            pass.setError(null);
            return true;
        }
    }
    private boolean validateEmail(String email1)
    {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email1);
        if (email1.isEmpty()) {
            email.setError("This Field can't be Empty");
            return false;
        } else if (!matcher.matches()) {
            email.setError("Please Enter a valid email ID");
            return false;
        } else {
            email.setError(null);
            return true;
        }
    }
    private boolean validatephoneno(String phone_no1)
    {
        if(phone_no1.isEmpty()){
            phone.setError("This Field can't be Empty");
            return false;
        }
        else if(phone_no1.length() != 10){
            phone.setError("Please enter a 10 digit Number");
            return false;
        }
        else {
            phone.setError(null);
            return true;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }
}
