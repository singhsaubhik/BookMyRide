package com.kiit.bike.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kiit.bike.R;
import com.kiit.bike.module.ContactUsModel;
import com.kiit.bike.utility.Pref;

public class ContactUsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText etTitle,etDescription;
    private Button btnSubmit;
    private Pref pref;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        initialize();
        peformAction();
    }

    private void initialize(){
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#000000"));
        toolbar.setTitle("Support");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);

        pref = new Pref(this);

        progressDialog = new ProgressDialog(ContactUsActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        etTitle = (EditText)findViewById(R.id.etTitle);
        etDescription = (EditText)findViewById(R.id.etDescription);
        btnSubmit = (Button)findViewById(R.id.btnSubmit);
    }

    private void peformAction(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etTitle.getText().toString().length()>0){
                    if (etDescription.getText().toString().length()>0){
                         sendForm();
                    }else {
                        etDescription.setError("Enter Description");
                        etDescription.requestFocus();
                    }
                }else {
                    etTitle.setError("Enter Title");
                    etTitle.requestFocus();
                }
            }
        });
    }

    private void sendForm(){
        progressDialog.show();
        ContactUsModel contactUsModel = new ContactUsModel();
        contactUsModel.setUser_email(pref.getEmail());
        contactUsModel.setTitle(etTitle.getText().toString().trim());
        contactUsModel.setDescription(etDescription.getText().toString().trim());
        FirebaseDatabase.getInstance().getReference().child("Support").push().setValue(contactUsModel, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError!=null){

                }else {
                    Toast.makeText(getApplicationContext(),"Successfully Send",Toast.LENGTH_SHORT).show();
                    progressDialog.hide();
                    etTitle.setText("");
                    etDescription.setText("");
                    onBackPressed();
                }
            }
        });
    }
}
