package com.iva.bike.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iva.bike.R;
import com.iva.bike.module.BikeModule;
import com.iva.bike.module.BookModule;
import com.iva.bike.module.UserModule;
import com.iva.bike.utility.Pref;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalOAuthScopes;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class BookActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView imgProduct;
    private RequestOptions requestOptions;
    private LinearLayout llStatus,llCart;
    private TextView tvProductName,tvProductDescription,tvProductAddress,tvProductPrice,tvProductColor,tvStartDate,tvEndDate,tvProductNumber,tvProductStatus;
    private String bike_id,bike_des,bike_color,bike_address,bike_title,bike_price,bike_img,bike_number,bike_status,bike_latitude,bike_longitude,email,start_date,end_date;
    private Pref pref;
    private ProgressDialog progressDialog;
    long diff;
    long days;
    SimpleDateFormat sformat = new SimpleDateFormat("dd-MM-yyyy");
    Date startDate,endDate,checkStartDate,checkEndDate;
    double totalPrice;
    private AlertDialog alertDialog;
    private static final String CONFIG_CLIENT_ID = "AaKWjq-vUdxd1AiUXMEk7AsL9ZQf2EvC75Q0YokfTSdyO9HuCqkUTfHRpMnRbxHHHY0lzzrOcOzQ9ZqH";
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            // The following are only used in PayPalFuturePaymentActivity.
            .merchantName("Example Merchant")
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        initialize();
        peformAction();
    }

    private void initialize(){
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#000000"));
        toolbar.setTitle("Book A Bike");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);

        tvProductName = (TextView)findViewById(R.id.tvProductName);
        tvProductDescription = (TextView)findViewById(R.id.tvProductDescription);
        tvProductAddress = (TextView)findViewById(R.id.tvProductAddress);
        tvProductPrice = (TextView)findViewById(R.id.tvProductPrice);
        tvProductColor = (TextView)findViewById(R.id.tvProductColor);
        tvStartDate = (TextView)findViewById(R.id.tvStartDate);
        tvEndDate = (TextView)findViewById(R.id.tvEndDate);
        tvProductNumber = (TextView)findViewById(R.id.tvProductNumber);
        tvProductStatus = (TextView)findViewById(R.id.tvProductStatus);
        imgProduct = (ImageView)findViewById(R.id.imgProduct);
        llStatus = (LinearLayout)findViewById(R.id.llStatus);
        llCart = (LinearLayout)findViewById(R.id.llCart);
        pref = new Pref(this);

        if (getIntent().getExtras()!=null){
            bike_id = getIntent().getStringExtra("bike_id");
            bike_des = getIntent().getStringExtra("bike_des");
            bike_color = getIntent().getStringExtra("bike_color");
            bike_address = getIntent().getStringExtra("bike_address");
            bike_title = getIntent().getStringExtra("bike_title");
            bike_price = getIntent().getStringExtra("bike_price");
            bike_img = getIntent().getStringExtra("bike_img");
            bike_number = getIntent().getStringExtra("bike_number");
            bike_status = getIntent().getStringExtra("bike_status");
            bike_latitude = getIntent().getStringExtra("bike_latitude");
            bike_longitude = getIntent().getStringExtra("bike_longitude");
            email = getIntent().getStringExtra("email");
            start_date = getIntent().getStringExtra("start_date");
            end_date = getIntent().getStringExtra("end_date");

        }


        try {
            startDate = sformat.parse(start_date);
            endDate = sformat.parse(end_date);
            diff = endDate.getTime() - startDate.getTime();
            days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.demo_user);
        requestOptions.error(R.drawable.demo_user);

        tvProductName.setText(bike_title);
        tvProductDescription.setText(bike_des);
        tvProductAddress.setText(bike_address);
        totalPrice = Double.parseDouble(bike_price) *(days+1);
        tvProductPrice.setText("Rs " +totalPrice);
        tvProductColor.setText("Bike Color : "+bike_color);
        tvProductNumber.setText(bike_number);
        tvStartDate.setText(start_date);
        tvEndDate.setText(end_date);
        if (!TextUtils.isEmpty(bike_img)) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference riversRef = storageReference.child(bike_img);
            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(BookActivity.this)
                            .load(uri)
                            .apply(requestOptions)
                            .into(imgProduct);


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    imgProduct.setImageResource(R.drawable.demo_user);
                }
            });
        }else {
            imgProduct.setImageResource(R.drawable.demo_user);
        }

        if (bike_status.equals("ON")){
            tvProductStatus.setText("Available");
            llStatus.setBackgroundColor(Color.parseColor("#15c205"));
        }else {
            tvProductStatus.setText("Booked");
            llStatus.setBackgroundColor(Color.parseColor("#ffcc0000"));
        }
    }

    private void peformAction(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        llCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  paymentModePopup();
                checkDate();
            }
        });
    }

    private void checkDate(){
        setUpProgressDialog();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Book").child(bike_id);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               progressDialog.hide();
               if (dataSnapshot.getValue()!=null) {
                   for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                       final BookModule bookModule = dataSnapshot1.getValue(BookModule.class);
                       try {
                           checkStartDate = sformat.parse(bookModule.getStart_date());
                           checkEndDate = sformat.parse(bookModule.getEnd_date());
                       } catch (ParseException e) {
                           e.printStackTrace();
                       }
                       

                   }
                   if (startDate.after(checkStartDate) && startDate.before(checkEndDate)) {

                       Toast.makeText(getApplicationContext(),"Bike is not available on that day",Toast.LENGTH_SHORT).show();
                       //   break;


                   } else if (endDate.after(checkStartDate) && endDate.before(checkEndDate)) {

                       Toast.makeText(getApplicationContext(),"Bike is not available on that day",Toast.LENGTH_SHORT).show();
                       // break;

                   } else if (sformat.format(startDate).equals(sformat.format(checkStartDate))) {

                       Toast.makeText(getApplicationContext(),"Bike is not available on that day",Toast.LENGTH_SHORT).show();
                       //  break;

                   } else if (sformat.format(endDate).equals(sformat.format(checkEndDate))) {

                       Toast.makeText(getApplicationContext(),"Bike is not available on that day",Toast.LENGTH_SHORT).show();
                       // break;

                   } else{
                       paymentModePopup();

                   }
               }else {
                   paymentModePopup();
               }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.hide();
            }
        });

    }

    private void checkBookingTable(final String paymentMode, final String transaction_id){
        setUpProgressDialog();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Book");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = 0;

                if (dataSnapshot.hasChild(bike_id)) {
                    DataSnapshot comments = dataSnapshot.child(bike_id);
                    count = comments.getChildrenCount();

                }
                createBooking(String.valueOf(count),paymentMode,transaction_id);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.hide();
            }
        });

    }

    private void createBooking(String count, final String paymentMode, String transaction_id){

        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child("Book");
        BookModule bookModule = new BookModule();
        bookModule.setBike_color(bike_color);
        bookModule.setBike_title(bike_title);
        bookModule.setBike_des(bike_des);
        bookModule.setBike_price(String.valueOf(totalPrice));
        bookModule.setBike_address(bike_address);
        bookModule.setBike_id(bike_id);
        bookModule.setBike_number(bike_number);
        bookModule.setBike_img(bike_img);
        bookModule.setBike_status(bike_status);
        bookModule.setStart_date(start_date);
        bookModule.setEnd_date(end_date);
        bookModule.setOwner_email(email);
        bookModule.setCustomer_email(pref.getEmail());
        bookModule.setTransaction_id(transaction_id);
        bookModule.setPaymentMode(paymentMode);

        dataRef.child(bike_id).child(count).setValue(bookModule, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                progressDialog.hide();
                if (databaseError != null) {

                } else {
                    pref.saveBikeId(bike_id);
                    if (paymentMode.equals("COD")) {
                        Toast.makeText(getApplicationContext(), "Successfully Booking", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(BookActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }else {
                        getTotalEarning();
                    }

                }

            }
        });
    }

    private void setUpProgressDialog(){

        progressDialog = new ProgressDialog(BookActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void paymentModePopup(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(BookActivity.this, R.style.CustomDialogNew);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_payment, null);
        TextView tvCancel = (TextView) dialogView.findViewById(R.id.tvCancel);
        TextView tvCash = (TextView) dialogView.findViewById(R.id.tvCash);
        TextView tvPaytm = (TextView) dialogView.findViewById(R.id.tvPaytm);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

            }
        });
        tvPaytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);

                    Intent intent = new Intent(BookActivity.this, com.paypal.android.sdk.payments.PaymentActivity.class);

                    // send the same configuration for restart resiliency
                    intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

                    intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYMENT, thingToBuy);

                    startActivityForResult(intent, REQUEST_CODE_PAYMENT);

            }
        });


        tvCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBookingTable("COD","");
            }
        });




        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(true);
        Window window = alertDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        alertDialog.show();
    }

    private PayPalPayment getThingToBuy(String paymentIntent) {
        Float paypalPrice=0.0f;
        paypalPrice = Float.parseFloat(String.valueOf(totalPrice));
        return new PayPalPayment(new BigDecimal(paypalPrice), "USD", "sample item",
                paymentIntent);
    }

    private PayPalOAuthScopes getOauthScopes() {
        /* create the set of required scopes
         * Note: see https://developer.paypal.com/docs/integration/direct/identity/attributes/ for mapping between the
         * attributes you select for this app in the PayPal developer portal and the scopes required here.
         */
        Set<String> scopes = new HashSet<String>(
                Arrays.asList(PayPalOAuthScopes.PAYPAL_SCOPE_EMAIL, PayPalOAuthScopes.PAYPAL_SCOPE_ADDRESS));
        return new PayPalOAuthScopes(scopes);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data
                        .getParcelableExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {

                        String transInfo = confirm.toJSONObject().toString(4);
                        JSONObject jsonObject = new JSONObject(transInfo);
                        JSONObject response = jsonObject.getJSONObject("response");
                        String create_time = response.optString("create_time");
                        String transaction_id = response.optString("id");
                        String intent = response.optString("intent");
                        String state = response.optString("state");
                        if (state.equals("approved")){

                            checkBookingTable("Paypal",transaction_id);

                        }



                        String id = confirm.getPayment().toJSONObject()
                                .toString(4);



                        Toast.makeText(getApplicationContext(), "Order placed",
                                Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                System.out.println("The user canceled.");
            } else if (resultCode == com.paypal.android.sdk.payments.PaymentActivity.RESULT_EXTRAS_INVALID) {
                System.out
                        .println("An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth = data
                        .getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("FuturePaymentExample", auth.toJSONObject()
                                .toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("FuturePaymentExample", authorization_code);

                        sendAuthorizationToServer(auth);
                        Toast.makeText(getApplicationContext(),
                                "Future Payment code received from PayPal",
                                Toast.LENGTH_LONG).show();



                    } catch (JSONException e) {
                        Log.e("FuturePaymentExample",
                                "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("FuturePaymentExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("FuturePaymentExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        }
    }

    private void sendAuthorizationToServer(PayPalAuthorization authorization) {

    }



    @Override
    public void onDestroy() {
        // Stop service when done
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    private void getTotalEarning(){
        setUpProgressDialog();
        String key = email.replaceAll("\\.", "").replaceAll("@", "");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child("User").child(key);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.hide();
                UserModule userModule = dataSnapshot.getValue(UserModule.class);
                String total = userModule.getTotal_earning();

                double totalEarning = Double.parseDouble(total)+totalPrice;
                updateMoney(String.valueOf(totalEarning));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
             progressDialog.hide();
            }
        });
    }

    private void updateMoney(String totalEarning){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String userid = email.replaceAll("\\.", "").replaceAll("@", "");
        mDatabase.child("User").child(userid).child("total_earning").setValue(totalEarning);
        Toast.makeText(getApplicationContext(), "Successfully Booking", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(BookActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }




}
