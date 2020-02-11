package com.example.parkingzone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    TextInputLayout email, pass;
    Button Login,forgot_Button;
    ImageView back;
    TextView nonexisting,forgot,forgot_Text,goback;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReferenceFromUrl("https://parking-zone-8ce19.firebaseio.com");
    int flag;
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Bundle bundle = getIntent().getExtras();
        flag = Integer.parseInt(bundle.getString("flag"));
        Toast.makeText(this, "Flag Value "+flag, Toast.LENGTH_SHORT).show();
        initialise();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,Selection.class));
                finish();
            }
        });
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pass.setVisibility(View.GONE);
                nonexisting.setVisibility(View.GONE);
                forgot.setVisibility(View.GONE);
                Login.setVisibility(View.GONE);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.topMargin = convertPixelsToDp(1000,LoginActivity.this);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                email.setLayoutParams(params);
                forgot_Text.setVisibility(View.VISIBLE);
                forgot_Button.setVisibility(View.VISIBLE);
                goback.setVisibility(View.VISIBLE);
                goback.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pass.setVisibility(View.VISIBLE);
                        nonexisting.setVisibility(View.VISIBLE);
                        forgot.setVisibility(View.VISIBLE);
                        Login.setVisibility(View.VISIBLE);
                        forgot_Text.setVisibility(View.GONE);
                        forgot_Button.setVisibility(View.GONE);
                        goback.setVisibility(View.GONE);
                    }
                });
                forgot_Button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(TextUtils.isEmpty(email.getEditText().getText().toString().trim())) email.setError("Please enter Registered Mail ID");
                        else if(!validateEmail(email.getEditText().getText().toString().trim())) email.setError("Please Enter a valid mail ID");
                        else{
                            email.setError(null);
                            mAuth.sendPasswordResetEmail(email.getEditText().getText().toString().trim())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                Toast.makeText(LoginActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                                pass.setVisibility(View.VISIBLE);
                                                nonexisting.setVisibility(View.VISIBLE);
                                                forgot.setVisibility(View.VISIBLE);
                                                Login.setVisibility(View.VISIBLE);
                                                forgot_Text.setVisibility(View.GONE);
                                                forgot_Button.setVisibility(View.GONE);
                                                goback.setVisibility(View.GONE);

                                            }else
                                                Toast.makeText(LoginActivity.this, "Failed to send reset email, Please enter registered Mail ID!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                    }
                });

            }
        });
        nonexisting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag == 1){
                    startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
                    finish();
                }else if(flag == 2){
                    startActivity(new Intent(LoginActivity.this,SignUp_Owner.class));
                    finish();
                }
            }
        });
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() !=null)
                    checkTypeOfUser();
            }
        };
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Inputemail = email.getEditText().getText().toString().trim();
                String Inputpass = pass.getEditText().getText().toString().trim();
                if (TextUtils.isEmpty(Inputemail) & TextUtils.isEmpty(Inputpass)) {
                    email.setError("This field can't be empty");
                    pass.setError("This field can't be empty");
                } else if (!validateEmail(Inputemail)) {
                    email.setError("Enter Valid Email ID");
                }else if(TextUtils.isEmpty(Inputpass)){
                    pass.setError("This field can't be empty");
                }else {
                    email.setError(null);
                    pass.setError(null);
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getEditText().getText().toString(), pass.getEditText().getText().toString())
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        String input = "Authentication Failed";
                                        input += "\n";
                                        input += "Please Enter Registered Mail ID and Password";
                                        input += "\n";
                                        input += "Or Create new account";
                                        Toast.makeText(LoginActivity.this, input, Toast.LENGTH_LONG).show();
                                    } else {
                                        checkTypeOfUser();
                                    }
                            }
                    });
                }
            }
        });
    }

    private void checkTypeOfUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(flag == 1) {
            DatabaseReference check_user = ref.child("Parker").child(user.getUid()).child("Parker Details");
            check_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Toast.makeText(LoginActivity.this, "Authentication Successful", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(LoginActivity.this, HomePage.class));
                        finish();
                        return;
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }else {
            Toast.makeText(this, "U selected Login as User \nPlease select Login as Owner\nin previous page", Toast.LENGTH_SHORT).show();
        }
        if(flag == 2) {
                DatabaseReference check_owner = ref.child("Owner").child(user.getUid()).child("Owner Details");
                check_owner.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(LoginActivity.this, "Authentication Successful", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(LoginActivity.this, OwnerHomePage.class));
                            finish();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }else {
            Toast.makeText(this, "U selected Login as Owner \nPlease select Login as User\nin previous page", Toast.LENGTH_SHORT).show();
        }
    }

    private void initialise() {
        Login = (Button) findViewById(R.id.submit);
        email = (TextInputLayout) findViewById(R.id.mail_id);
        pass = (TextInputLayout) findViewById(R.id.password);
        nonexisting = (TextView) findViewById(R.id.non_existing);
        forgot = (TextView) findViewById(R.id.forgot);
        mAuth = FirebaseAuth.getInstance();
        forgot_Text = (TextView) findViewById(R.id.forgot_text);
        forgot_Button = (Button) findViewById(R.id.forgot_submit);
        goback = (TextView) findViewById(R.id.go_back);
        back = (ImageView) findViewById(R.id.back);

    }

    private boolean validateEmail(String mail) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(mail);

        if (mail.isEmpty()) {
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

    public static int convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int dp = (int) (px / (metrics.densityDpi / 160f));
        return dp;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }
}