package com.android.schoolstore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DBNAME = "store.db";
    public static final int VER = 1;

    public DBHelper(Context context) {
        super(context, DBNAME, null, VER);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table users(id INTEGER primary key AUTOINCREMENT,fullName TEXT, contact TEXT, email TEXT, password TEXT, access TEXT, profile BLOB, student_id TEXT)");
        db.execSQL("create table food(id INTEGER primary key AUTOINCREMENT,picture BLOB, name TEXT,description TEXT, price TEXT, quantity TEXT,added_by TEXT,user_id TEXT,own_by TEXT, own_id TEXT)");
        db.execSQL("create table my_cart(cart_id INTEGER primary key AUTOINCREMENT,food_id TEXT,customer_id TEXT, store_id TEXT, Picture BLOB, food_name TEXT, food_descriptions TEXT, Price TEXT, Quantity TEXT, Total REAL)");
        db.execSQL("create table restaurants(res_id INTEGER  primary key AUTOINCREMENT, res_name TEXT , res_address TEXT, res_photo BLOB NOT NULL, res_contact TEXT, res_email TEXT,opening_times TEXT, res_addedBy TEXT, shipping TEXT)");
        db.execSQL("create table locations(loc_id INTEGER primary key AUTOINCREMENT, user_id TEXT, user_loc TEXT)");
        db.execSQL("create table orders(order_id INTEGER primary key AUTOINCREMENT, user_id TEXT,store_id TEXT,item_id TEXT,order_qty TEXT,order_price TEXT,order_subtotal TEXT,order_method TEXT,order_method_price TEXT, order_total TEXT,order_address TEXT,order_status TEXT,order_date TEXT,order_status_date TEXT,order_description TEXT, item_name TEXT, item_des TEXT, item_picture BLOB, order_count TEXT)");
        db.execSQL("create table customers(c_id INTEGER primary key AUTOINCREMENT, c_user_id TEXT, c_store_id TEXT, c_order_count TEXT, c_order_date TEXT, c_status TEXT, c_count TEXT)");
        db.execSQL("create table ratings(rate_id INTEGER primary key AUTOINCREMENT, rate_user_id TEXT,rate_store_id TEXT,rate_name TEXT, rate_text TEXT,rate_stars REAL, rate_date TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop Table if exists users");
        db.execSQL("drop Table if exists food");
        db.execSQL("drop Table if exists my_cart");
        db.execSQL("drop Table if exists restaurants");
        db.execSQL("drop Table if exists locations");
        db.execSQL("drop Table if exists orders");
        db.execSQL("drop Table if exists customers");
        db.execSQL("drop Table if exists ratings");
    }


    public boolean check_email(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from users where email=?", new String[]{email});
        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }


    }

    public boolean check_emails_pass(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from users where email=? and password=?", new String[]{email, password});
        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }

    }

    public Cursor getFoods(String sql) {
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }

    public Cursor fetchUser(String userEmail) {
        SQLiteDatabase database1 = this.getWritableDatabase();
        Cursor c = database1.rawQuery("SELECT * FROM users WHERE email =?", new String[]{userEmail});
        return c;
    }



    public Double getSubtotal(String current){
        double total = 0;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT SUM(Total) FROM my_cart WHERE customer_id =?", new String[]{current});
        if(cursor.moveToFirst()){
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    public float getStarTotal(String restaurant){
        float total = 0;
        float zero = 0;
        float first = 1;
        float sec = 2;
        float thi = 3;
        float fou = 4;
        float fif = 5;

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT SUM(rate_stars) FROM ratings WHERE rate_store_id =?", new String[]{restaurant});
        if(cursor.moveToFirst()){
            total = cursor.getFloat(0);
        }
        cursor.close();
        if(total <= 5)return zero;
        else if(total <= 15) return first;
        else if (total <= 25)return sec;
        else if (total <= 40) return thi;
        else if(total <= 55) return fou;
        else if (total <= 70) return fif;
        else if (total <= 80) return fif;
        else if (total > 80) return fif;
        else return total;
    }



    public Cursor cartInfo(String store, String user_id){
        SQLiteDatabase database1 = this.getWritableDatabase();
        Cursor c = database1.rawQuery("SELECT * FROM my_cart WHERE store_id = ? AND customer_id = ?", new String[]{store,user_id});
        return c;
    }









}







