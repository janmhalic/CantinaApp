package com.android.schoolstore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class my_orders extends AppCompatActivity {
    DBHelper dbHelper;
    TextView all, processing, completed, order_title, content;
    SessionManager sessionManager;
    String current_user_ID;
    SQLiteDatabase sqLiteDatabase;
    RecyclerView recyclerView;
    ordersAdapter ordersAdapter;
    ImageButton close;
    LinearLayout empty_order;
    Dialog dialog;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    Button browse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        getID();


        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL, false));

        try{
            sessionManager = new SessionManager(getApplicationContext());
            current_user_ID = sessionManager.getId();
            dbHelper = new DBHelper(this);
            sqLiteDatabase = dbHelper.getWritableDatabase();
            displayOrder(current_user_ID);
            dialog = new Dialog(my_orders.this);
            calendar = Calendar.getInstance();
            simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error. Reload App!", Toast.LENGTH_SHORT).show();

        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), homepage.class));
                finish();
            }
        });

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayOrder(current_user_ID);
                all.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.order_bg));
                processing.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.order_bg_none));
                completed.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.order_bg_none));
                all.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.red_500));
                processing.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.black));
                completed.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.black));
            }
        });

        processing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayWaitingOrder(current_user_ID);
                all.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.order_bg_none));
                processing.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.order_bg));
                completed.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.order_bg_none));
                processing.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.red_500));
                all.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.black));
                completed.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.black));
            }
        });

        completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayCompletedOrder(current_user_ID);
                all.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.order_bg_none));
                processing.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.order_bg_none));
                completed.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.order_bg));
                completed.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.red_500));
                processing.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.black));
                all.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.black));
            }
        });

    }

    private void displayCompletedOrder(String current_user_id) {

        String search = "Finished";

        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM orders WHERE user_id = ? and order_status = ?", new String[]{current_user_id, search});
        if (c.getCount() > 0) {
            empty_order.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            ArrayList<OrderModel>orderModels = new ArrayList<>();
            while(c.moveToNext()){
                int order_id = c.getInt(0);
                String userId = c.getString(1);
                String store_id = c.getString(2);
                String item_id = c.getString(3);
                String order_qty = c.getString(4);
                String order_price = c.getString(5);
                String order_subtotal = c.getString(6);
                String order_method = c.getString(7);
                String order_method_price = c.getString(8);
                String order_total = c.getString(9);
                String order_address = c.getString(10);
                String order_status = c.getString(11);
                String order_date = c.getString(12);
                String order_status_date = c.getString(13);
                String order_description = c.getString(14);
                String item_name = c.getString(15);
                String item_des = c.getString(16);
                byte[] item_picture = c.getBlob(17);
                String count = c.getString(18);

                orderModels.add(new OrderModel(order_id,userId,store_id,item_id,order_qty,order_price,order_subtotal,order_method,order_method_price,order_total,
                        order_address,order_status,order_date,order_status_date,order_description,item_name,item_des,item_picture, count));

            }
            c.close();
            ordersAdapter = new ordersAdapter(this,R.layout.single_order,orderModels,sqLiteDatabase, dialog, calendar, simpleDateFormat);
            recyclerView.setAdapter(ordersAdapter);
        }else{
            empty_order.setVisibility(View.VISIBLE);
            content.setText("You don't have any completed orders yet.");
            order_title.setText("No completed orders yet");
            recyclerView.setVisibility(View.GONE);
        }


    }

    private void displayWaitingOrder(String current_user_id) {

        String search = "Waiting";

        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM orders WHERE user_id = ? and order_status = ?", new String[]{current_user_id, search});
        if (c.getCount() > 0) {
            empty_order.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            ArrayList<OrderModel>orderModels = new ArrayList<>();
            while(c.moveToNext()){
                int order_id = c.getInt(0);
                String userId = c.getString(1);
                String store_id = c.getString(2);
                String item_id = c.getString(3);
                String order_qty = c.getString(4);
                String order_price = c.getString(5);
                String order_subtotal = c.getString(6);
                String order_method = c.getString(7);
                String order_method_price = c.getString(8);
                String order_total = c.getString(9);
                String order_address = c.getString(10);
                String order_status = c.getString(11);
                String order_date = c.getString(12);
                String order_status_date = c.getString(13);
                String order_description = c.getString(14);
                String item_name = c.getString(15);
                String item_des = c.getString(16);
                byte[] item_picture = c.getBlob(17);
                String count = c.getString(18);

                orderModels.add(new OrderModel(order_id,userId,store_id,item_id,order_qty,order_price,order_subtotal,order_method,order_method_price,order_total,
                        order_address,order_status,order_date,order_status_date,order_description,item_name,item_des,item_picture, count));

            }
            c.close();
            ordersAdapter = new ordersAdapter(this,R.layout.single_order,orderModels,sqLiteDatabase,dialog, calendar, simpleDateFormat);
            recyclerView.setAdapter(ordersAdapter);
        }else{
            empty_order.setVisibility(View.VISIBLE);
            content.setText("You don't have any orders yet. Try one of our awesome restaurants and place your first order!");
            order_title.setText("No orders yet");
            recyclerView.setVisibility(View.GONE);
        }

    }

    private void getID() {
        recyclerView = findViewById(R.id.orders_rv);
        close = findViewById(R.id.close_page);
        all = findViewById(R.id.all);
        processing = findViewById(R.id.processing);
        completed = findViewById(R.id.completed);
        empty_order = findViewById(R.id.empty_panel);
        order_title = findViewById(R.id.order_title) ;
        content = findViewById(R.id.content);
        browse = findViewById(R.id.browse);
    }

    private void displayOrder(String user_id) {
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM orders WHERE user_id = ?", new String[]{user_id});
        if (c.getCount() > 0) {
            empty_order.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            ArrayList<OrderModel>orderModels = new ArrayList<>();
            while(c.moveToNext()){
                int order_id = c.getInt(0);
                String userId = c.getString(1);
                String store_id = c.getString(2);
                String item_id = c.getString(3);
                String order_qty = c.getString(4);
                String order_price = c.getString(5);
                String order_subtotal = c.getString(6);
                String order_method = c.getString(7);
                String order_method_price = c.getString(8);
                String order_total = c.getString(9);
                String order_address = c.getString(10);
                String order_status = c.getString(11);
                String order_date = c.getString(12);
                String order_status_date = c.getString(13);
                String order_description = c.getString(14);
                String item_name = c.getString(15);
                String item_des = c.getString(16);
                byte[] item_picture = c.getBlob(17);
                String count = c.getString(18);

                orderModels.add(new OrderModel(order_id,userId,store_id,item_id,order_qty,order_price,order_subtotal,order_method,order_method_price,order_total,
                        order_address,order_status,order_date,order_status_date,order_description,item_name,item_des,item_picture, count));

            }
            c.close();
            ordersAdapter = new ordersAdapter(this,R.layout.single_order,orderModels,sqLiteDatabase,dialog, calendar, simpleDateFormat);
            recyclerView.setAdapter(ordersAdapter);
        }else{
            empty_order.setVisibility(View.VISIBLE);
            content.setText("You don't have any orders yet. Try one of our awesome restaurants and place your first order!");
            order_title.setText("No orders yet");
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}