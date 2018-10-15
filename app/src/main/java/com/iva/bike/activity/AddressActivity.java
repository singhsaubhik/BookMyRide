package com.iva.bike.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.iva.bike.R;
import com.iva.bike.adapter.PlacesAutoCompleteAdapter;
import com.iva.bike.utility.Constants;
import com.iva.bike.utility.NetworkConnectionCheck;
import com.iva.bike.utility.Pref;
import com.iva.bike.utility.RecyclerItemClickListener;


public class AddressActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    protected GoogleApiClient mGoogleApiClient;
    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(
            new LatLng(22.5726, 88.3639), new LatLng(22.5958, 88.2636));
    private EditText mAutocompleteView;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    ImageView delete;
    NetworkConnectionCheck networkConnectionCheck;
    private String flag;
    Pref pref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        initialize();
    }

    private void initialize(){


        networkConnectionCheck = new NetworkConnectionCheck(this);

        mAutocompleteView = (EditText) findViewById(R.id.autocomplete_places);
        delete = (ImageView) findViewById(R.id.cross);



        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this, R.layout.address_row, BOUNDS_INDIA, null);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .build();


        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAutoCompleteAdapter);
        pref = new Pref(this);

        if (getIntent().getExtras()!=null){
            flag = getIntent().getStringExtra("flag");
        }

        //Autocomplete Text Change...............................................................
        autotextChage();


        //RecyclerView Item Touch Listener.......................................................
        itemTouchListener();



    }


    public void autotextChage() {
        mAutocompleteView.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (!s.toString().equals("") && mGoogleApiClient.isConnected()) {
                    mAutoCompleteAdapter.getFilter().filter(s.toString());


                } else if (!mGoogleApiClient.isConnected()) {
                    Toast.makeText(getApplicationContext(), Constants.API_NOT_CONNECTED, Toast.LENGTH_SHORT).show();

                }
                if (s.length()>0){
                    mRecyclerView.setVisibility(View.VISIBLE);
                }else {
                    mRecyclerView.setVisibility(View.GONE);
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                //  pbLoader.setVisibility(View.GONE);

            }

            public void afterTextChanged(Editable s) {
                // pbLoader.setVisibility(View.VISIBLE);
            }
        });
    }

    public void itemTouchListener() {
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final PlacesAutoCompleteAdapter.PlaceAutocomplete item = mAutoCompleteAdapter.getItem(position);
                final String placeId = String.valueOf(item.placeId);
                Log.i("TAG", "Autocomplete item selected: " + item.description);
                        /*
                             issue a request to the Places Geo Data API to retrieve a Place object with additional details about the place.
                         */
//
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getCount() == 1) {
                            String loc = String.valueOf(places.get(0).getAddress());
                            double latitude =places.get(0).getLatLng().latitude;
                            double longitude=places.get(0).getLatLng().longitude;



                           if (flag.equals("create")){
                               Intent data = new Intent();
                               data.putExtra("flagLocation", loc);
                               data.putExtra("latitude",String.valueOf(latitude));
                               data.putExtra("longitude",String.valueOf(longitude));
                               setResult(1, data);
                               hideKeyboard();
                               finish();
                           }else if (flag.equals("pickup")){
                               Intent data = new Intent();
                               data.putExtra("flagLocation", loc);
                               data.putExtra("latitude",String.valueOf(latitude));
                               data.putExtra("longitude",String.valueOf(longitude));
                               pref.saveAddress(loc);
                               pref.saveLatNew(String.valueOf(latitude));
                               pref.saveLongitudeNew(String.valueOf(longitude));
                               setResult(2, data);
                               hideKeyboard();
                               finish();
                           }else if (flag.equals("drop")){
                               Intent data = new Intent();
                               data.putExtra("flagLocation1",pref.getAddress());
                               data.putExtra("latitude1",pref.getLatNew());
                               data.putExtra("longitude1",pref.getLongitudeNew());
                               data.putExtra("flagLocation2", loc);
                               data.putExtra("latitude2",String.valueOf(latitude));
                               data.putExtra("longitude2",String.valueOf(longitude));
                               setResult(3, data);
                               hideKeyboard();
                               finish();
                           }else if (flag.equals("pickupNew")){
                               Intent data = new Intent();
                               data.putExtra("flagLocation3", loc);
                               data.putExtra("latitude3",String.valueOf(latitude));
                               data.putExtra("longitude3",String.valueOf(longitude));
                               setResult(4, data);
                               hideKeyboard();
                               finish();
                           }else if (flag.equals("dropNew")){
                               Intent data = new Intent();

                               data.putExtra("flagLocation5", loc);
                               data.putExtra("latitude5",String.valueOf(latitude));
                               data.putExtra("longitude5",String.valueOf(longitude));
                               setResult(5, data);
                               hideKeyboard();
                               finish();
                           }



                        } else {
                            Toast.makeText(getApplicationContext(), Constants.SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                Log.i("TAG", "Clicked: " + item.description);
                Log.i("TAG", "Called getPlaceById to get Place details for " + item.placeId);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

    }

    private void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mAutoCompleteAdapter.setGoogleApiClient(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mAutoCompleteAdapter.setGoogleApiClient(null);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, Constants.API_NOT_CONNECTED, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()){
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
    }

}
