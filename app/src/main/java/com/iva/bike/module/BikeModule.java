package com.iva.bike.module;

public class BikeModule {
    private String bike_id;
    private String bike_des;
    private String bike_color;
    private String bike_address;
    private String bike_title;
    private String bike_price;
    private String bike_img;
    private String bike_number;
    private String bike_status;
    private String email;
    double bike_latitude,bike_longitude;
    private String start_date,end_date;

    public String getBike_status() {
        return bike_status;
    }

    public void setBike_status(String bike_status) {
        this.bike_status = bike_status;
    }



    public String getBike_id() {
        return bike_id;
    }

    public void setBike_id(String bike_id) {
        this.bike_id = bike_id;
    }

    public String getBike_des() {
        return bike_des;
    }

    public void setBike_des(String bike_des) {
        this.bike_des = bike_des;
    }

    public String getBike_color() {
        return bike_color;
    }

    public void setBike_color(String bike_color) {
        this.bike_color = bike_color;
    }

    public String getBike_address() {
        return bike_address;
    }

    public void setBike_address(String bike_address) {
        this.bike_address = bike_address;
    }

    public String getBike_title() {
        return bike_title;
    }

    public void setBike_title(String bike_title) {
        this.bike_title = bike_title;
    }

    public double getBike_latitude() {
        return bike_latitude;
    }

    public void setBike_latitude(double bike_latitude) {
        this.bike_latitude = bike_latitude;
    }

    public double getBike_longitude() {
        return bike_longitude;
    }

    public void setBike_longitude(double bike_longitude) {
        this.bike_longitude = bike_longitude;
    }

    public String getBike_price() {
        return bike_price;
    }

    public void setBike_price(String bike_price) {
        this.bike_price = bike_price;
    }

    public String getBike_img() {
        return bike_img;
    }

    public void setBike_img(String bike_img) {
        this.bike_img = bike_img;
    }

    public String getBike_number() {
        return bike_number;
    }

    public void setBike_number(String bike_number) {
        this.bike_number = bike_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }
}
