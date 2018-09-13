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

public class BookRideAdapter extends RecyclerView.Adapter<BookRideAdapter.ItemViewHolder> {
    private ArrayList<BookModule> orderList;
    Context context;
    private RequestOptions requestOptions;
    private Pref pref;


    public BookRideAdapter(Context context, ArrayList<BookModule> orderList) {
        this.context = context;
        this.orderList=orderList;
        pref = new Pref(context);
        requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.demo_user);
        requestOptions.error(R.drawable.demo_user);

    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_book_row, parent, false);
        return new BookRideAdapter.ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {

        holder.tvProductName.setText(orderList.get(position).getBike_title());
        holder.tvProductDescription.setText(orderList.get(position).getBike_des());
        holder.tvProductAddress.setText(orderList.get(position).getBike_address());
        holder.tvProductPrice.setText("$"+orderList.get(position).getBike_price());
        holder.tvProductColor.setText("Bike Color : "+orderList.get(position).getBike_color());
        holder.tvProductNumber.setText(orderList.get(position).getBike_number());
        holder.tvVendorEmail.setText(orderList.get(position).getOwner_email());
        holder.tvStartDate.setText(orderList.get(position).getStart_date());
        holder.tvEndDate.setText(orderList.get(position).getEnd_date());

        if (orderList.get(position).getBike_status().equals("ON")){
            holder.tvProductStatus.setText("Available");
            holder.llStatus.setBackgroundColor(Color.parseColor("#15c205"));
        }else {
            holder.tvProductStatus.setText("Booked");
            holder.llStatus.setBackgroundColor(Color.parseColor("#ffcc0000"));
        }
        if (!TextUtils.isEmpty(orderList.get(position).getBike_img())) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference riversRef = storageReference.child(orderList.get(position).getBike_img());
            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(context)
                            .load(uri)
                            .apply(requestOptions)
                            .into(holder.imgProduct);


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    holder.imgProduct.setImageResource(R.drawable.demo_user);
                }
            });
        }else {
            holder.imgProduct.setImageResource(R.drawable.demo_user);
        }






    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgProduct,imgCart;
        private TextView tvProductName,tvProductDescription,tvProductAddress,tvProductPrice,tvProductColor,tvProductNumber,tvProductStatus,tvVendorEmail,tvStartDate,tvEndDate;
        private LinearLayout llStatus;
        public ItemViewHolder(View itemView) {
            super(itemView);

            tvProductName=(TextView)itemView.findViewById(R.id.tvProductName);
            tvProductDescription = (TextView)itemView.findViewById(R.id.tvProductDescription);
            tvProductAddress = (TextView)itemView.findViewById(R.id.tvProductAddress);
            tvProductPrice = (TextView)itemView.findViewById(R.id.tvProductPrice);
            tvProductColor = (TextView)itemView.findViewById(R.id.tvProductColor);
            imgProduct = (ImageView)itemView.findViewById(R.id.imgProduct);
            tvProductNumber = (TextView)itemView.findViewById(R.id.tvProductNumber);
            llStatus = (LinearLayout)itemView.findViewById(R.id.llStatus);
            tvProductStatus = (TextView)itemView.findViewById(R.id.tvProductStatus);
            tvVendorEmail = (TextView)itemView.findViewById(R.id.tvVendorEmail);
            tvStartDate = (TextView)itemView.findViewById(R.id.tvStartDate);
            tvEndDate = (TextView)itemView.findViewById(R.id.tvEndDate);

            imgCart = (ImageView)itemView.findViewById(R.id.imgCart);

        }
    }


}
