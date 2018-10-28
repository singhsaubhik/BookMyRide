package com.iva.bike.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iva.bike.R;
import com.iva.bike.module.Cars;

import java.util.ArrayList;

public class SnapCardAdapter extends RecyclerView.Adapter<SnapCardAdapter.MyHolder> {

    private ArrayList<Cars> list;
    private Context context;

    public SnapCardAdapter(ArrayList<Cars> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.snap_recycler_item,parent,false);

        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        public MyHolder(View itemView) {
            super(itemView);
        }
    }
}
