package com.example.parkingzone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.IOException;

import javaFiles.NewCar;

public class SignUpActivity2 extends AppCompatActivity {
    TextInputLayout userName, plateNo;
    RadioGroup type;
    ImageView back;
    ImageButton photo;
    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPCTURE_CODE = 1001;
    StorageReference storageReference;
    Button register;
    Uri filepath;
    NewCar newCar;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReferenceFromUrl("https://parking-zone-8ce19.firebaseio.com");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);
        initialise();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity2.this,SignUpActivity.class));
                finish();
            }
        });

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                   if(checkSelfPermission(Manifest.permission.CAMERA)
                           == PackageManager.PERMISSION_DENIED ||
                           checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                           == PackageManager.PERMISSION_DENIED){
                       String [] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                       requestPermissions(permission, PERMISSION_CODE);
                   }
                   else{
                       openCamera();
                   }
               }else{
                   openCamera();
               }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validateName(userName.getEditText().getText().toString().trim()) | !validatePlate(plateNo.getEditText().getText().toString().trim()))
                    Toast.makeText(SignUpActivity2.this, "Car Not Registered", Toast.LENGTH_SHORT).show();
                else{
                    userName.setError(null);
                    plateNo.setError(null);
                    newCar.setName(userName.getEditText().getText().toString().trim());
                    newCar.setPlate(plateNo.getEditText().getText().toString().trim());
                    int selectedId = type.getCheckedRadioButtonId();
                    RadioButton selectedType = (RadioButton) findViewById(selectedId);
                    newCar.setType(selectedType.getText().toString());
                    FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
                    if(user!=null){
                        final DatabaseReference usersRef = ref.child("Parker").child(user.getUid()).child("Vehicle Details");
                        usersRef.setValue(newCar);
                        startActivity(new Intent(SignUpActivity2.this,HomePage.class));
                        finish();
                        Toast.makeText(SignUpActivity2.this, "New Car Added", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                openCamera();
            else
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }
    private void openCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"New Picture");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"From Camera");
        filepath = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,filepath);
        startActivityForResult(cameraIntent,IMAGE_CAPCTURE_CODE);

    }

    private void initialise() {
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://parking-zone-8ce19.appspot.com/");
        newCar = new NewCar();
        userName = (TextInputLayout) findViewById(R.id.name);
        plateNo = (TextInputLayout) findViewById(R.id.lice_no);
        type = (RadioGroup) findViewById(R.id.type);
        register = (Button) findViewById(R.id.submit);
        back = (ImageView) findViewById(R.id.back);
        photo = (ImageButton) findViewById(R.id.photo);
        }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            photo.setImageURI(filepath);
            StorageReference files = storageReference.child("Licenses").child(filepath.getLastPathSegment());
            files.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(SignUpActivity2.this, "Uploaded", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
    private boolean validateName(String name) {
        if(name.isEmpty()){
            userName.setError("This Field can't be Empty");
            return false;
        }
        else {
            userName.setError(null);
            return true;
        }
    }
    private boolean validatePlate(String plate_no) {
        if(plate_no.isEmpty()){
            plateNo.setError("This Field can't be Empty");
            return false;
        }
        else {
            plateNo.setError(null);
            return true;
        }
    }
}
