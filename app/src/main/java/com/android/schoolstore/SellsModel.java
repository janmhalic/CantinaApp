package com.android.schoolstore;

public class SellsModel {
    private int sell_id;
    private String sell_name;
    private String sell_description;
    private String sell_price;
    private String sell_qty;
    private String sell_AddedBy;
    private byte[] sell_image;

    //constructor

    public SellsModel(int sell_id, String sell_name, String sell_description, String sell_price, String sell_qty, String sell_AddedBy, byte[] sell_image) {
        this.sell_id = sell_id;
        this.sell_name = sell_name;
        this.sell_description = sell_description;
        this.sell_price = sell_price;
        this.sell_qty = sell_qty;
        this.sell_AddedBy = sell_AddedBy;
        this.sell_image = sell_image;
    }

    //getter and setter Method


    public int getSell_id() {
        return sell_id;
    }

    public void setSell_id(int sell_id) {
        this.sell_id = sell_id;
    }

    public String getSell_name() {
        return sell_name;
    }

    public void setSell_name(String sell_name) {
        this.sell_name = sell_name;
    }

    public String getSell_description() {
        return sell_description;
    }

    public void setSell_description(String sell_description) {
        this.sell_description = sell_description;
    }

    public String getSell_price() {
        return sell_price;
    }

    public void setSell_price(String sell_price) {
        this.sell_price = sell_price;
    }

    public String getSell_qty() {
        return sell_qty;
    }

    public void setSell_qty(String sell_qty) {
        this.sell_qty = sell_qty;
    }

    public String getSell_AddedBy() {
        return sell_AddedBy;
    }

    public void setSell_AddedBy(String sell_AddedBy) {
        this.sell_AddedBy = sell_AddedBy;
    }

    public byte[] getSell_image() {
        return sell_image;
    }

    public void setSell_image(byte[] sell_image) {
        this.sell_image = sell_image;
    }
}






