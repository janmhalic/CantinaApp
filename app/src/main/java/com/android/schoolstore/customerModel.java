package com.android.schoolstore;

public class customerModel {
    private String customer_id;
    private String restaurant_id;
    private String count;
    private String date;
    private String status;
    private String c_id;
    private String time;

    public customerModel(String customer_id, String restaurant_id,String count, String date, String status,String c_id, String time) {
        this.customer_id = customer_id;
        this.restaurant_id = restaurant_id;
        this.count = count;
        this.date = date;
        this.status = status;
        this.c_id = c_id;
        this.time = time;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(String restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getC_id() {
        return c_id;
    }

    public void setC_id(String c_id) {
        this.c_id = c_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
