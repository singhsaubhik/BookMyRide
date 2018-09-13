package com.kiit.bike.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kiit.bike.R;
import com.kiit.bike.adapter.BookOrderAdapter;
import com.kiit.bike.adapter.ProductAdapter;
import com.kiit.bike.module.BikeModule;
import com.kiit.bike.utility.Pref;

import java.util.ArrayList;

public class BookOrderActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView rvProductList;
    private LinearLayoutManager layoutManager;
    private ArrayList<BikeModule> arrayList = new ArrayList<>();
    private BookOrderAdapter bookOrderAdapter;
    private LinearLayout llLoader;
    private LinearLayout llNothing;
    private SwipeRefreshLayout swipeRefreshLayout;
    Pref pref;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_order);

        initialize();
        peformAction();
        getBookingOrderAdmin();
    }

    private void initialize(){
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#000000"));
        toolbar.setTitle("Book Order");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);

        pref = new Pref(this);

        rvProductList = (RecyclerView)findViewById(R.id.rvProductList);
        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rvProductList.setLayoutManager(layoutManager);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.all_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark), getResources().getColor(R.color.colorAccent));
        llLoader = (LinearLayout)findViewById(R.id.llLoader);
        llNothing = (LinearLayout)findViewById(R.id.llNothing);


    }

    private void peformAction(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBookingOrderAdmin();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void getBookingOrderAdmin(){
        rvProductList.setVisibility(View.GONE);
        llLoader.setVisibility(View.VISIBLE);
        llNothing.setVisibility(View.GONE);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("Bike").orderByChild("email").equalTo(pref.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

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

    private void setUpRecycler() {
        bookOrderAdapter = new BookOrderAdapter(BookOrderActivity.this,arrayList);
        rvProductList.setAdapter(bookOrderAdapter);
    }
}
