package com.android.schoolstore;

public class OrderModel {
    private int oder_id;
    private String user_id;
    private String store_id;
    private String item_id;
    private String order_qty;
    private String order_price;
    private String order_subtotal;
    private String order_method;
    private String order_method_price;
    private String order_total;
    private String order_address;
    private String order_status;
    private String order_date;
    private String order_status_date;
    private String order_description;
    private String item_name;
    private String item_des;
    private byte[] item_picture;
    private String count;


    //constructor

    public OrderModel(int oder_id, String user_id, String store_id, String item_id, String order_qty, String order_price, String order_subtotal, String order_method, String order_method_price, String order_total, String order_address, String order_status, String order_date, String order_status_date, String order_description,String item_name,String item_des, byte[] item_picture, String count) {
        this.oder_id = oder_id;
        this.user_id = user_id;
        this.store_id = store_id;
        this.item_id = item_id;
        this.order_qty = order_qty;
        this.order_price = order_price;
        this.order_subtotal = order_subtotal;
        this.order_method = order_method;
        this.order_method_price = order_method_price;
        this.order_total = order_total;
        this.order_address = order_address;
        this.order_status = order_status;
        this.order_date = order_date;
        this.order_status_date = order_status_date;
        this.order_description = order_description;
        this.item_name = item_name;
        this.item_des = item_des;
        this.item_picture = item_picture;
        this.count = count;
    }

    //getter and setter method


    public int getOder_id() {
        return oder_id;
    }

    public void setOder_id(int oder_id) {
        this.oder_id = oder_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getOrder_qty() {
        return order_qty;
    }

    public void setOrder_qty(String order_qty) {
        this.order_qty = order_qty;
    }

    public String getOrder_price() {
        return order_price;
    }

    public void setOrder_price(String order_price) {
        this.order_price = order_price;
    }

    public String getOrder_subtotal() {
        return order_subtotal;
    }

    public void setOrder_subtotal(String order_subtotal) {
        this.order_subtotal = order_subtotal;
    }

    public String getOrder_method() {
        return order_method;
    }

    public void setOrder_method(String order_method) {
        this.order_method = order_method;
    }

    public String getOrder_method_price() {
        return order_method_price;
    }

    public void setOrder_method_price(String order_method_price) {
        this.order_method_price = order_method_price;
    }

    public String getOrder_total() {
        return order_total;
    }

    public void setOrder_total(String order_total) {
        this.order_total = order_total;
    }

    public String getOrder_address() {
        return order_address;
    }

    public void setOrder_address(String order_address) {
        this.order_address = order_address;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getOrder_status_date() {
        return order_status_date;
    }

    public void setOrder_status_date(String order_status_date) {
        this.order_status_date = order_status_date;
    }

    public String getOrder_description() {
        return order_description;
    }

    public void setOrder_description(String order_description) {
        this.order_description = order_description;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_des() {
        return item_des;
    }

    public void setItem_des(String item_des) {
        this.item_des = item_des;
    }

    public byte[] getItem_picture() {
        return item_picture;
    }

    public void setItem_picture(byte[] item_picture) {
        this.item_picture = item_picture;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
