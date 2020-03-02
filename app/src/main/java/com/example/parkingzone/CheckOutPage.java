package com.example.parkingzone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

import Utils.PaymentsUtils;
import javaFiles.CheckOutDetails;
import javaFiles.NewCar;

public class CheckOutPage extends AppCompatActivity {

    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;
    TextView location, carName, start_time, end_time, parking_slot, total_amount, tempo, plate_no;
    NewCar newCar;
    //String paymentStatus;
    CheckOutDetails checkOutDetails;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReferenceFromUrl("https://parking-zone-8ce19.firebaseio.com");
    // PaymentsClient paymentsClient;
    long maxId;
    private PaymentsClient paymentsClient;
    private View mGooglePayButton;
    private TextView mGooglePayStatusText;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out_page);
        initialise();

        paymentsClient = PaymentsUtils.createPaymentsClient(this);
        possiblyShowGooglePayButton();

        mGooglePayButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        requestPayment(view);
                    }
                });

        retrieveData();
        retrieveCarData();
        checkPaymentStatus();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void possiblyShowGooglePayButton() {
        final Optional<JSONObject> isReadyToPayJson = PaymentsUtils.getIsReadyToPayRequest();
        if (!isReadyToPayJson.isPresent()) {
            return;
        }
        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.get().toString());
        if (request == null) {
            return;
        }
        Task<Boolean> task = paymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(this,
                new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            setGooglePayAvailable(task.getResult());
                        } else {
                            Log.w("isReadyToPay failed", task.getException());
                        }
                    }
                });
    }

    private void setGooglePayAvailable(Boolean result) {
        if (result) {
            mGooglePayStatusText.setVisibility(View.GONE);
            mGooglePayButton.setVisibility(View.VISIBLE);
        } else {
            mGooglePayStatusText.setText(R.string.googlepay_status_unavailable);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // value passed in AutoResolveHelper
            case LOAD_PAYMENT_DATA_REQUEST_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        handlePaymentSuccess(paymentData);
                        break;
                    case Activity.RESULT_CANCELED:
                        // Nothing to here normally - the user simply cancelled without selecting a
                        // payment method.
                        break;
                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        handleError(status.getStatusCode());
                        break;
                    default:
                        // Do nothing.
                }

                // Re-enables the Google Pay payment button.
                mGooglePayButton.setClickable(true);
                break;
        }
    }

    private void handlePaymentSuccess(PaymentData paymentData) {
        String paymentInformation = paymentData.toJson();

        // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
        if (paymentInformation == null) {
            return;
        }
        JSONObject paymentMethodData;

        try {
            paymentMethodData = new JSONObject(paymentInformation).getJSONObject("paymentMethodData");
            // If the gateway is set to "example", no payment information is returned - instead, the
            // token will only consist of "examplePaymentMethodToken".
            if (paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("type")
                    .equals("PAYMENT_GATEWAY")
                    && paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("token")
                    .equals("examplePaymentMethodToken")) {
                AlertDialog alertDialog =
                        new AlertDialog.Builder(this)
                                .setTitle("Warning")
                                .setMessage(
                                        "Gateway name set to \"example\" - please modify "
                                                + "Constants.java and replace it with your own gateway.")
                                .setPositiveButton("OK", null)
                                .create();
                alertDialog.show();
            }

            String billingName =
                    paymentMethodData.getJSONObject("info").getJSONObject("billingAddress").getString("name");
            Log.d("BillingName", billingName);
            Toast.makeText(this, getString(R.string.payments_show_name, billingName), Toast.LENGTH_LONG)
                    .show();

            // Logging token string.
            Log.d("GooglePaymentToken", paymentMethodData.getJSONObject("tokenizationData").getString("token"));
        } catch (JSONException e) {
            Log.e("handlePaymentSuccess", "Error: " + e.toString());
            return;
        }
    }

    private void handleError(int statusCode) {
        Log.w("loadPaymentData failed", String.format("Error code: %d", statusCode));
    }

    // This method is called when the Pay with Google button is clicked.
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void requestPayment(View view) {
        // Disables the button to prevent multiple clicks.
        mGooglePayButton.setClickable(false);

        // The price provided to the API should include taxes and shipping.
        // This price is not displayed to the user.
        String price = PaymentsUtils.microsToString(Long.parseLong(total_amount.getText().toString()));

        // TransactionInfo transaction = PaymentsUtil.createTransaction(price);
        Optional<JSONObject> paymentDataRequestJson = PaymentsUtils.getPaymentDataRequest(price);
        if (!paymentDataRequestJson.isPresent()) {
            return;
        }
        PaymentDataRequest request =
                PaymentDataRequest.fromJson(paymentDataRequestJson.get().toString());

        // Since loadPaymentData may show the UI asking the user to select a payment method, we use
        // AutoResolveHelper to wait for the user interacting with it. Once completed,
        // onActivityResult will be called with the result.
        if (request != null) {
            AutoResolveHelper.resolveTask(
                    paymentsClient.loadPaymentData(request), this, LOAD_PAYMENT_DATA_REQUEST_CODE);
        }
    }


    private void checkPaymentStatus() {
        if (tempo.getText().toString().equals("truth"))
            Toast.makeText(this, "Payment Successfull", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Please complete the Payment", Toast.LENGTH_SHORT).show();
    }

    private void initialise() {
        location = findViewById(R.id.location);
        carName = findViewById(R.id.car_name);
        start_time = findViewById(R.id.start_time);
        end_time = findViewById(R.id.end_time);
        parking_slot = findViewById(R.id.parking_spot);
        total_amount = findViewById(R.id.price);
        tempo = findViewById(R.id.temp);
        plate_no = findViewById(R.id.plate_no);

        checkOutDetails = new CheckOutDetails();
        newCar = new NewCar();

        mGooglePayButton = findViewById(R.id.googlepay_button);
        mGooglePayStatusText = findViewById(R.id.googlepay_status);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Black)));
        actionBar.setTitle("Check Out Page");

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
                        Toast.makeText(CheckOutPage.this, "No Car found Please register", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(CheckOutPage.this, "U have failed this City " + databaseError, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void retrieveData() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final DatabaseReference bookingId = ref.child("New Booking").child(user.getUid());
            bookingId.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        maxId = dataSnapshot.getChildrenCount();
                        DatabaseReference userRef = bookingId.child(String.valueOf(maxId));
                        userRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    //Toast.makeText(CheckOutPage.this, maxId+"", Toast.LENGTH_SHORT).show();
                                    getDataFromFirebase(dataSnapshot);
                                } else {
                                    Toast.makeText(CheckOutPage.this, "No Booking Found", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(CheckOutPage.this, "U have failed this City " + databaseError, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    private void getDataFromFirebase(DataSnapshot dataSnapshot) {
        checkOutDetails.setParking_slot((String) dataSnapshot.child("parking_slot").getValue());
        checkOutDetails.setStart_time((String) dataSnapshot.child("start_time").getValue());
        checkOutDetails.setEnd_time((String) dataSnapshot.child("end_time").getValue());
        checkOutDetails.setTotal_amount((String) dataSnapshot.child("total_amount").getValue());
        checkOutDetails.setPayment_status((String) dataSnapshot.child("payment_status").getValue());
        checkOutDetails.setCurrent_date((String) dataSnapshot.child("current_date").getValue());

        String start_date_time = checkOutDetails.getCurrent_date() + " , " + checkOutDetails.getStart_time();
        String end_date_time = checkOutDetails.getCurrent_date() + " , " + checkOutDetails.getEnd_time();
        tempo.setText(checkOutDetails.getPayment_status());
        start_time.setText(start_date_time);
        end_time.setText(end_date_time);
        parking_slot.setText(checkOutDetails.getParking_slot());
        total_amount.setText(checkOutDetails.getTotal_amount());
    }

    private void getCarDataFromFirebase(DataSnapshot dataSnapshot) {
        newCar.setCarType((String) dataSnapshot.child("carType").getValue());
        newCar.setPlate((String) dataSnapshot.child("plate").getValue());
        carName.setText(newCar.getCarType());
        plate_no.setText(newCar.getPlate());
    }
}

