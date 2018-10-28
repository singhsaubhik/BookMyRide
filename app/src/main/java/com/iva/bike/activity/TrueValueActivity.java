package com.iva.bike.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.iva.bike.R;
import com.iva.bike.adapter.SnapCardAdapter;
import com.iva.bike.module.Cars;

import java.util.ArrayList;

public class TrueValueActivity extends AppCompatActivity {

    SnapCardAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<Cars> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_true_value2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("TRUEVALUE");
        setSupportActionBar(toolbar);

        init();



    }

    private void init(){
        recyclerView = findViewById(R.id.snap_recycler);
        list = new ArrayList<>();

        list.add(new Cars("BMW"));
        list.add(new Cars("BMW"));
        list.add(new Cars("BMW"));
        list.add(new Cars("BMW"));

        list.add(new Cars("BMW"));
        list.add(new Cars("BMW"));

        adapter = new SnapCardAdapter(list,this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setAdapter(adapter);

    }
}
