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
import com.kiit.bike.adapter.BookRideAdapter;
import com.kiit.bike.adapter.HistoryAdapter;
import com.kiit.bike.module.BikeModule;
import com.kiit.bike.module.BookModule;
import com.kiit.bike.utility.Pref;

import java.util.ArrayList;
import java.util.Collections;

public class HistoryActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView rvHistory;
    private LinearLayoutManager layoutManager;
    private ArrayList<BookModule> arrayList = new ArrayList<>();
    private HistoryAdapter historyAdapter;
    private LinearLayout llLoader;
    private LinearLayout llNothing;
    private SwipeRefreshLayout swipeRefreshLayout;
    Pref pref;
    private String bike_id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        initialize();
        peformAction();

    }

    private void initialize(){
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#000000"));
        toolbar.setTitle("Order History");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);

        pref = new Pref(this);

        rvHistory = (RecyclerView)findViewById(R.id.rvHistory);
        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rvHistory.setLayoutManager(layoutManager);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.all_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark), getResources().getColor(R.color.colorAccent));
        llLoader = (LinearLayout)findViewById(R.id.llLoader);
        llNothing = (LinearLayout)findViewById(R.id.llNothing);

        if (getIntent().getExtras()!=null){
            bike_id = getIntent().getStringExtra("bike_id");
        }

        getHistory(bike_id);
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
                getHistory(bike_id);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void getHistory(String bike_id){
        arrayList.clear();
        rvHistory.setVisibility(View.GONE);
        llLoader.setVisibility(View.VISIBLE);
        llNothing.setVisibility(View.GONE);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Book").child(bike_id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Log.d("Error", "value");
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        BookModule bookModule = dataSnapshot1.getValue(BookModule.class);
                        bookModule.setBike_title(bookModule.getBike_title());
                        bookModule.setBike_des(bookModule.getBike_des());
                        bookModule.setBike_id(bookModule.getBike_id());
                        bookModule.setBike_address(bookModule.getBike_address());
                        bookModule.setBike_price(bookModule.getBike_price());
                        bookModule.setBike_color(bookModule.getBike_color());
                        bookModule.setBike_number(bookModule.getBike_number());
                        bookModule.setBike_img(bookModule.getBike_img());
                        bookModule.setBike_status(bookModule.getBike_status());
                        bookModule.setStart_date(bookModule.getStart_date());
                        bookModule.setEnd_date(bookModule.getEnd_date());
                        bookModule.setOwner_email(bookModule.getOwner_email());
                        bookModule.setPaymentMode(bookModule.getPaymentMode());
                        bookModule.setTransaction_id(bookModule.getTransaction_id());
                        arrayList.add(bookModule);

                    }
                    if (arrayList.size()>0) {
                        llLoader.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        rvHistory.setVisibility(View.VISIBLE);
                        llNothing.setVisibility(View.GONE);
                        setUpRecycler();
                    }else {
                        llLoader.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        rvHistory.setVisibility(View.GONE);
                        llNothing.setVisibility(View.VISIBLE);
                    }
                } else {
                    rvHistory.setVisibility(View.GONE);
                    llLoader.setVisibility(View.GONE);
                    llNothing.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error", databaseError.getMessage().toString());
                rvHistory.setVisibility(View.GONE);
                llLoader.setVisibility(View.GONE);
                llNothing.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setUpRecycler(){
        Collections.reverse(arrayList);
        historyAdapter = new HistoryAdapter(HistoryActivity.this,arrayList);
        rvHistory.setAdapter(historyAdapter);
    }
}
