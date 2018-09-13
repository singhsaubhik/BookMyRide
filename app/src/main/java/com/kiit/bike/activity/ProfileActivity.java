package com.kiit.bike.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kiit.bike.R;
import com.kiit.bike.module.UserModule;
import com.kiit.bike.utility.Constants;
import com.kiit.bike.utility.Pref;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    EditText etFName,etLName,etEmail,etMobile;
    CircleImageView imgProfile;
    FloatingActionButton fabImagePick;
    LinearLayout llSave;
    Pref pref;
    private Toolbar toolbar;
    private Dialog dialog;
    private Uri imageUri;
    private final int CAMERA_REQUEST = 1;
    private final int GALLERY_REQUEST = 2;
    private final int CAMERA_PERMISSION_REQ = 3;
    private final int STORAGE_PERMISSION_REQ = 4;

    private String encodedImage;
    private Uri filePath =null;
    private ProgressDialog progressDialog;
    String imgUrl="";
    private RequestOptions requestOptions;
    boolean isClickedOnCamera = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initialize();
        peformAction();
    }
    
    private void initialize(){
        pref = new Pref(this);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#000000"));
        toolbar.setTitle("Edit Profile");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);


        requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.nouser);
        requestOptions.error(R.drawable.nouser);

        etFName = (EditText)findViewById(R.id.etFName);
        etLName = (EditText)findViewById(R.id.etLName);
        etEmail = (EditText)findViewById(R.id.etEmail);
        etMobile = (EditText)findViewById(R.id.etMobile);

        imgProfile = (CircleImageView)findViewById(R.id.imgProfile);
        fabImagePick = (FloatingActionButton)findViewById(R.id.fabImagePick);
        llSave = (LinearLayout)findViewById(R.id.llSave);

        String[] splited = pref.getName().split("\\s+");
        if (splited.length>1){
            etFName.setText(splited[0]);
            etLName.setText(splited[1]);
        }else {
            etFName.setText(splited[0]);
        }

        etEmail.setText(pref.getEmail());
        etMobile.setText(pref.getMobile());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();

        if (!pref.getEmail().equals("")) {
            progressDialog = new ProgressDialog(ProfileActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            StorageReference riversRef = storageReference.child(Constants.USER_IMAGE_PATH +pref.getEmail().replaceAll("\\.", "").replaceAll("@", "") + ".png");
            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    progressDialog.hide();
                    Glide.with(ProfileActivity.this)
                            .load(uri)
                            .apply(requestOptions)
                            .into(imgProfile);


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    progressDialog.hide();
                    imgProfile.setImageResource(R.drawable.nouser);
                }
            });
        }
    }
    
    private void peformAction(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        fabImagePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData();
            }
        });
    }

    private void setUpProgressDialog() {

        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public void updateData() {

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String userid = pref.getEmail().replaceAll("\\.", "").replaceAll("@", "");
        mDatabase.child("User").child(userid).child("name").setValue(etFName.getText().toString().trim()+" "+etLName.getText().toString().trim());
        pref.saveName(etFName.getText().toString()+" "+etLName.getText().toString().trim());
        mDatabase.child("User").child(userid).child("mobile").setValue(Long.parseLong(etMobile.getText().toString().trim()));
        pref.saveMobile(etMobile.getText().toString());
      //
        Toast.makeText(ProfileActivity.this,"Successfully updated",Toast.LENGTH_SHORT).show();
       // pref.saveImage("");
        onBackPressed();


    }

    private void selectImage(){
        Log.d("dialog","Profile");
        dialog = new Dialog(ProfileActivity.this,R.style.Custom_dialogue_theme);
        dialog.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        dialog.setContentView(R.layout.profile_image_chooser_layout);
        dialog.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.profile_image_chooser_title_layout);
        LinearLayout llCameraCapture = (LinearLayout) dialog.findViewById(R.id.llCameraCapture);
        LinearLayout llCallery = (LinearLayout)dialog.findViewById(R.id.llCallery);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                //TODO: Nothing Done Yet..
            }
        });


        llCameraCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // cameraIntent();
                isClickedOnCamera = true;
                reqestForCameraPermission();
            }
        });

        llCallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  galleryIntent();
                isClickedOnCamera = false;
                requestForStoragePermission();
            }
        });

        dialog.show();
    }

    private void reqestForCameraPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            cameraIntent();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showDialogForPermission("You have to give permission for camera to access this feature");
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQ);
            }
        }
    }

    private void requestForStoragePermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            galleryIntent();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showDialogForPermission("You have to give permission for storage to access this feature");
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQ);
            }
        }
    }

    private void showDialogForPermission(String msg) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(ProfileActivity.this);
        builder1.setMessage(msg);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        openSettingPage();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void openSettingPage() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSION_REQ: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    reqestForCameraPermission();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    showDialogForPermission("You have to give permission for camera to access this feature");
                }
                return;
            }

            case STORAGE_PERMISSION_REQ: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    if (isClickedOnCamera) {
                        cameraIntent();
                    } else {
                        galleryIntent();
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    showDialogForPermission("You have to give permission for storage to access this feature");
                }
                return;
            }

        }


    }

    private void cameraIntent(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Profile Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        // Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    private void galleryIntent(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(dialog.isShowing())
        {
            dialog.dismiss();
        }
        switch(requestCode) {
            case CAMERA_REQUEST:

                if (resultCode == RESULT_OK) {
                    try {
                        try {
                            filePath = imageUri;;
                            Log.d("filePath", String.valueOf(filePath));
                            String imageurl = /*"file://" +*/ getRealPathFromURI(imageUri);
                            BitmapFactory.Options o = new BitmapFactory.Options();
                            o.inSampleSize = 6;
                            Bitmap bm = cropToSquare(BitmapFactory.decodeFile(imageurl, o));
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bm.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
                            byte[] b = baos.toByteArray();
                            imgProfile.setVisibility(View.VISIBLE);
                            imgProfile.setImageBitmap(bm);

                            uploadFile();



                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                    }

                }
                break;
            case GALLERY_REQUEST:
                if(resultCode == RESULT_OK){
                    filePath = data.getData();
                    Log.d("filePathGallery",String.valueOf(filePath));
                    imgProfile.setImageURI(filePath);
                    uploadFile();

                }
        }

    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor =getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    public static Bitmap cropToSquare(Bitmap bitmap){
        int width  = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = (height > width) ? width : height;
        int newHeight = (height > width)? height - ( height - width) : height;
        int cropW = (width - height) / 2;
        cropW = (cropW < 0)? 0: cropW;
        int cropH = (height - width) / 2;
        cropH = (cropH < 0)? 0: cropH;
        Bitmap cropImg = Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);

        return cropImg;
    }


    private void uploadFile() {
        // progressDialog.show();

        if (filePath != null) {
            if (!filePath.equals("")) {
                progressDialog = new ProgressDialog(ProfileActivity.this);
                progressDialog.setMessage("Please wait...");
                progressDialog.setCanceledOnTouchOutside(false);

                String imageName = pref.getEmail().replaceAll("\\.", "").replaceAll("@", "") + ".png";
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                final StorageReference riversRef;

                riversRef = storageReference.child(Constants.USER_IMAGE_PATH  + imageName);



                riversRef.putFile(filePath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //    imgUrl = taskSnapshot.getDownloadUrl().toString();
                                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        progressDialog.hide();
                                        //Bitmap hochladen
                                        imgUrl = uri.toString();
                                        Log.d("SoumyaUrl",imgUrl);
                                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                        String userid = pref.getEmail().replaceAll("\\.", "").replaceAll("@", "");
                                        mDatabase.child("User").child(userid).child("profile_img").setValue(imgUrl);
                                     //   pref.saveImage(imgUrl);

                                    }
                                });





                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {

                                progressDialog.hide();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                                progressDialog.show();
                                progressDialog.hide();
                            }
                        })
                        ;
            }else {
                //progressDialog.hide();
            }
        } else {
        }
    }
}
