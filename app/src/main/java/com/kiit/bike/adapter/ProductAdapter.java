package com.kiit.bike.adapter;

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
import com.kiit.bike.R;
import com.kiit.bike.activity.BookActivity;
import com.kiit.bike.activity.CreateBikeActivity;
import com.kiit.bike.activity.MainActivity;
import com.kiit.bike.activity.ProfileActivity;
import com.kiit.bike.module.BikeModule;
import com.kiit.bike.utility.Constants;
import com.kiit.bike.utility.Pref;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ItemViewHolder> {
    private ArrayList<BikeModule> orderList;
    Context context;
    private RequestOptions requestOptions;
    private Pref pref;


    public ProductAdapter(Context context, ArrayList<BikeModule> orderList) {
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
        return new ProductAdapter.ItemViewHolder(itemView);
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
                    ((MainActivity) context).setImageVisible(false, orderList.get(position).getBike_id());
                }else if (pref.getType().equals("User")){
                    ((MainActivity) context).setImageVisible(false, orderList.get(position).getBike_id());
                
                    ((MainActivity)context).showDialog(context,orderList.get(position).getBike_id(),orderList.get(position).getBike_des(),orderList.get(position).getBike_color(),
                            orderList.get(position).getBike_address(),orderList.get(position).getBike_title(),orderList.get(position).getBike_price(),orderList.get(position).getBike_img(),
                            orderList.get(position).getBike_number(),orderList.get(position).getBike_status(),String.valueOf(orderList.get(position).getBike_latitude()),String.valueOf(orderList.get(position).getBike_longitude()),orderList.get(position).getEmail());
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (pref.getType().equals("Admin")) {
                    ((MainActivity) context).setImageVisible(true, orderList.get(position).getBike_id());
                }
                // Toast.makeText(context,"Hi",Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        if (pref.getType().equals("Admin")){
            holder.imgCart.setVisibility(View.VISIBLE);
        }else if (pref.getType().equals("User")){
            holder.imgCart.setVisibility(View.GONE);
        }

        holder.imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CreateBikeActivity.class);
                intent.putExtra("flag","update");
                intent.putExtra("bike_id",orderList.get(position).getBike_id());
                intent.putExtra("bike_des",orderList.get(position).getBike_des());
                intent.putExtra("bike_color",orderList.get(position).getBike_color());
                intent.putExtra("bike_address",orderList.get(position).getBike_address());
                intent.putExtra("bike_title",orderList.get(position).getBike_title());
                intent.putExtra("bike_price",orderList.get(position).getBike_price());
                intent.putExtra("bike_img",orderList.get(position).getBike_img());
                intent.putExtra("bike_number",orderList.get(position).getBike_number());
                intent.putExtra("bike_status",orderList.get(position).getBike_status());
                intent.putExtra("bike_latitude",String.valueOf(orderList.get(position).getBike_latitude()));
                intent.putExtra("bike_longitude",String.valueOf(orderList.get(position).getBike_longitude()));
                context.startActivity(intent);
            }
        });


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
