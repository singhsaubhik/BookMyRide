package com.iva.bike.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.iva.bike.R;
import com.iva.bike.activity.HistoryActivity;
import com.iva.bike.activity.MainActivity;
import com.iva.bike.module.BikeModule;
import com.iva.bike.utility.Pref;

import java.util.ArrayList;

public class BookOrderAdapter extends RecyclerView.Adapter<BookOrderAdapter.ItemViewHolder> {
    private ArrayList<BikeModule> orderList;
    Context context;
    private RequestOptions requestOptions;
    private Pref pref;


    public BookOrderAdapter(Context context, ArrayList<BikeModule> orderList) {
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
                .inflate(R.layout.product_row, parent, false);
        return new BookOrderAdapter.ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {

        holder.tvProductName.setText(orderList.get(position).getBike_title());
        holder.tvProductDescription.setText(orderList.get(position).getBike_des());
        holder.tvProductAddress.setText(orderList.get(position).getBike_address());
        holder.tvProductPrice.setText("$"+orderList.get(position).getBike_price());
        holder.tvProductColor.setText("Bike Color : "+orderList.get(position).getBike_color());
        holder.tvProductNumber.setText(orderList.get(position).getBike_number());

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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pref.getType().equals("Admin")) {
                    Intent intent = new Intent(context, HistoryActivity.class);
                    intent.putExtra("bike_id",orderList.get(position).getBike_id());
                    context.startActivity(intent);
                }
            }
        });


        if (pref.getType().equals("Admin")){
            holder.imgCart.setVisibility(View.GONE);
        }else if (pref.getType().equals("User")){
            holder.imgCart.setVisibility(View.VISIBLE);
        }



    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgProduct,imgCart;
        private TextView tvProductName,tvProductDescription,tvProductAddress,tvProductPrice,tvProductColor,tvProductNumber,tvProductStatus;
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
            imgCart = (ImageView)itemView.findViewById(R.id.imgCart);

        }
    }


}
