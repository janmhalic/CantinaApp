package com.android.schoolstore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class my_customers extends AppCompatActivity {
    RecyclerView recyclerView;
    SQLiteDatabase sqLiteDatabase;
    SessionManager sessionManager;
    DBHelper dbHelper;
    TextView restaurantName, orderCount, all, processing, completed, content_title, context;
    String owner_in_session;
    String res_id;
    customerAdapter customerAdapter;
    ImageButton close;
    LinearLayout empty_customer;
    loadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_customers);
        findId();


        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        try{
            dbHelper = new DBHelper(this);
            sqLiteDatabase = dbHelper.getWritableDatabase();
            sessionManager = new SessionManager(getApplicationContext());
            owner_in_session = sessionManager.getId();
            loadingDialog = new loadingDialog(this);


        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error: Database. Try Reload", Toast.LENGTH_SHORT).show();
        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        if(getIntent().getBundleExtra("store_data")!=null) {
            Bundle bundle = getIntent().getBundleExtra("store_data");
            res_id = bundle.getString("store_id");

            restaurantName.setText(bundle.getString("store_name"));
            displayCustomers(res_id);

        }

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayCustomers(res_id);
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
                displayProcessing(res_id);
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
                displayCompleted(res_id);
                all.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.order_bg_none));
                processing.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.order_bg_none));
                completed.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.order_bg));
                completed.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.red_500));
                processing.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.black));
                all.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.black));

            }
        });








    }



    private void displayCompleted(String res_id) {

        String search = "Finished";

        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM customers WHERE c_store_id=? and c_status = ?", new String[]{res_id,search});
        int count = c.getCount();
        ArrayList<customerModel> customerModels = new ArrayList<>();
        if (count > 0){
            recyclerView.setVisibility(View.VISIBLE);
            empty_customer.setVisibility(View.GONE);
            while(c.moveToNext()){
                String id = String.valueOf(c.getInt(0));
                String user = c.getString(1);
                String store = c.getString(2);
                String counts = c.getString(3);
                String date = c.getString(4);
                String status = c.getString(5);
                String time = c.getString(6);
                customerModels.add(new customerModel(user, store,counts,date, status, id, time));

            }

        }else{
            empty_customer.setVisibility(View.VISIBLE);
            content_title.setText("No completed orders yet.");
            context.setText("");
            recyclerView.setVisibility(View.GONE);
        }
        customerAdapter = new customerAdapter(this, R.layout.single_customer,customerModels,sqLiteDatabase);
        recyclerView.setAdapter(customerAdapter);
        c.close();

    }

    private void displayProcessing(String res_id) {

        String search = "Waiting";

        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM customers WHERE c_store_id=? and c_status = ?", new String[]{res_id,search});
        int count = c.getCount();
        ArrayList<customerModel> customerModels = new ArrayList<>();
        if (count > 0){
            recyclerView.setVisibility(View.VISIBLE);
            empty_customer.setVisibility(View.GONE);
            while(c.moveToNext()){
                String id = String.valueOf(c.getInt(0));
                String user = c.getString(1);
                String store = c.getString(2);
                String counts = c.getString(3);
                String date = c.getString(4);
                String status = c.getString(5);
                String time = c.getString(6);
                customerModels.add(new customerModel(user, store,counts,date, status, id, time));

            }

        }else {
            empty_customer.setVisibility(View.VISIBLE);
            content_title.setText("No New Customers yet.");
            context.setText("There is no processing orders.");
            recyclerView.setVisibility(View.GONE);
        }
        customerAdapter = new customerAdapter(this, R.layout.single_customer,customerModels,sqLiteDatabase);
        recyclerView.setAdapter(customerAdapter);
        c.close();

    }




    private void displayCustomers(String restaurant_id) {
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM customers WHERE c_store_id=?", new String[]{restaurant_id});
        int count = c.getCount();
        ArrayList<customerModel> customerModels = new ArrayList<>();
        if (count > 0){
            recyclerView.setVisibility(View.VISIBLE);
            empty_customer.setVisibility(View.GONE);
                while(c.moveToNext()){
                    String id = String.valueOf(c.getInt(0));
                    String user = c.getString(1);
                    String store = c.getString(2);
                    String counts = c.getString(3);
                    String date = c.getString(4);
                    String status = c.getString(5);
                    String time = c.getString(6);
                    customerModels.add(new customerModel(user, store,counts,date,status,id,time));

                }

        }else{
            empty_customer.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            content_title.setText("No Customers yet.");
            context.setText("Wait for customers to place their orders.");
        }
        customerAdapter = new customerAdapter(this, R.layout.single_customer,customerModels,sqLiteDatabase);
        recyclerView.setAdapter(customerAdapter);
        orderCount.setText(count+" Customers");
        c.close();
    }

    private void findId() {
        restaurantName = findViewById(R.id.res_name);
        recyclerView = findViewById(R.id.customer_rv);
        close = findViewById(R.id.close);
        orderCount = findViewById(R.id.count);
        all = findViewById(R.id.all);
        processing = findViewById(R.id.processing);
        completed = findViewById(R.id.completed);
        empty_customer = findViewById(R.id.empty_customer);
        content_title = findViewById(R.id.c_title);
        context = findViewById(R.id.context);
    }
}