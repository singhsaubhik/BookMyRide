package com.iva.bike.activity;

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
import com.iva.bike.R;
import com.iva.bike.adapter.BookRideAdapter;
import com.iva.bike.module.BookModule;
import com.iva.bike.utility.Pref;

import java.util.ArrayList;
import java.util.Collections;

public class BookRideActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView rvBook;
    private LinearLayoutManager layoutManager;
    private ArrayList<BookModule> arrayList = new ArrayList<>();
    private BookRideAdapter bookAdapter;
    private LinearLayout llLoader;
    private LinearLayout llNothing;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Pref pref;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_ride);

        initialize();
        peformAction();
        getUserBook();
    }

    private void initialize(){
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#000000"));
        toolbar.setTitle("Book Ride");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);

        pref = new Pref(this);

        rvBook = (RecyclerView)findViewById(R.id.rvBook);
        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rvBook.setLayoutManager(layoutManager);
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
                getUserBook();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void getUserBook(){
        arrayList.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Book");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                           callUserList(dataSnapshot1.getKey());


                    }

                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });
    }

    private void setUpRecycler() {
      //  Collections.reverse(arrayList);
        bookAdapter = new BookRideAdapter(BookRideActivity.this,arrayList);
        rvBook.setAdapter(bookAdapter);
    }

    private void callUserList(String key){
        rvBook.setVisibility(View.GONE);
        llLoader.setVisibility(View.VISIBLE);
        llNothing.setVisibility(View.GONE);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Book").child(key);
         Query query = reference.orderByChild("customer_email").equalTo(pref.getEmail());
         query.addListenerForSingleValueEvent(new ValueEventListener() {
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
                                arrayList.add(bookModule);

                     }
                     if (arrayList.size()>0) {
                         llLoader.setVisibility(View.GONE);
                         swipeRefreshLayout.setRefreshing(false);
                         rvBook.setVisibility(View.VISIBLE);
                         llNothing.setVisibility(View.GONE);
                         setUpRecycler();
                     }else {
                         llLoader.setVisibility(View.GONE);
                         swipeRefreshLayout.setRefreshing(false);
                         rvBook.setVisibility(View.GONE);
                         llNothing.setVisibility(View.VISIBLE);
                     }
                 } else {
                     rvBook.setVisibility(View.GONE);
                     llLoader.setVisibility(View.GONE);
                     llNothing.setVisibility(View.VISIBLE);
                 }
             }

             @Override
             public void onCancelled(DatabaseError databaseError) {
                 Log.d("Error", databaseError.getMessage().toString());
                 rvBook.setVisibility(View.GONE);
                 llLoader.setVisibility(View.GONE);
                 llNothing.setVisibility(View.VISIBLE);
             }
         });
    }
}
