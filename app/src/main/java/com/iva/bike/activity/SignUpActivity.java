package com.iva.bike.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iva.bike.R;
import com.iva.bike.module.UserModule;
import com.iva.bike.utility.NetworkConnectionCheck;
import com.iva.bike.utility.Pref;

public class SignUpActivity extends AppCompatActivity {
    private LinearLayout llSignIn;
    private EditText etEmail,etName,etPhoneNumber,etCreatePassword;
    private Button btnSignUp;
    private NetworkConnectionCheck connectionCheck;
    private CheckBox checkBox;
    private String flag="User";
    private ProgressDialog progressDialog;
    private FirebaseAuth auth;
    private Pref pref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initialize();
        peformAction();
    }

    private void initialize(){
        connectionCheck = new NetworkConnectionCheck(this);
        pref=new Pref(SignUpActivity.this);
        auth = FirebaseAuth.getInstance();

        llSignIn = (LinearLayout) findViewById(R.id.llSignIn);
        etEmail = (EditText)findViewById(R.id.etEmail);
        etName = (EditText)findViewById(R.id.etName);
        etPhoneNumber = (EditText)findViewById(R.id.etPhoneNumber);
        etCreatePassword = (EditText)findViewById(R.id.etCreatePassword);
        checkBox = (CheckBox)findViewById(R.id.cb_Vendor);

        btnSignUp = (Button)findViewById(R.id.btnSignUp);
    }

    private void peformAction(){
        llSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    flag = "Admin";

                }else {
                    flag = "User";
                }
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etEmail.getText().toString().length()>0){
                    if (etName.getText().toString().length()>0){
                        if (etPhoneNumber.getText().toString().length()>0){
                            if (etCreatePassword.getText().toString().length()>0){
                               callSignupMethod(flag);
                            }else {
                                etCreatePassword.setError("Enter Password");
                                etCreatePassword.requestFocus();
                            }
                        }else {
                            etPhoneNumber.setError("Enter Phone Number");
                            etPhoneNumber.requestFocus();
                        }
                    }else {
                        etName.setError("Enter Name");
                        etName.requestFocus();
                    }
                }else {
                    etEmail.setError("Enter Email");
                    etEmail.requestFocus();
                }
            }
        });

    }

    private void setUpProgressDialog(){

        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void callSignupMethod(final String flag){
        setUpProgressDialog();
        auth.createUserWithEmailAndPassword(etEmail.getText().toString().trim(), etCreatePassword.getText().toString().trim())
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        } else {
                            if (connectionCheck.isNetworkAvailable()){
                                insertUSerDetails(flag);
                            }else {
                                connectionCheck.getNetworkActiveAlert().show();
                            }
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    private void insertUSerDetails(final String type){
        setUpProgressDialog();
        String userid = etEmail.getText().toString().trim().replaceAll("\\.", "").replaceAll("@", "");
        UserModule userModel = new UserModule();
        userModel.setEmail(etEmail.getText().toString().trim());
        userModel.setName(etName.getText().toString().trim());
        userModel.setPass(etCreatePassword.getText().toString().trim());
        userModel.setMobile(Long.parseLong(etPhoneNumber.getText().toString().trim()));
        userModel.setType(type);
        userModel.setTotal_earning("0");



        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child("User");
        dataRef.child(userid).setValue(userModel, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                progressDialog.dismiss();
                if (databaseError != null) {
                } else {
                    pref.saveEmail(etEmail.getText().toString().trim());
                    pref.saveMobile(etPhoneNumber.getText().toString().trim());
                    pref.saveName(etName.getText().toString().trim());
                    pref.saveType(type);
                    startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                    //sentVerificationCode(mEtEmail.getText().toString().trim());



                }

            }
        });
    }
}
