package com.unipi.p17172.emarket.models;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;

@IgnoreExtraProperties
public class OrderModel {

    private String id;
    private String currency;
    @ServerTimestamp
    private Date date_created;
    @ServerTimestamp
    private Date date_completed;
    @ServerTimestamp
    private Date date_delivery;
    private String order_status;
    private double total;
    private String userId;
    private ArrayList<String> products;

    public OrderModel() {} // Needed for Firebase

    public OrderModel(String id, String currency, Date date_created, Date date_completed, Date date_delivery, String order_status, double total, String userId, ArrayList<String> products) {
        this.id = id;
        this.currency = currency;
        this.date_created = date_created;
        this.date_completed = date_completed;
        this.date_delivery = date_delivery;
        this.order_status = order_status;
        this.total = total;
        this.userId = userId;
        this.products = products;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @ServerTimestamp
    public Date getDate_created() {
        return date_created;
    }

    public void setDate_created(Date date_created) {
        this.date_created = date_created;
    }

    @ServerTimestamp
    public Date getDate_completed() {
        return date_completed;
    }

    public void setDate_completed(Date date_completed) {
        this.date_completed = date_completed;
    }

    @ServerTimestamp
    public Date getDate_delivery() {
        return date_delivery;
    }

    public void setDate_delivery(Date date_delivery) {
        this.date_delivery = date_delivery;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<String> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<String> products) {
        this.products = products;
    }
}
