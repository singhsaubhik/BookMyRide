package com.kiit.bike.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kiit.bike.R;
import com.kiit.bike.module.BookModule;
import com.kiit.bike.utility.Pref;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ItemViewHolder> {
    private ArrayList<BookModule> orderList;
    Context context;



    public HistoryAdapter(Context context, ArrayList<BookModule> orderList) {
        this.context = context;
        this.orderList=orderList;


    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_row, parent, false);
        return new HistoryAdapter.ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {


        holder.tvCustomerEmail.setText(orderList.get(position).getCustomer_email());
        holder.tvStartDate.setText(orderList.get(position).getStart_date());
        holder.tvEndDate.setText(orderList.get(position).getEnd_date());
        holder.tvPaymentMode.setText(orderList.get(position).getPaymentMode());
        holder.tvPrice.setText("$"+orderList.get(position).getBike_price());


    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView tvCustomerEmail,tvStartDate,tvEndDate,tvPaymentMode,tvPrice;

        public ItemViewHolder(View itemView) {
            super(itemView);


            tvCustomerEmail = (TextView)itemView.findViewById(R.id.tvCustomerEmail);
            tvStartDate = (TextView)itemView.findViewById(R.id.tvStartDate);
            tvEndDate = (TextView)itemView.findViewById(R.id.tvEndDate);
            tvPaymentMode = (TextView)itemView.findViewById(R.id.tvPaymentMode);
            tvPrice = (TextView)itemView.findViewById(R.id.tvPrice);



        }
    }


}
