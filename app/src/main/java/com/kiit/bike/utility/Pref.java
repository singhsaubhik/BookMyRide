package com.kiit.bike.utility;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by KIRTIKA on 3/2/2017.
 */

public class Pref {

    private SharedPreferences _pref;
    private static final String PREF_FILE = "com.chatapp";
    private SharedPreferences.Editor _editorPref;

    public Pref(Context context) {
        _pref = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        _editorPref = _pref.edit();
    }



    public void saveEmail(String email) {
        _editorPref.putString("email", email);
        _editorPref.commit();
    }


    public String getEmail() {
        return _pref.getString("email", "");
    }

    public void saveMobile(String mobile) {
        _editorPref.putString("mobile", mobile);
        _editorPref.commit();
    }


    public String getMobile() {
        return _pref.getString("mobile", "");
    }

    public void saveName(String name) {
        _editorPref.putString("name", name);
        _editorPref.commit();
    }


    public String getName() {
        return _pref.getString("name", "");
    }



    public void saveType(String type) {
        _editorPref.putString("type", type);
        _editorPref.commit();
    }


    public String getType() {
        return _pref.getString("type", "");
    }

    public void saveImage(String image) {
        _editorPref.putString("image", image);
        _editorPref.commit();
    }


    public String getImage() {
        return _pref.getString("image", "");
    }

    public void saveBikeImage(String image) {
        _editorPref.putString("bike_image", image);
        _editorPref.commit();
    }


    public String getBikeImage() {
        return _pref.getString("bike_image", "");
    }

    public void saveLatitude(String latitude) {
        _editorPref.putString("latitude", latitude);
        _editorPref.commit();
    }


    public String getLatitude() {
        return _pref.getString("latitude", "");
    }

    public void saveLongitude(String longitude) {
        _editorPref.putString("longitude", longitude);
        _editorPref.commit();
    }


    public String getLongitude() {
        return _pref.getString("longitude", "");
    }



    public void saveLatNew(String latitude_new) {
        _editorPref.putString("latitude_new", latitude_new);
        _editorPref.commit();
    }


    public String getLatNew() {
        return _pref.getString("latitude_new", "");
    }


    public void saveLongitudeNew(String longitude_new) {
        _editorPref.putString("longitude_new", longitude_new);
        _editorPref.commit();
    }


    public String getLongitudeNew() {
        return _pref.getString("longitude_new", "");
    }


    public void saveAddress(String address) {
        _editorPref.putString("address", address);
        _editorPref.commit();
    }
    public String getAddress() {
        return _pref.getString("address", "");
    }


    public void saveBikeId(String bike_id) {
        _editorPref.putString("bike_id", bike_id);
        _editorPref.commit();
    }
    public String getBikeId() {
        return _pref.getString("bike_id", "");
    }


    public void saveCurrentAddress(String current_address) {
        _editorPref.putString("current_address", current_address);
        _editorPref.commit();
    }
    public String getCurrentAddress() {
        return _pref.getString("current_address", "");
    }








}
