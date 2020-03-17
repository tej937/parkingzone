package com.example.parkingzone;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.math.BigDecimal;

import Utils.Constants;
import javaFiles.CheckOutDetails;

public class PaymentMethod extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout mDraw;
    NavigationView navigationView;
    ActionBarDrawerToggle mToggle;
    public static final int PAYPAL_REQUEST_CODE = 7171;

    Dialog myDialog;
    View headerView;
    private static PayPalConfiguration configuration = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Constants.Pay_pal_ID);
    RelativeLayout paypalButton, gpayButton, cod;
    String total_amount;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReferenceFromUrl("https://parking-zone-8ce19.firebaseio.com");
    long maxId;
    CheckOutDetails checkOutDetails;
    private PaymentsClient paymentsClient;
    private View mGooglePayButton;
    private TextView mGooglePayStatusText;
    private boolean alreadyExecuted = false;

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);
        initialise();
        //
//
//                paymentsClient = PaymentsUtils.createPaymentsClient(this);
//        possiblyShowGooglePayButton();
//
//        mGooglePayButton.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        try {
//                            requestPayment(view);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//
//        Wallet.WalletOptions walletOptions = new Wallet.WalletOptions.Builder()
//                .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
//                .build();
//        paymentsClient = Wallet.getPaymentsClient(this,walletOptions);
//
//        try {
//            IsReadyToPayRequest isReadyToPayRequest = IsReadyToPayRequest.fromJson(baseConfigurationJson().toString());
//            Task<Boolean> task = paymentsClient.isReadyToPay(isReadyToPayRequest);
//            task.addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
//                @Override
//                public void onComplete(@NonNull Task<Boolean> task) {
//                    if(task.isSuccessful()){
//                        setGooglePayAvailable(task.getResult());
//                    }else {
//                            Log.w("isReadyToPay failed", task.getException());
//                    }
//                }
//            });
//
//        } catch (JSONException e) {
//            Log.w("Error", String.valueOf(e));
//        }
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, configuration);
        startService(intent);
        paypalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveData();
            }
        });
        cod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PaymentMethod.this, QR_CodeDisplay.class).putExtra("flag", "2"));
                Toast.makeText(PaymentMethod.this, "Please pay at Counter", Toast.LENGTH_SHORT).show();
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
        checkOutDetails = new CheckOutDetails();
        paypalButton = findViewById(R.id.method1);
        cod = findViewById(R.id.method4);
//        mGooglePayButton = findViewById(R.id.googlepay_button);
//        mGooglePayStatusText = findViewById(R.id.googlepay_status);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Black)));

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
                                    Toast.makeText(PaymentMethod.this, "No Booking Found", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(PaymentMethod.this, "U have failed this City " + databaseError, Toast.LENGTH_SHORT).show();
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
        checkOutDetails.setTotal_amount((String) dataSnapshot.child("total_amount").getValue());
        total_amount = checkOutDetails.getTotal_amount();
        if (!alreadyExecuted) {
            processPayment();
            alreadyExecuted = true;
        }
    }

    private void processPayment() {
        double price = Double.parseDouble(total_amount) / 72.81;
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(price), "USD", "Parking Cost", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, configuration);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }


//    @RequiresApi(api = Build.VERSION_CODES.N)
//    private void possiblyShowGooglePayButton() {
//        final Optional<JSONObject> isReadyToPayJson = PaymentsUtils.getIsReadyToPayRequest();
//        if (!isReadyToPayJson.isPresent()) {
//            return;
//        }
//        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.get().toString());
//        if (request == null) {
//            return;
//        }
//        Task<Boolean> task = paymentsClient.isReadyToPay(request);
//        task.addOnCompleteListener(this,
//                new OnCompleteListener<Boolean>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Boolean> task) {
//                        if (task.isSuccessful()) {
//                            setGooglePayAvailable(task.getResult());
//                        } else {
//                            Log.w("isReadyToPay failed", task.getException());
//                        }
//                    }
//                });
//    }
//    private void setGooglePayAvailable(Boolean result) {
//        if (result) {
//            mGooglePayStatusText.setVisibility(View.GONE);
//            mGooglePayButton.setVisibility(View.VISIBLE);
//        } else {
//            mGooglePayStatusText.setText(R.string.googlepay_status_unavailable);
//        }
//    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            // value passed in AutoResolveHelper
//            case LOAD_PAYMENT_DATA_REQUEST_CODE:
//                switch (resultCode) {
//                    case Activity.RESULT_OK:
//                        PaymentData paymentData = PaymentData.getFromIntent(data);
//                        handlePaymentSuccess(paymentData);
//                        break;
//                    case Activity.RESULT_CANCELED:
//                        // Nothing to here normally - the user simply cancelled without selecting a
//                        // payment method.
//                        break;
//                    case AutoResolveHelper.RESULT_ERROR:
//                        Status status = AutoResolveHelper.getStatusFromIntent(data);
//                        handleError(status.getStatusCode());
//                        break;
//                    default:
//                        // Do nothing.
//                }
//
//                // Re-enables the Google Pay payment button.
//                mGooglePayButton.setClickable(true);
//                break;
//        }
//    }
//
//    private void handlePaymentSuccess(PaymentData paymentData) {
//        String paymentInformation = paymentData.toJson();
//
//        // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
//        if (paymentInformation == null) {
//            return;
//        }
//        JSONObject paymentMethodData;
//
//        try {
//            paymentMethodData = new JSONObject(paymentInformation).getJSONObject("paymentMethodData");
//            // If the gateway is set to "example", no payment information is returned - instead, the
//            // token will only consist of "examplePaymentMethodToken".
//            if (paymentMethodData
//                    .getJSONObject("tokenizationData")
//                    .getString("type")
//                    .equals("PAYMENT_GATEWAY")
//                    && paymentMethodData
//                    .getJSONObject("tokenizationData")
//                    .getString("token")
//                    .equals("examplePaymentMethodToken")) {
//                AlertDialog alertDialog =
//                        new AlertDialog.Builder(this)
//                                .setTitle("Warning")
//                                .setMessage(
//                                        "Gateway name set to \"example\" - please modify "
//                                                + "Constants.java and replace it with your own gateway.")
//                                .setPositiveButton("OK", null)
//                                .create();
//                alertDialog.show();
//            }
//
//            String billingName =
//                    paymentMethodData.getJSONObject("info").getJSONObject("billingAddress").getString("name");
//            Log.d("BillingName", billingName);
//            Toast.makeText(this, getString(R.string.payments_show_name, billingName), Toast.LENGTH_LONG)
//                    .show();
//
//            // Logging token string.
//            Log.d("GooglePaymentToken", paymentMethodData.getJSONObject("tokenizationData").getString("token"));
//        } catch (JSONException e) {
//            Log.e("handlePaymentSuccess", "Error: " + e.toString());
//            return;
//        }
//    }
//
//    private void handleError(int statusCode) {
//        Log.w("loadPaymentData failed", String.format("Error code: %d", statusCode));
//    }
//
//    // This method is called when the Pay with Google button is clicked.
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    public void requestPayment(View view) throws JSONException {
//        // Disables the button to prevent multiple clicks.
//        mGooglePayButton.setClickable(false);
//
//        PaymentDataRequest request =
//                PaymentDataRequest.fromJson(getTransactionInfo(total_amount.getText().toString()).toString());
//
//        // Since loadPaymentData may show the UI asking the user to select a payment method, we use
//        // AutoResolveHelper to wait for the user interacting with it. Once completed,
//        // onActivityResult will be called with the result.
//        if (request != null) {
//            AutoResolveHelper.resolveTask(
//                    paymentsClient.loadPaymentData(request), this, LOAD_PAYMENT_DATA_REQUEST_CODE);
//        }
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Toast.makeText(this, "INSIDE ON Activity", Toast.LENGTH_SHORT).show();
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    startActivity(new Intent(this, QR_CodeDisplay.class).putExtra("flag", "1"));
                    //Toast.makeText(this, "Success and now go fucking back to checkoutactivity", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == Activity.RESULT_CANCELED)
                Toast.makeText(this, "Cancel" + requestCode + " and " + resultCode, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Result Not OK " + requestCode + "and" + resultCode, Toast.LENGTH_SHORT).show();
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
            Toast.makeText(this, "Invalid " + requestCode + "and" + resultCode, Toast.LENGTH_SHORT).show();
        else {
            Toast.makeText(this, "Result Code didnt match " + requestCode + "and" + resultCode, Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.parking) {
            Intent intent = new Intent(PaymentMethod.this, MyParking.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.setting) {
            Intent intent = new Intent(PaymentMethod.this, SettingPage.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.payment) {
            Intent intent = new Intent(PaymentMethod.this, PaymentMethod.class);
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
                    Intent intent = new Intent(PaymentMethod.this, Selection.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            });
            return true;
        }else if(id == R.id.home){
            Intent intent = new Intent(PaymentMethod.this,HomePage.class);
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
