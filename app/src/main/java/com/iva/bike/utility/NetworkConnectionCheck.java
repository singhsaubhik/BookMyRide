package com.iva.bike.utility;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

public class NetworkConnectionCheck {
    private Context context;
    // flag for GPS status
    boolean isGPSEnabled = false;

    public NetworkConnectionCheck(Context context){
        this.context = context;
    }

    public boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        return info != null && info.isConnectedOrConnecting();
    }

    public AlertDialog getNetworkActiveAlert(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Network Status");
        builder.setMessage("Network connection not available. Please connect the network.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        dialogInterface.cancel();

                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //dialogInterface.cancel();
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        return  alertDialog;
    }

    public boolean isGPSEnabled(){
        LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);

        // getting GPS status
        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        return isGPSEnabled;
    }

    public android.app.AlertDialog getSettingsAlert(){
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle("GPS Status");
        builder.setMessage("GPS is not enabled. Please enable the GPS.");
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });
        android.app.AlertDialog alertDialog = builder.create();
        return  alertDialog;
    }

}
