package com.iva.bike.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
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
import com.iva.bike.adapter.ProductAdapter;
import com.iva.bike.module.BikeModule;
import com.iva.bike.utility.Constants;
import com.iva.bike.utility.Pref;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private DrawerLayout dlMain;

    Pref pref;
    TextView tvName,tvEmail;
    CircleImageView imgProfile;
    LinearLayout llLogout,llSettings,llMyRide,llCreateRide,llBookRide,llHeader;
    TextView tvType;

    TextView tvNameUser,tvEmailUser;
    CircleImageView imgProfileUser;
    LinearLayout llLogoutUser,llSettingsUser,llMyRideUser,llBookRideUser,llHeaderUser,llHeadAboutUs;
    TextView tvTypeUser;
    ProgressDialog progressDialog,mProgressDialog;
    private RequestOptions requestOptions;
    private RecyclerView rvProductList;
    private LinearLayoutManager layoutManager;
    private ArrayList<BikeModule> arrayList = new ArrayList<>();
    private ProductAdapter productAdapter;
    private LinearLayout llLoader;
    private LinearLayout llNothing;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView imgDelete;
    private LinearLayout llDrawerAdmin,llDrawerUser;
    private GeoFire geofire;
    private ArrayList<String> arrayListKey = new ArrayList<>();
    HashSet<String> hashSet = new HashSet<String>();
    private LinearLayout llBooking;
    private LinearLayout llPicup,llDropOff,llStartDate,llEndDate;
    private EditText tvPickup,tvDropOff,tvStartDate,tvEndDate;
    String latitude,longitude,latitude2,longitude2,latitude3,longitude3,latitude4,longitude4,latitude5,longitude5;
    private Button btnGo;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private DatePickerDialog datePickerDialog;
    private String flag="";
    SimpleDateFormat dfDate  = new SimpleDateFormat("dd-MM-yyyy");
    private LinearLayout llPickupNew;
    private EditText etPickupNew;
    private String addressNew;
    private AlertDialog alertDialog;
    private EditText etDialogPickup,etDialogDropOff,etDialogStartDate,etDialogEndDate;
    private LinearLayout llDialogPickup,llDialogDropOff,llDialogStartDate,llDialogEndDate;
    private LinearLayout llPickupMain;
    private ImageView imgTrack;
    private String clickType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();

        performAction();

    }

    private void initialize(){

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#000000"));
        toolbar.setTitle("Dashboard");
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);

        requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.nouser);
        requestOptions.error(R.drawable.nouser);

        pref = new Pref(this);



        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("MyLocation");
        geofire = new GeoFire(mDatabase);

        dlMain = findViewById(R.id.dlMain);
        llDrawerAdmin = (LinearLayout)findViewById(R.id.llDrawerAdmin);
        llDrawerUser = (LinearLayout)findViewById(R.id.llDrawerUser);

        llBooking = (LinearLayout)findViewById(R.id.llBooking);
        llPickupMain = (LinearLayout)findViewById(R.id.llPickupMain);
        imgTrack = (ImageView)findViewById(R.id.imgTrack);
        llPickupNew = (LinearLayout)findViewById(R.id.llPickupNew);
        etPickupNew = (EditText)findViewById(R.id.etPickupNew);
        etPickupNew.setFocusable(false);
        etPickupNew.setClickable(false);
        etPickupNew.setFocusableInTouchMode(false);
        etPickupNew.setEnabled(true);
        etPickupNew.setKeyListener(null);
        etPickupNew.setHorizontallyScrolling(true);

        if (pref.getType().equals("Admin")){
            llDrawerAdmin.setVisibility(View.VISIBLE);
            llDrawerUser.setVisibility(View.GONE);
            llBooking.setVisibility(View.GONE);
            llPickupMain.setVisibility(View.GONE);
        }else {
            llDrawerUser.setVisibility(View.VISIBLE);
            llDrawerAdmin.setVisibility(View.GONE);
            llBooking.setVisibility(View.GONE);
            llPickupMain.setVisibility(View.VISIBLE);
        }

        tvName = (TextView)findViewById(R.id.tvName);
        tvEmail = (TextView)findViewById(R.id.tvEmail);
        tvNameUser = (TextView)findViewById(R.id.tvNameUser);
        tvEmailUser = (TextView)findViewById(R.id.tvEmailUser);

        tvName.setText(pref.getName());
        tvEmail.setText(pref.getEmail());
        tvNameUser.setText(pref.getName());
        tvEmailUser.setText(pref.getEmail());

        imgProfile = (CircleImageView)findViewById(R.id.imgProfile);
        imgProfileUser = (CircleImageView)findViewById(R.id.imgProfileUser);

        llMyRide = (LinearLayout)findViewById(R.id.llMyRide);
        llCreateRide = (LinearLayout)findViewById(R.id.llCreateRide);
        llBookRide = (LinearLayout)findViewById(R.id.llBookRide);
        llSettings = (LinearLayout)findViewById(R.id.llSettings);
        llLogout = (LinearLayout)findViewById(R.id.llLogout);
        llHeader = (LinearLayout)findViewById(R.id.llHeader);
        llHeadAboutUs = findViewById(R.id.llAboutUser);

        llMyRideUser = (LinearLayout)findViewById(R.id.llMyRideUser);
        llBookRideUser = (LinearLayout)findViewById(R.id.llBookRideUser);
        llSettingsUser = (LinearLayout)findViewById(R.id.llSettingsUser);
        llLogoutUser = (LinearLayout)findViewById(R.id.llLogoutUser);
        llHeaderUser = (LinearLayout)findViewById(R.id.llHeaderUser);

        llPicup = (LinearLayout)findViewById(R.id.llPickup);
        llDropOff = (LinearLayout)findViewById(R.id.llDropOff);
        llStartDate = (LinearLayout)findViewById(R.id.llStartDate);
        llEndDate = (LinearLayout)findViewById(R.id.llEndDate);
        tvPickup = (EditText) findViewById(R.id.etPickup);
        tvDropOff = (EditText) findViewById(R.id.etDropOff);
        tvStartDate = (EditText) findViewById(R.id.etStartDate);
        tvEndDate = (EditText)findViewById(R.id.etEndDate);
        btnGo = (Button)findViewById(R.id.btnGo);


        Calendar calendar =Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mHour = calendar.get(Calendar.HOUR);
        mMinute = calendar.get(Calendar.MINUTE);

        datePickerDialog = new DatePickerDialog(this,dateSetListener,mYear,mMonth,mDay);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);


        rvProductList = (RecyclerView)findViewById(R.id.rvProductList);
        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rvProductList.setLayoutManager(layoutManager);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.all_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark), getResources().getColor(R.color.colorAccent));
        llLoader = (LinearLayout)findViewById(R.id.llLoader);
        llNothing = (LinearLayout)findViewById(R.id.llNothing);

        tvType = (TextView)findViewById(R.id.tvType);
        tvType.setText(pref.getType());
        tvTypeUser = (TextView)findViewById(R.id.tvTypeUser);
        tvTypeUser.setText(pref.getType());

        imgDelete = (ImageView)findViewById(R.id.imgDelete);

      loadProfileImage();



        if (pref.getType().equals("Admin")) {
            getBookingRide();
        }else {
            addressNew = pref.getCurrentAddress();
            etPickupNew.setText(pref.getCurrentAddress());
            getUserBookingRideNew(pref.getLatitude(),pref.getLongitude());
        }



    }

    private void performAction(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!dlMain.isDrawerOpen(Gravity.START)) {
                    dlMain.openDrawer(Gravity.START);
                }else {
                    dlMain.closeDrawer(Gravity.START);
                }
            }
        });

        llMyRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlMain.closeDrawer(Gravity.START);


                llMyRide.setBackgroundColor(Color.parseColor("#EAE6E5"));
                llCreateRide.setBackgroundColor(Color.parseColor("#ffffff"));
                llBookRide.setBackgroundColor(Color.parseColor("#ffffff"));
                llSettings.setBackgroundColor(Color.parseColor("#ffffff"));
                llLogout.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        });

        llCreateRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlMain.closeDrawer(Gravity.START);


                llMyRide.setBackgroundColor(Color.parseColor("#ffffff"));
                llCreateRide.setBackgroundColor(Color.parseColor("#EAE6E5"));
                llBookRide.setBackgroundColor(Color.parseColor("#ffffff"));
                llSettings.setBackgroundColor(Color.parseColor("#ffffff"));
                llLogout.setBackgroundColor(Color.parseColor("#ffffff"));

                startActivity(new Intent(MainActivity.this,CreateBikeActivity.class));
            }
        });

        llBookRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlMain.closeDrawer(Gravity.START);


                llMyRide.setBackgroundColor(Color.parseColor("#ffffff"));
                llCreateRide.setBackgroundColor(Color.parseColor("#ffffff"));
                llBookRide.setBackgroundColor(Color.parseColor("#EAE6E5"));
                llSettings.setBackgroundColor(Color.parseColor("#ffffff"));
                llLogout.setBackgroundColor(Color.parseColor("#ffffff"));

                startActivity(new Intent(MainActivity.this,BookOrderActivity.class));

            }
        });

        llSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlMain.closeDrawer(Gravity.START);


                llMyRide.setBackgroundColor(Color.parseColor("#ffffff"));
                llCreateRide.setBackgroundColor(Color.parseColor("#ffffff"));
                llBookRide.setBackgroundColor(Color.parseColor("#ffffff"));
                llSettings.setBackgroundColor(Color.parseColor("#EAE6E5"));
                llLogout.setBackgroundColor(Color.parseColor("#ffffff"));

                startActivity(new Intent(MainActivity.this,RevenueActivity.class));

            }
        });


        llLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlMain.closeDrawer(Gravity.START);


                llMyRide.setBackgroundColor(Color.parseColor("#ffffff"));
                llCreateRide.setBackgroundColor(Color.parseColor("#ffffff"));
                llBookRide.setBackgroundColor(Color.parseColor("#ffffff"));
                llSettings.setBackgroundColor(Color.parseColor("#ffffff"));
                llLogout.setBackgroundColor(Color.parseColor("#EAE6E5"));

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                pref.saveEmail("");
                pref.saveType("");
            }
        });

        llHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlMain.closeDrawer(Gravity.START);
                startActivity(new Intent(MainActivity.this,ProfileActivity.class));
            }
        });

        llMyRideUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlMain.closeDrawer(Gravity.START);

                llMyRideUser.setBackgroundColor(Color.parseColor("#EAE6E5"));
                llBookRideUser.setBackgroundColor(Color.parseColor("#ffffff"));
                llSettingsUser.setBackgroundColor(Color.parseColor("#ffffff"));
                llLogoutUser.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        });


        llBookRideUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlMain.closeDrawer(Gravity.START);

                llMyRideUser.setBackgroundColor(Color.parseColor("#ffffff"));
                llBookRideUser.setBackgroundColor(Color.parseColor("#EAE6E5"));
                llSettingsUser.setBackgroundColor(Color.parseColor("#ffffff"));
                llLogoutUser.setBackgroundColor(Color.parseColor("#ffffff"));

                startActivity(new Intent(MainActivity.this,BookRideActivity.class));
            }
        });

        llSettingsUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlMain.closeDrawer(Gravity.START);

                llMyRideUser.setBackgroundColor(Color.parseColor("#ffffff"));
                llBookRideUser.setBackgroundColor(Color.parseColor("#ffffff"));
                llSettingsUser.setBackgroundColor(Color.parseColor("#EAE6E5"));
                llLogoutUser.setBackgroundColor(Color.parseColor("#ffffff"));

                startActivity(new Intent(MainActivity.this,ContactUsActivity.class));
            }
        });

        llLogoutUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlMain.closeDrawer(Gravity.START);


                llMyRideUser.setBackgroundColor(Color.parseColor("#ffffff"));
                llBookRideUser.setBackgroundColor(Color.parseColor("#ffffff"));
                llSettingsUser.setBackgroundColor(Color.parseColor("#ffffff"));
                llLogoutUser.setBackgroundColor(Color.parseColor("#EAE6E5"));

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                pref.saveEmail("");
                pref.saveType("");
            }
        });

        llHeadAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View v = getLayoutInflater().inflate(R.layout.about_us_dialog_layout,null,false);
                CardView c = v.findViewById(R.id.ok_button);
                builder.setView(v);
                builder.show();

                c.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
            }
        });

        llHeaderUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlMain.closeDrawer(Gravity.START);
                startActivity(new Intent(MainActivity.this,ProfileActivity.class));
            }
        });

        llPicup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,AddressActivity.class);
                intent.putExtra("flag","pickup");
                startActivityForResult(intent, 2);
            }
        });

        llDropOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,AddressActivity.class);
                intent.putExtra("flag","drop");
                startActivityForResult(intent, 3);
            }
        });

        llPickupNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,AddressActivity.class);
                intent.putExtra("flag","pickupNew");
                startActivityForResult(intent, 4);
                clickType = "auto";
            }
        });

        llStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = "start";
                datePickerDialog.show();

            }

        });

        llEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = "end";
                datePickerDialog.show();

            }

        });

        imgTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addressNew = pref.getCurrentAddress();
                etPickupNew.setText(pref.getCurrentAddress());
                getUserBookingRideNew(pref.getLatitude(),pref.getLongitude());
                clickType = "current";
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                   if (pref.getType().equals("Admin")) {
                       getBookingRide();
                   }else if (pref.getType().equals("User")){
                      if (etPickupNew.getText().toString().length()>0){
                          if (clickType.equals("auto")) {
                              getUserBookingRideNew(latitude3, longitude3);
                          }else if (clickType.equals("current")){
                              getUserBookingRideNew(pref.getLatitude(),pref.getLongitude());
                          }
                      }else {
                          etPickupNew.setError("Enter Location");
                          etPickupNew.requestFocus();
                      }
                   }

            }
        });

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvPickup.getText().toString().length()>0){
                    if (tvDropOff.getText().toString().length()>0){
                        if (tvStartDate.getText().toString().length()>0){
                            if (tvEndDate.getText().toString().length()>0){
                                try {
                                    if (dfDate.parse(tvStartDate.getText().toString().trim()).before(dfDate.parse(tvEndDate.getText().toString().trim()))){
                                        tvPickup.setError(null);
                                        tvDropOff.setError(null);
                                        tvStartDate.setError(null);
                                        tvEndDate.setError(null);
                                        getUserBookingRide(latitude,longitude,tvStartDate.getText().toString().trim(),tvEndDate.getText().toString().trim());
                                    }else if (dfDate.parse(tvStartDate.getText().toString().trim()).equals(dfDate.parse(tvEndDate.getText().toString().trim()))){
                                        tvPickup.setError(null);
                                        tvDropOff.setError(null);
                                        tvStartDate.setError(null);
                                        tvEndDate.setError(null);
                                        getUserBookingRide(latitude,longitude,tvStartDate.getText().toString().trim(),tvEndDate.getText().toString().trim());
                                    }
                                        else {
                                        Toast.makeText(getApplicationContext(),"Start Date must be smaller than End Date",Toast.LENGTH_SHORT).show();
                                    }

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                tvEndDate.setError("Enter EndDate");
                                tvEndDate.requestFocus();
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }else {
                            tvStartDate.setError("Enter StartDate");
                            tvStartDate.requestFocus();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }else {
                        tvDropOff.setError("Enter DropOff");
                        tvDropOff.requestFocus();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }else {
                    tvPickup.setError("Enter Pickup");
                    tvPickup.requestFocus();
                    swipeRefreshLayout.setRefreshing(false);
                }

            }
        });







    }

    @Override
    protected void onResume() {
        super.onResume();

        tvName.setText(pref.getName());
        tvEmail.setText(pref.getEmail());
    }

    private void getBookingRide(){
        rvProductList.setVisibility(View.GONE);
        llLoader.setVisibility(View.VISIBLE);
        llNothing.setVisibility(View.GONE);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("Bike").orderByChild("email").equalTo(pref.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Log.d("Error", "value");
                    arrayList.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        BikeModule bikeModule = dataSnapshot1.getValue(BikeModule.class);
                     //   if (pref.getEmail().equals(bikeModule.getEmail())) {
                            bikeModule.setBike_title(bikeModule.getBike_title());
                            bikeModule.setBike_des(bikeModule.getBike_des());
                            bikeModule.setBike_id(bikeModule.getBike_id());
                            bikeModule.setBike_address(bikeModule.getBike_address());
                            bikeModule.setBike_price(bikeModule.getBike_price());
                            bikeModule.setBike_color(bikeModule.getBike_color());
                            bikeModule.setBike_number(bikeModule.getBike_number());
                            bikeModule.setBike_img(bikeModule.getBike_img());
                            bikeModule.setBike_status(bikeModule.getBike_status());
                            bikeModule.setBike_latitude(bikeModule.getBike_latitude());
                            bikeModule.setBike_longitude(bikeModule.getBike_longitude());
                            bikeModule.setEmail(bikeModule.getEmail());
                            arrayList.add(bikeModule);
                      //  }

                    }
                    if (arrayList.size()>0) {
                        llLoader.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        rvProductList.setVisibility(View.VISIBLE);
                        llNothing.setVisibility(View.GONE);
                        setUpRecycler();
                    }else {
                        llLoader.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        rvProductList.setVisibility(View.GONE);
                        llNothing.setVisibility(View.VISIBLE);
                    }
                } else {
                    rvProductList.setVisibility(View.GONE);
                    llLoader.setVisibility(View.GONE);
                    llNothing.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error", databaseError.getMessage().toString());
                rvProductList.setVisibility(View.GONE);
                llLoader.setVisibility(View.GONE);
                llNothing.setVisibility(View.VISIBLE);

            }
        });
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setMessage("LOADING...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void setUpRecycler() {
        productAdapter = new ProductAdapter(MainActivity.this,arrayList);
        rvProductList.setAdapter(productAdapter);
    }

    public void setImageVisible(Boolean visible, final String  bike_id){
        if (visible){
           imgDelete.setVisibility(View.VISIBLE);

            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Bike");
                    databaseReference.child(bike_id).removeValue();
                    Toast.makeText(getApplicationContext(),"Successfully Deleted",Toast.LENGTH_LONG).show();
                    getBookingRide();
                    imgDelete.setVisibility(View.GONE);

                }
            });
        }else {
            imgDelete.setVisibility(View.GONE);
        }
    }



    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();



    }

    @Override
    public void onBackPressed() {
        /*View v = getLayoutInflater().inflate(R.layout.quit_dialog_layout,null,false);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(v);
        builder.show();

        v.findViewById(R.id.quit_button_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Yes Pressed", Toast.LENGTH_SHORT).show();
                MainActivity.super.onDestroy();

            }
        });

        v.findViewById(R.id.quit_button_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                MainActivity.super.onBackPressed();
            }
        });*/

        new FancyAlertDialog.Builder(this)
                .setBackgroundColor(getResources().getColor(R.color.colorPrimary))
                .setTitle("Exit Application")
                .setMessage("Are you sure want to exit the application")
                .setNegativeBtnText("Cancel")
                .setPositiveBtnText("Exit")
                .setNegativeBtnBackground(Color.GREEN)
                .setPositiveBtnBackground(Color.RED)
                .setAnimation(Animation.POP)
                .isCancellable(true)
                .setIcon(R.mipmap.ic_launcher_round, Icon.Visible)
                .OnPositiveClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        MainActivity.super.onBackPressed();
                    }
                }).build();
    }

    private void getUserBookingRide(String latitude, String longitude, final String startDate, final String endDate){
        arrayList.clear();
        arrayListKey.clear();
        hashSet.clear();

        rvProductList.setVisibility(View.GONE);
        llLoader.setVisibility(View.VISIBLE);
        llNothing.setVisibility(View.GONE);
        GeoQuery geoQuery = geofire.queryAtLocation(new GeoLocation(
                Double.parseDouble(latitude), Double.parseDouble(longitude)), 5);
       geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
           @Override
           public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {

                   DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                   Query query = reference.child("Bike").orderByChild("bike_id").equalTo(dataSnapshot.getKey());
                   query.addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(DataSnapshot dataSnapshot) {

                           if (dataSnapshot.exists()) {
                               llLoader.setVisibility(View.GONE);
                               swipeRefreshLayout.setRefreshing(false);
                               Log.d("Error", "value");
                            //   arrayList.clear();
                               for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                   final BikeModule bikeModule = dataSnapshot1.getValue(BikeModule.class);
                                   bikeModule.setBike_title(bikeModule.getBike_title());
                                   bikeModule.setBike_des(bikeModule.getBike_des());
                                   bikeModule.setBike_id(bikeModule.getBike_id());
                                   bikeModule.setBike_address(bikeModule.getBike_address());
                                   bikeModule.setBike_price(bikeModule.getBike_price());
                                   bikeModule.setBike_color(bikeModule.getBike_color());
                                   bikeModule.setBike_number(bikeModule.getBike_number());
                                   bikeModule.setBike_img(bikeModule.getBike_img());
                                   bikeModule.setBike_status(bikeModule.getBike_status());
                                   bikeModule.setBike_latitude(bikeModule.getBike_latitude());
                                   bikeModule.setBike_longitude(bikeModule.getBike_longitude());
                                   bikeModule.setStart_date(startDate);
                                   bikeModule.setEnd_date(endDate);
                                   arrayList.add(bikeModule);
                               }
                               if (arrayList.size()>0) {
                                   rvProductList.setVisibility(View.VISIBLE);
                                   llNothing.setVisibility(View.GONE);
                                   setUpRecycler();
                               }else {
                                   rvProductList.setVisibility(View.GONE);
                                   llNothing.setVisibility(View.VISIBLE);
                               }
                           } else {
                               rvProductList.setVisibility(View.GONE);
                               llLoader.setVisibility(View.GONE);
                               llNothing.setVisibility(View.VISIBLE);
                           }
                       }

                       @Override
                       public void onCancelled(DatabaseError databaseError) {
                           Log.d("Error", databaseError.getMessage().toString());
                           rvProductList.setVisibility(View.GONE);
                           llLoader.setVisibility(View.GONE);
                           llNothing.setVisibility(View.VISIBLE);
                       }
                   });
            //  }

           }

           @Override
           public void onDataExited(DataSnapshot dataSnapshot) {

           }

           @Override
           public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {

           }

           @Override
           public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {

           }

           @Override
           public void onGeoQueryReady() {
               rvProductList.setVisibility(View.GONE);
               llLoader.setVisibility(View.GONE);
               llNothing.setVisibility(View.VISIBLE);
           }

           @Override
           public void onGeoQueryError(DatabaseError error) {

           }
       });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==2){
            if (resultCode==2){

                String value=data.getStringExtra("flagLocation");

                tvPickup.setText(value);
                tvDropOff.setText("");
                String latitude=data.getStringExtra("latitude");
                String longitude = data.getStringExtra("longitude");





                //  typeSearch(value);

            }
        }else if (requestCode==3){
            if (resultCode==3){
                String value=data.getStringExtra("flagLocation1");

                tvPickup.setText(value);

                latitude=data.getStringExtra("latitude1");
                longitude = data.getStringExtra("longitude1");



                String value2=data.getStringExtra("flagLocation2");

                tvDropOff.setText(value2);
                latitude2=data.getStringExtra("latitude2");
                longitude2 = data.getStringExtra("longitude2");



            }
        }else if (requestCode==4){
            if (resultCode==4){
                 addressNew=data.getStringExtra("flagLocation3");

                etPickupNew.setText(addressNew);

                 latitude3=data.getStringExtra("latitude3");
                 longitude3 = data.getStringExtra("longitude3");
                getUserBookingRideNew(latitude3,longitude3);



            }
        }else if (requestCode==5){
            if (resultCode==5){




                String value4=data.getStringExtra("flagLocation5");

                etDialogDropOff.setText(value4);
                latitude5=data.getStringExtra("latitude5");
                longitude5 = data.getStringExtra("longitude5");



            }
        }
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            if (flag.equals("start")){
                tvStartDate.setText(day+"-"+(month+1)+"-"+year);
            }else if (flag.equals("end")){
                tvEndDate.setText(day+"-"+(month+1)+"-"+year);
            }else if (flag.equals("start_new")){
                etDialogStartDate.setText(day+"-"+(month+1)+"-"+year);
            }else if (flag.equals("end_new")){
                etDialogEndDate.setText(day+"-"+(month+1)+"-"+year);
            }


        }
    };

    private void loadProfileImage(){
        if (!pref.getEmail().equals("")) {
            final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference riversRef = storageReference.child(Constants.USER_IMAGE_PATH + pref.getEmail().replaceAll("\\.", "").replaceAll("@", "") + ".png");
            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    if (pref.getType().equals("Admin")) {
                        Glide.with(MainActivity.this)
                                .load(uri)
                                .apply(requestOptions)
                                .into(imgProfile);
                    } else {
                        Glide.with(MainActivity.this)
                                .load(uri)
                                .apply(requestOptions)
                                .into(imgProfileUser);
                    }


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    if (pref.getType().equals("Admin")) {
                        imgProfile.setImageResource(R.drawable.nouser);
                    } else {
                        imgProfileUser.setImageResource(R.drawable.nouser);
                    }
                }
            });
        }
    }

    private void getUserBookingRideNew(String latitude, String longitude){
        arrayList.clear();
        arrayListKey.clear();
        hashSet.clear();

        rvProductList.setVisibility(View.GONE);
        llLoader.setVisibility(View.VISIBLE);
        llNothing.setVisibility(View.GONE);
        GeoQuery geoQuery = geofire.queryAtLocation(new GeoLocation(
                Double.parseDouble(latitude), Double.parseDouble(longitude)), 5);
        geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
            @Override
            public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                Query query = reference.child("Bike").orderByChild("bike_id").equalTo(dataSnapshot.getKey());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            llLoader.setVisibility(View.GONE);
                            swipeRefreshLayout.setRefreshing(false);
                            Log.d("Error", "value");
                            //   arrayList.clear();
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                final BikeModule bikeModule = dataSnapshot1.getValue(BikeModule.class);
                                bikeModule.setBike_title(bikeModule.getBike_title());
                                bikeModule.setBike_des(bikeModule.getBike_des());
                                bikeModule.setBike_id(bikeModule.getBike_id());
                                bikeModule.setBike_address(bikeModule.getBike_address());
                                bikeModule.setBike_price(bikeModule.getBike_price());
                                bikeModule.setBike_color(bikeModule.getBike_color());
                                bikeModule.setBike_number(bikeModule.getBike_number());
                                bikeModule.setBike_img(bikeModule.getBike_img());
                                bikeModule.setBike_status(bikeModule.getBike_status());
                                bikeModule.setBike_latitude(bikeModule.getBike_latitude());
                                bikeModule.setBike_longitude(bikeModule.getBike_longitude());
                                arrayList.add(bikeModule);
                            }
                            if (arrayList.size()>0) {
                                rvProductList.setVisibility(View.VISIBLE);
                                llNothing.setVisibility(View.GONE);
                                setUpRecycler();
                            }else {
                                rvProductList.setVisibility(View.GONE);
                                llNothing.setVisibility(View.VISIBLE);
                            }
                        } else {
                            rvProductList.setVisibility(View.GONE);
                            llLoader.setVisibility(View.GONE);
                            llNothing.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("Error", databaseError.getMessage().toString());
                        rvProductList.setVisibility(View.GONE);
                        llLoader.setVisibility(View.GONE);
                        llNothing.setVisibility(View.VISIBLE);
                    }
                });
                //  }

            }

            @Override
            public void onDataExited(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {

            }

            @Override
            public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                rvProductList.setVisibility(View.GONE);
                llLoader.setVisibility(View.GONE);
                llNothing.setVisibility(View.VISIBLE);
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    public void showDialog(final Context context, final String bike_id, final String bike_des, final String bike_color, final String bike_address, final String bike_title, final String bike_price, final String bike_img, final String bike_number, final String bike_status, final String bike_latitude, final String bike_longitude, final String email){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context, R.style.CustomDialogNew);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_book, null);

         etDialogPickup = (EditText) dialogView.findViewById(R.id.etDialogPickup);

         etDialogPickup.setText(addressNew);

         etDialogDropOff = (EditText) dialogView.findViewById(R.id.etDialogDropOff);
         etDialogStartDate = (EditText) dialogView.findViewById(R.id.etDialogStartDate);
         etDialogEndDate = (EditText) dialogView.findViewById(R.id.etDialogEndDate);

         llDialogPickup = (LinearLayout)dialogView.findViewById(R.id.llDialogPickup);
         llDialogDropOff = (LinearLayout)dialogView.findViewById(R.id.llDialogDropOff);
         llDialogStartDate = (LinearLayout)dialogView.findViewById(R.id.llDialogStartDate);
         llDialogEndDate = (LinearLayout)dialogView.findViewById(R.id.llDialogEndDate);

        LinearLayout llCancel = (LinearLayout)dialogView.findViewById(R.id.llCancel);
        Button btnDialogGo = (Button)dialogView.findViewById(R.id.btnDialogGo);

        llCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

            }
        });

        llDialogDropOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,AddressActivity.class);
                intent.putExtra("flag","dropNew");
                startActivityForResult(intent, 5);
            }
        });

        llDialogStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = "start_new";
                datePickerDialog.show();
            }
        });

        llDialogEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = "end_new";
                datePickerDialog.show();
            }
        });

        btnDialogGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etDialogDropOff.getText().toString().length()>0){
                    if (etDialogStartDate.getText().toString().length()>0){
                        if (etDialogEndDate.getText().toString().length()>0){
                            try {
                                if (dfDate.parse(etDialogStartDate.getText().toString().trim()).before(dfDate.parse(etDialogEndDate.getText().toString().trim()))){
                                    Intent intent = new Intent(context, BookActivity.class);
                                    intent.putExtra("bike_id",bike_id);
                                    intent.putExtra("bike_des",bike_des);
                                    intent.putExtra("bike_color",bike_color);
                                    intent.putExtra("bike_address",bike_address);
                                    intent.putExtra("bike_title",bike_title);
                                    intent.putExtra("bike_price",bike_price);
                                    intent.putExtra("bike_img",bike_img);
                                    intent.putExtra("bike_number",bike_number);
                                    intent.putExtra("bike_status",bike_status);
                                    intent.putExtra("bike_latitude",bike_latitude);
                                    intent.putExtra("bike_longitude",bike_longitude);
                                    intent.putExtra("email",email);
                                    intent.putExtra("start_date",etDialogStartDate.getText().toString().trim());
                                    intent.putExtra("end_date",etDialogEndDate.getText().toString().trim());
                                    context.startActivity(intent);
                                    alertDialog.dismiss();
                                }else if (dfDate.parse(etDialogStartDate.getText().toString().trim()).equals(dfDate.parse(etDialogEndDate.getText().toString().trim()))){
                                    Intent intent = new Intent(context, BookActivity.class);
                                    intent.putExtra("bike_id",bike_id);
                                    intent.putExtra("bike_des",bike_des);
                                    intent.putExtra("bike_color",bike_color);
                                    intent.putExtra("bike_address",bike_address);
                                    intent.putExtra("bike_title",bike_title);
                                    intent.putExtra("bike_price",bike_price);
                                    intent.putExtra("bike_img",bike_img);
                                    intent.putExtra("bike_number",bike_number);
                                    intent.putExtra("bike_status",bike_status);
                                    intent.putExtra("bike_latitude",bike_latitude);
                                    intent.putExtra("bike_longitude",bike_longitude);
                                    intent.putExtra("email",email);
                                    intent.putExtra("start_date",etDialogStartDate.getText().toString().trim());
                                    intent.putExtra("end_date",etDialogEndDate.getText().toString().trim());
                                    context.startActivity(intent);
                                    alertDialog.dismiss();
                                }
                                else {
                                    Toast.makeText(getApplicationContext(),"Start Date must be smaller than End Date",Toast.LENGTH_SHORT).show();
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }else {
                            etDialogEndDate.setError("Enter EndDate");
                            etDialogEndDate.requestFocus();
                        }
                    }else {
                        etDialogStartDate.setError("Enter StartDate");
                        etDialogStartDate.requestFocus();
                    }
                }else {
                    etDialogDropOff.setError("Enter DropOff");
                    etDialogDropOff.requestFocus();
                }
            }
        });




        dialogBuilder.setView(dialogView);
        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        Window window = alertDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        alertDialog.show();
    }







}
