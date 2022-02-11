package com.android.schoolstore;

public class CartModel {
    private int cart_id;
    private String food_id;
    private String customer_id;
    private String store_id;
    private byte[] food_image;
    private String food_name;
    private String food_description;
    private String food_price;
    private String food_quantity;
    private Double food_total;


    //cart_id,food_id TEXT,customer_id TEXT, store_id, Picture BLOB, food_name TEXT, food_descriptions TEXT, Price TEXT, Quantity TEXT, Total REAL

    //constructor

    public CartModel(int cart_id, String food_id, String customer_id, String store_id, byte[] food_image, String food_name, String food_description, String food_price, String food_quantity, Double food_total) {
        this.cart_id = cart_id;
        this.food_id = food_id;
        this.customer_id = customer_id;
        this.store_id = store_id;
        this.food_image = food_image;
        this.food_name = food_name;
        this.food_description = food_description;
        this.food_price = food_price;
        this.food_quantity = food_quantity;
        this.food_total = food_total;
    }


    //getter and setter method


    public int getCart_id() {
        return cart_id;
    }

    public void setCart_id(int cart_id) {
        this.cart_id = cart_id;
    }

    public String getFood_id() {
        return food_id;
    }

    public void setFood_id(String food_id) {
        this.food_id = food_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public byte[] getFood_image() {
        return food_image;
    }

    public void setFood_image(byte[] food_image) {
        this.food_image = food_image;
    }

    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public String getFood_description() {
        return food_description;
    }

    public void setFood_description(String food_description) {
        this.food_description = food_description;
    }

    public String getFood_price() {
        return food_price;
    }

    public void setFood_price(String food_price) {
        this.food_price = food_price;
    }

    public String getFood_quantity() {
        return food_quantity;
    }

    public void setFood_quantity(String food_quantity) {
        this.food_quantity = food_quantity;
    }

    public Double getFood_total() {
        return food_total;
    }

    public void setFood_total(Double food_total) {
        this.food_total = food_total;
    }
}
