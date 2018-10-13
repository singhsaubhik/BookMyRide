package com.iva.bike.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.iva.bike.R;
import com.iva.bike.module.TransactionRequestModel;
import com.iva.bike.module.UserModule;
import com.iva.bike.utility.NetworkConnectionCheck;
import com.iva.bike.utility.Pref;

public class RevenueActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView tvRevenue;
    private LinearLayout llRequest;
    private ProgressDialog progressDialog;
    private Pref pref;
    private String total;
    private double subtotal;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue);
        initialize();
        performAction();
    }

    private void initialize(){
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#000000"));
        toolbar.setTitle("Revenue");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);

        pref = new Pref(this);

        tvRevenue = (TextView)findViewById(R.id.tvRevenue);
        llRequest = (LinearLayout)findViewById(R.id.llRequest);

        getTotalEarning();
    }

    private void performAction(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        llRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RevenueActivity.this, R.style.CustomDialog);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_paypal, null);
                final EditText etPrice=(EditText)dialogView.findViewById(R.id.etPrice);
                LinearLayout llPayPalSubmit=(LinearLayout)dialogView.findViewById(R.id.llPayPalSubmit);
                ImageView imgClose=(ImageView)dialogView.findViewById(R.id.imgClose);

                dialogBuilder.setView(dialogView);
                final AlertDialog alertDialog = dialogBuilder.create();
                Window window = alertDialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setGravity(Gravity.CENTER);
                alertDialog.show();
                alertDialog.setCancelable(false);
                llPayPalSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                          if (etPrice.getText().toString().length()>0) {
                              if (Double.parseDouble(total) >= 500) {
                                    requestForTransaction("paypal", etPrice.getText().toString().trim());
                                  Toast.makeText(getApplicationContext(), "Your Request Successfully Submitted", Toast.LENGTH_SHORT).show();
                                  alertDialog.dismiss();
                              } else {
                                  Toast.makeText(getApplicationContext(), "You have to earn 500 $", Toast.LENGTH_SHORT).show();
                              }
                          }else {
                              etPrice.setError("Enter Price");
                              etPrice.requestFocus();
                          }


                    }
                });

                imgClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
            }
        });
            }



    private void setUpProgressDialog(){

        progressDialog = new ProgressDialog(RevenueActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void getTotalEarning(){
        setUpProgressDialog();
        String key = pref.getEmail().replaceAll("\\.", "").replaceAll("@", "");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query query = ref.child("User").child(key);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.hide();
                UserModule userModule = dataSnapshot.getValue(UserModule.class);
                 total = userModule.getTotal_earning();
                tvRevenue.setText("$" +total);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.hide();
            }
        });
    }

    private void requestForTransaction(final String paymentMethod,String price)
    {
        setUpProgressDialog();
        TransactionRequestModel transactionRequestModel = new TransactionRequestModel();
        transactionRequestModel.setPrice(price);
        transactionRequestModel.setPaymentMethod(paymentMethod);
        transactionRequestModel.setPayTo(pref.getEmail());
        FirebaseDatabase.getInstance().getReference().child("Transaction").push().setValue(transactionRequestModel, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError==null){
                    progressDialog.hide();
                    Toast.makeText(getApplicationContext(),"transaction successfully completed",Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.hide();
                }
            }
        });

        subtotal =  Double.parseDouble(total) - Double.parseDouble(price);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String userid = pref.getEmail().replaceAll("\\.", "").replaceAll("@", "");
        mDatabase.child("User").child(userid).child("total_earning").setValue(String.valueOf(subtotal));


    }


}
