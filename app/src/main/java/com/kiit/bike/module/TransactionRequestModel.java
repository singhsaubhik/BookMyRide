package com.kiit.bike.module;

/**
 * Created by kiit on 05-12-2017.
 */

public class TransactionRequestModel {

    private String paymentMethod;
    private String payTo;
    private String price;


    public TransactionRequestModel() {
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPayTo() {
        return payTo;
    }

    public void setPayTo(String payTo) {
        this.payTo = payTo;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
