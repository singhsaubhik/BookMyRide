package com.kiit.bike.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kiit.bike.R;
import com.kiit.bike.module.UserModule;
import com.kiit.bike.utility.NetworkConnectionCheck;
import com.kiit.bike.utility.Pref;
import com.kiit.bike.utility.ValidationUtils;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail,etPassword;
    private Button btnLogin;
    private LinearLayout llSignUp;

    private NetworkConnectionCheck connectionCheck;;
    private ProgressDialog progressDialog;
    private Pref pref;
    private FirebaseAuth auth;
    private TextView tvPassword;
    private EditText etDialogEmailId;
    private LinearLayout llDialogSubmit;
    private Dialog dialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this);
        initialize();
        performAction();
    }

    private void initialize(){
        connectionCheck=new NetworkConnectionCheck(this);
        pref=new Pref(this);
        auth = FirebaseAuth.getInstance();

        etEmail = (EditText)findViewById(R.id.etEmail);
        etPassword = (EditText)findViewById(R.id.etPassword);

        btnLogin = (Button)findViewById(R.id.btnLogin);
        llSignUp = (LinearLayout)findViewById(R.id.llSignUp);
        tvPassword = (TextView)findViewById(R.id.tvPassword);
    }

    private void setUpProgressDialog(){

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }


    private void performAction(){
        llSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etEmail.getText().toString().trim().length()>0){
                    if (etPassword.getText().toString().trim().length()>0){
                        if (ValidationUtils.isValidEmail(etEmail.getText().toString().trim())){
                            if (connectionCheck.isNetworkAvailable()) {
                                loginProcess();
                            }else {
                                connectionCheck.getNetworkActiveAlert().show();
                            }
                        }else {
                            etEmail.setError("Enter Valid EmailId");
                        }
                    }else {
                        etPassword.requestFocus();
                        etPassword.setError("Enter Email");
                    }
                }else {
                    etEmail.requestFocus();
                    etEmail.setError("Enter Email");
                }
            }
        });

        tvPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(LoginActivity.this,android.R.style.Theme_Translucent);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.dialog_forgot_password);
                dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation2;
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                LinearLayout llCancel = (LinearLayout) dialog.findViewById(R.id.llCancel);
                etDialogEmailId=(EditText)dialog.findViewById(R.id.etDialogEmailId);
                llDialogSubmit=(LinearLayout)dialog.findViewById(R.id.llDialogSubmit);
                llDialogSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (etDialogEmailId.getText().toString()!=null) {
                            if(TextUtils.isEmpty(etDialogEmailId.getText().toString()))
                            {
                                etDialogEmailId.setError("Please enter email");
                                etDialogEmailId.requestFocus();
                                hideKeyboard(view);
                            }
                            else  if (!(ValidationUtils.isValidEmail(etDialogEmailId.getText().toString()))) {
                                etDialogEmailId.setError("Invalid email");
                                etDialogEmailId.requestFocus();
                                hideKeyboard(view);
                            } else {
                                dialog.dismiss();
                                String email = etDialogEmailId.getText().toString().trim();
                                callForgetPasswordMethod(email);
                                hideKeyboard(view);
                            }
                        }else {
                            Toast.makeText(getApplicationContext(),"Please enter email id",Toast.LENGTH_SHORT).show();
                        }
                    }
                });


                llCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hideKeyboard(view);
                        dialog.dismiss();


                    }
                });
                dialog.show();
            }
        });
    }

    private void loginProcess(){
        setUpProgressDialog();
        auth.signInWithEmailAndPassword(etEmail.getText().toString().trim(), etPassword.getText().toString().trim())
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), task.getException().getMessage().toString(),Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog.dismiss();
                      //     if (checkIfEmailVerified()) {
                                getUserDetails();
                          /*  }
                            else
                            {
                                Toast.makeText(LoginActivity.this, "Please verify your email", Toast.LENGTH_SHORT).show();
                            }*/

                        }
                    }
                });
    }

    private boolean checkIfEmailVerified()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.isEmailVerified())
        {
            // user is verified, so you can finish this activity or send user to activity which you want.
            return true;
        }
        else
        {

            FirebaseAuth.getInstance().signOut();
            return false;


        }
    }


    private void getUserDetails() {
        setUpProgressDialog();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("User");
        Query getProfiledetails = mDatabase.child(etEmail.getText().toString().trim().replaceAll("\\.", "").replaceAll("@", ""));
        getProfiledetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                if (dataSnapshot != null) {

                    UserModule userModel = dataSnapshot.getValue(UserModule.class);
                    if (userModel!=null) {
                        if (userModel.getPass().equals(etPassword.getText().toString().trim())) {
                            String email = userModel.getEmail();
                            String name = userModel.getName();
                            long mobile = userModel.getMobile();
                            pref.saveEmail(email);
                            pref.saveName(name);
                            pref.saveMobile(String.valueOf(mobile));

                            pref.saveType(userModel.getType());
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            //  finish();


                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "Invalid mobile number or password", Toast.LENGTH_SHORT).show();
                        }
                    }

                    else
                    {
                        Toast.makeText(LoginActivity.this, "Invalid mobile number or password", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Invalid mobile number or password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void callForgetPasswordMethod(String email){
        setUpProgressDialog();
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.hide();
                            Toast.makeText(getApplicationContext(), "Password reset mail has been sent to your mobile",Toast.LENGTH_SHORT).show();

                        } else {
                            progressDialog.hide();
                            Toast.makeText(getApplicationContext(), task.getException().getMessage().toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });






    }
}
