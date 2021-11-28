package com.upt.cti.smartwallet.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Payment implements Serializable {

    public String timestamp;
    private double cost;
    private String name;
    private String type;


    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Payment(String timestamp, double cost, String name, String type) {
        this.timestamp = timestamp;
        this.cost = cost;
        this.name = name;
        this.type = type;
    }

    public Payment() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }

    public String getType() {
        return type;
    }

    public Payment copy() {
        Payment payment = new Payment();
        payment.timestamp = new String(timestamp);
        payment.cost = cost;
        payment.name =  new String(name);
        payment.type =  new String(type);
        return payment;
    }
}