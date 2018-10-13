package com.iva.bike.activity;

import android.Manifest;
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
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.github.angads25.toggle.LabeledSwitch;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iva.bike.R;
import com.iva.bike.module.BikeModule;
import com.iva.bike.module.UserModule;
import com.iva.bike.utility.Constants;
import com.iva.bike.utility.Pref;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Random;


public class CreateBikeActivity extends AppCompatActivity {
    AppCompatSpinner spBikeColor;
    EditText etBikeTitle, etBikeDescription, etBikePrice, etBikeNumber;
    ImageView imgBikePhoto;
    LinearLayout llAddPhotoProduct, llCreate;
    TextView tvBikeAddress, tvCreate;
    Toolbar toolbar;
    Pref pref;
    String bike_latitude, bike_longitude;
    ArrayList<String> colorList = new ArrayList<>();
    String color = "";
    String address = "";
    ProgressDialog progressDialog;
    Dialog dialog;
    private Uri imageUri;
    private final int CAMERA_REQUEST = 2;
    private final int GALLERY_REQUEST = 3;
    private final int CAMERA_PERMISSION_REQ = 4;
    private final int STORAGE_PERMISSION_REQ = 5;


    private String encodedImage;
    private Uri filePath = null;
    String imgUrl = "";
    String imgLink = "";
    private RequestOptions requestOptions;
    LabeledSwitch labeledSwitch;
    String bike_status = "ON";
    String flag = "";
    String bike_id;
    GeoFire geofire;
    boolean isClickedOnCamera = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_bike);
        initialize();
        peformAction();
    }

    private void initialize() {

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#000000"));
        toolbar.setTitle("Create Ride");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);

        pref = new Pref(this);

        requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.demo_user);
        requestOptions.error(R.drawable.demo_user);


        spBikeColor = (AppCompatSpinner) findViewById(R.id.spBikeColor);
        etBikeTitle = (EditText) findViewById(R.id.etBikeTitle);
        etBikeDescription = (EditText) findViewById(R.id.etBikeDescription);
        etBikePrice = (EditText) findViewById(R.id.etBikePrice);
        etBikeNumber = (EditText) findViewById(R.id.etBikeNumber);
        tvBikeAddress = (TextView) findViewById(R.id.tvBikeAddress);
        tvCreate = (TextView) findViewById(R.id.tvCreate);
        imgBikePhoto = (ImageView) findViewById(R.id.imgBikePhoto);
        llAddPhotoProduct = (LinearLayout) findViewById(R.id.llAddPhotoProduct);
        llCreate = (LinearLayout) findViewById(R.id.llCreate);
        labeledSwitch = findViewById(R.id.switchStatus);

        colorList.add("--Select Bike Color--");
        colorList.add("Black");
        colorList.add("White");
        colorList.add("Blue");
        colorList.add("Grey");
        colorList.add("Red");
        colorList.add("Green");
        colorList.add("Yellow");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, colorList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spBikeColor.setAdapter(dataAdapter);

        if (getIntent().getExtras() != null) {
            flag = getIntent().getStringExtra("flag");
            if (flag.equals("update")) {
                toolbar.setTitle("Edit Ride");
                bike_id = getIntent().getStringExtra("bike_id");
                etBikeDescription.setText(getIntent().getStringExtra("bike_des"));
                etBikeTitle.setText(getIntent().getStringExtra("bike_title"));
                etBikePrice.setText(getIntent().getStringExtra("bike_price"));
                etBikeNumber.setText(getIntent().getStringExtra("bike_number"));
                color = getIntent().getStringExtra("bike_color");
                if (color.equals("Black")) {
                    spBikeColor.setSelection(1);
                } else if (color.equals("White")) {
                    spBikeColor.setSelection(2);
                } else if (color.equals("Blue")) {
                    spBikeColor.setSelection(3);
                } else if (color.equals("Grey")) {
                    spBikeColor.setSelection(4);
                } else if (color.equals("Red")) {
                    spBikeColor.setSelection(5);
                } else if (color.equals("Green")) {
                    spBikeColor.setSelection(6);
                } else if (color.equals("Yellow")) {
                    spBikeColor.setSelection(7);
                }

                bike_latitude = getIntent().getStringExtra("bike_latitude");
                bike_longitude = getIntent().getStringExtra("bike_longitude");
                address = getIntent().getStringExtra("bike_address");
                tvBikeAddress.setText(address);


                bike_status = getIntent().getStringExtra("bike_status");
                if (bike_status.equals("ON")) {
                    labeledSwitch.setOn(true);
                } else {
                    labeledSwitch.setOn(false);
                }

                tvCreate.setText("EDIT");

                imgLink = getIntent().getStringExtra("bike_img");
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                StorageReference riversRef = storageReference.child(imgLink);
                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(CreateBikeActivity.this)
                                .load(uri)
                                .apply(requestOptions)
                                .into(imgBikePhoto);


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        imgBikePhoto.setImageResource(R.drawable.demo_user);
                    }
                });


            }
        }


    }

    private void peformAction() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tvBikeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateBikeActivity.this, AddressActivity.class);
                intent.putExtra("flag", "create");
                startActivityForResult(intent, 1);
            }
        });

        spBikeColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    color = adapterView.getItemAtPosition(i).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        labeledSwitch.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(LabeledSwitch labeledSwitch, boolean isOn) {
                // Implement your switching logic here
                if (isOn) {
                    bike_status = "ON";
                } else {
                    bike_status = "OFF";
                }
            }
        });

        llAddPhotoProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        llCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag.equals("update")) {
                    updateData();
                } else {
                    if (!color.equals("")) {
                        if (etBikeTitle.getText().toString().trim().length() > 0) {
                            if (etBikeDescription.getText().toString().trim().length() > 0) {
                                if (etBikePrice.getText().toString().length() > 0) {
                                    if (!address.equals("")) {
                                      //  if (!filePath.equals("")) {
                                            if (etBikeNumber.getText().toString().length() > 0) {
                                                uploadFile();
                                            } else {
                                                etBikeNumber.setError("Enter Bike Number");
                                            }
                                      /*  } else {
                                            Toast.makeText(getApplicationContext(), "Add Image", Toast.LENGTH_SHORT).show();
                                        }*/
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Select Bike Address", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    etBikePrice.setError("Enter Bike Price");
                                }
                            } else {
                                etBikeDescription.setError("Enter Bike Name");
                            }
                        } else {
                            etBikeTitle.setError("Enter Bike Name");
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Select Bike Color", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == 1) {

                address = data.getStringExtra("flagLocation");

                tvBikeAddress.setText(address);
                bike_latitude = data.getStringExtra("latitude");
                bike_longitude = data.getStringExtra("longitude");


            }

        } else {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            switch (requestCode) {
                case CAMERA_REQUEST:

                    if (resultCode == RESULT_OK) {
                        try {
                            try {
                                filePath = imageUri;
                                ;
                                Log.d("filePath", String.valueOf(filePath));
                                String imageurl = /*"file://" +*/ getRealPathFromURI(imageUri);
                                BitmapFactory.Options o = new BitmapFactory.Options();
                                o.inSampleSize = 6;
                                Bitmap bm = cropToSquare(BitmapFactory.decodeFile(imageurl, o));
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bm.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
                                byte[] b = baos.toByteArray();


                                imgBikePhoto.setVisibility(View.VISIBLE);
                                imgBikePhoto.setImageBitmap(bm);
                                if (flag.equals("update")) {
                                    uploadFileEdit();
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (OutOfMemoryError e) {
                            e.printStackTrace();
                        }

                    }
                    break;
                case GALLERY_REQUEST:
                    if (resultCode == RESULT_OK) {
                        filePath = data.getData();
                        Log.d("filePathGallery", String.valueOf(filePath));
                        if (flag.equals("update")) {
                            uploadFileEdit();
                        }
                        imgBikePhoto.setImageURI(filePath);
                    }
            }
        }
    }

    private void setUpProgressDialog() {

        progressDialog = new ProgressDialog(CreateBikeActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void createBike(String imgLink) {
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child("Bike");
        final String key = dataRef.push().getKey();
        String userid = pref.getEmail().trim().replaceAll("\\.", "").replaceAll("@", "");
        BikeModule bikeModule = new BikeModule();
        bikeModule.setBike_color(color);
        bikeModule.setBike_title(etBikeTitle.getText().toString().trim());
        bikeModule.setBike_des(etBikeDescription.getText().toString().trim());
        bikeModule.setBike_price(etBikePrice.getText().toString().trim());
        bikeModule.setBike_address(address);
        bikeModule.setBike_latitude(Double.parseDouble(bike_latitude));
        bikeModule.setBike_longitude(Double.parseDouble(bike_longitude));
        bikeModule.setBike_id(key);
        bikeModule.setBike_number(etBikeNumber.getText().toString().trim());
        bikeModule.setBike_img(imgLink);
        bikeModule.setBike_status(bike_status);
        bikeModule.setEmail(pref.getEmail().trim());

        dataRef.child(key).setValue(bikeModule, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                progressDialog.dismiss();
                if (databaseError != null) {
                    pref.saveBikeImage("");
                } else {

                    pref.saveBikeImage("");
                    createGeoFireTable(key, Double.parseDouble(bike_latitude), Double.parseDouble(bike_longitude));

                }

            }
        });
    }

    private long generateReferCode() {
        Random random = new Random(System.nanoTime());

        int randomInt = random.nextInt(1000000000);
        return randomInt;
    }

    private void selectImage() {
        Log.d("dialog", "Profile");
        dialog = new Dialog(CreateBikeActivity.this, R.style.Custom_dialogue_theme);
        dialog.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        dialog.setContentView(R.layout.profile_image_chooser_layout);
        dialog.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.profile_image_chooser_title_layout);
        LinearLayout llCameraCapture = (LinearLayout) dialog.findViewById(R.id.llCameraCapture);
        LinearLayout llCallery = (LinearLayout) dialog.findViewById(R.id.llCallery);
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
                isClickedOnCamera = true;
                reqestForCameraPermission();
            }
        });

        llCallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            if (ActivityCompat.shouldShowRequestPermissionRationale(CreateBikeActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showDialogForPermission("You have to give permission for camera to access this feature");
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(CreateBikeActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQ);
            }
        }
    }

    private void requestForStoragePermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            galleryIntent();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(CreateBikeActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showDialogForPermission("You have to give permission for storage to access this feature");
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(CreateBikeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQ);
            }
        }
    }


    private void showDialogForPermission(String msg) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(CreateBikeActivity.this);
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

    private void cameraIntent() {
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

    private void galleryIntent() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST);
    }


    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static Bitmap cropToSquare(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = (height > width) ? width : height;
        int newHeight = (height > width) ? height - (height - width) : height;
        int cropW = (width - height) / 2;
        cropW = (cropW < 0) ? 0 : cropW;
        int cropH = (height - width) / 2;
        cropH = (cropH < 0) ? 0 : cropH;
        Bitmap cropImg = Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);

        return cropImg;
    }


    private void uploadFile() {

        if (filePath != null) {
            if (!filePath.equals("")) {

                final String imageName = generateReferCode() + ".png";
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                StorageReference riversRef;
                riversRef = storageReference.child(Constants.BIKE_IMAGE_PATH + imageName);
                riversRef.putFile(filePath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //  imgUrl = taskSnapshot.getDownloadUrl().toString();
                                pref.saveBikeImage(Constants.BIKE_IMAGE_PATH + imageName);

                                imgLink = Constants.BIKE_IMAGE_PATH + imageName;
                                //   progressDialog.hide();
                                createBike(imgLink);
                                //     progressDialog.hide();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                //  progressDialog.hide();
                                Log.d("Image", "2");
                                Log.d("Image", exception.toString());
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                setUpProgressDialog();
                                Log.d("Image", "3");
                                Log.d("Image", taskSnapshot.toString());
                            }
                        });
            } else {
                //  progressDialog.hide();
            }
        } else {
        }

    }

    private void uploadFileEdit() {

        if (filePath != null) {
            if (!filePath.equals("")) {
                progressDialog = new ProgressDialog(CreateBikeActivity.this);
                progressDialog.setMessage("Please wait...");
                progressDialog.setCanceledOnTouchOutside(false);


                final String imageName = generateReferCode() + ".png";
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                StorageReference riversRef;
                riversRef = storageReference.child(Constants.BIKE_IMAGE_PATH + imageName);
                riversRef.putFile(filePath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.hide();
                                //  imgUrl = taskSnapshot.getDownloadUrl().toString();
                                imgLink = Constants.BIKE_IMAGE_PATH + imageName;


                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                progressDialog.hide();
                                Log.d("Image", "2");
                                Log.d("Image", exception.toString());
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.show();
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();

                                    }
                                }, 5000);
                                Log.d("Image", "3");
                                Log.d("Image", taskSnapshot.toString());
                            }
                        })
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                progressDialog.hide();
                            }
                        });
            } else {
                //  progressDialog.hide();
            }
        } else {
        }

    }

    private void updateData() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        //  String userid = pref.getEmail().replaceAll("\\.", "").replaceAll("@", "");
        mDatabase.child("Bike").child(bike_id).child("bike_des").setValue(etBikeDescription.getText().toString().trim());
        mDatabase.child("Bike").child(bike_id).child("bike_title").setValue(etBikeTitle.getText().toString().trim());
        mDatabase.child("Bike").child(bike_id).child("bike_price").setValue(etBikePrice.getText().toString().trim());
        mDatabase.child("Bike").child(bike_id).child("bike_number").setValue(etBikeNumber.getText().toString().trim());
        mDatabase.child("Bike").child(bike_id).child("bike_color").setValue(color);
        mDatabase.child("Bike").child(bike_id).child("bike_status").setValue(bike_status);
        mDatabase.child("Bike").child(bike_id).child("bike_latitude").setValue(Double.parseDouble(bike_latitude));
        mDatabase.child("Bike").child(bike_id).child("bike_longitude").setValue(Double.parseDouble(bike_longitude));
        mDatabase.child("Bike").child(bike_id).child("bike_address").setValue(address);
        mDatabase.child("Bike").child(bike_id).child("bike_img").setValue(imgLink);
        updateGeoFireTable(bike_id, Double.parseDouble(bike_latitude), Double.parseDouble(bike_longitude));

    }

    private void createGeoFireTable(String key, double bike_latitude, double bike_longitude) {
        setUpProgressDialog();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("MyLocation");
        geofire = new GeoFire(mDatabase);
        geofire.setLocation(key, new GeoLocation(bike_latitude, bike_longitude), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    progressDialog.hide();
                    Log.d("GeoFireError: ", String.valueOf(error));
                } else {
                    progressDialog.hide();
                    Toast.makeText(getApplicationContext(), "Ride Created Successfully", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
        });
    }

    private void updateGeoFireTable(String key, double bike_latitude, double bike_longitude) {
        setUpProgressDialog();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("MyLocation");
        geofire = new GeoFire(mDatabase);
        geofire.setLocation(key, new GeoLocation(bike_latitude, bike_longitude), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    progressDialog.hide();
                    Log.d("GeoFireError: ", String.valueOf(error));
                } else {
                    progressDialog.hide();
                    Toast.makeText(CreateBikeActivity.this, "Successfully edited", Toast.LENGTH_SHORT).show();
                    /*Intent intent = new Intent(CreateBikeActivity.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);*/
                    onBackPressed();
                }
            }
        });
    }
}
