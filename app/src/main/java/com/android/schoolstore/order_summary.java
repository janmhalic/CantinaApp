package com.android.schoolstore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class order_summary extends AppCompatActivity {
    ImageButton back;
    int customer_id;
    String restaurant_id, owner_in_session, Date, storeName,time, c_id;
    DBHelper dbHelper;
    RecyclerView recyclerView;
    SQLiteDatabase sqLiteDatabase;
    SessionManager sessionManager;
    TextView customer_name, customer_contact, customer_status, subtotal, receive, total, customer_location, payment_method, order_des, orderStatus, date_complete;
    summaryAdapter summaryAdapter;
    Button received_btn,failed_btn,view_profile;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    LinearLayout lower_panel;
    Dialog dialog;
    loadingDialog loadingDialog;
    my_customers my_customers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);
        findId();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));

        try{
            dbHelper = new DBHelper(this);
            sqLiteDatabase = dbHelper.getWritableDatabase();
            sessionManager = new SessionManager(getApplicationContext());
            owner_in_session = sessionManager.getId();
            dialog = new Dialog(order_summary.this);
            calendar = Calendar.getInstance();
            simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date = simpleDateFormat.format(calendar.getTime());
            loadingDialog = new loadingDialog(this);

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error: Database. Try Reload", Toast.LENGTH_SHORT).show();
        }

        if(getIntent().getBundleExtra("store_data")!=null){
            Bundle bundle = getIntent().getBundleExtra("store_data");
            customer_id = bundle.getInt("customer_id");
            restaurant_id = bundle.getString("restaurant_id");
            storeName = bundle.getString("store_name");
            time = bundle.getString("dateAdded");
            c_id = bundle.getString("c_id");


            try{
                displayOrders(String.valueOf(customer_id), restaurant_id, time);
                displayCustomerInfo();

            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error. getting  info.", Toast.LENGTH_SHORT).show();
            }

        }

        received_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.showDialog("Please Wait...");
                String receive = "Finished";
                ContentValues contentValues = new ContentValues();
                contentValues.put("order_status", receive);
                contentValues.put("order_status_date", Date);

                long result = sqLiteDatabase.update("orders",contentValues,"order_count="+time,null);
                if(result==-1){
                    Toast.makeText(getApplicationContext(), "Order Failed to Submit", Toast.LENGTH_SHORT).show();

                }
                else{
                    received_btn.setVisibility(View.GONE);
                    failed_btn.setVisibility(View.GONE);
                    orderStatus.setVisibility(View.VISIBLE);
                    lower_panel.setVisibility(View.VISIBLE);
                    orderStatus.setText("Order received by Customer");
                    receivedDialog();
                    updateCustomer(c_id, receive);
                }
                loadingDialog.hideDialog();
            }
        });

        failed_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.showDialog("Please Wait...");
                String receive = "Receive Failed";
                ContentValues contentValues = new ContentValues();
                contentValues.put("order_status", receive);
                contentValues.put("order_status_date", Date);

                long result = sqLiteDatabase.update("orders",contentValues,"order_count="+time,null);
                if(result==-1){
                    Toast.makeText(getApplicationContext(), "Order Failed to Submit", Toast.LENGTH_SHORT).show();
                }
                else{
                    received_btn.setVisibility(View.GONE);
                    failed_btn.setVisibility(View.GONE);
                    orderStatus.setVisibility(View.VISIBLE);
                    lower_panel.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Order Failed to Receive.", Toast.LENGTH_SHORT).show();
                    updateCustomer(c_id, receive);
                    Bundle b = new Bundle();
                    b.putString("store_id", restaurant_id);
                    b.putString("store_name", storeName);

                    Intent intent=new Intent(order_summary.this,my_customers.class);
                    intent.putExtra("store_data",b);
                    startActivity(intent);
                    finish();
                }
                loadingDialog.hideDialog();
            }
        });

        view_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.showDialog("Loading...");
                Bundle bundle = new Bundle();
                bundle.putInt("id", customer_id);
                bundle.putString("store_name", customer_name.getText().toString());
                Intent intent=new Intent(getApplicationContext(),view_profile.class);
                intent.putExtra("profile_data",bundle);
                startActivity(intent);
                loadingDialog.hideDialog();
            }
        });


    }

    private void receivedDialog() {
        dialog.setContentView(R.layout.receive_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        Button finish = dialog.findViewById(R.id.finish);

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.showDialog("PLease wait...");
                //pass store data to view customers

                Bundle b = new Bundle();
                b.putString("store_id", restaurant_id);
                b.putString("store_name", storeName);

                Intent intent=new Intent(order_summary.this,my_customers.class);
                intent.putExtra("store_data",b);
                startActivity(intent);
                loadingDialog.hideDialog();
                finish();
            }
        });

        dialog.show();
    }


    private void updateCustomer(String cus,String receive) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("c_status", receive);

        long result = sqLiteDatabase.update("customers",contentValues,"c_id="+cus,null);
        if(result==-1){
            Toast.makeText(getApplicationContext(), "Order Failed to update Customer", Toast.LENGTH_SHORT).show();
        }



    }

    private void displayOrders(String id, String res_id, String date) {
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM orders WHERE user_id = ? and store_id = ? and order_count = ? ", new String[]{id,res_id, date});
        if (c.getCount() > 0) {
            ArrayList<OrderModel> orderModels = new ArrayList<>();
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

                subtotal.setText(order_subtotal);
                receive.setText("₱"+order_method_price);
                total.setText(order_total);
                customer_location.setText(order_address);
                payment_method.setText(order_method);
                order_des.setText(order_description);
                customer_status.setText(order_status);

                if(order_status.equals("Finished")){
                    received_btn.setVisibility(View.GONE);
                    failed_btn.setVisibility(View.GONE);
                    customer_status.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.green));
                    lower_panel.setVisibility(View.VISIBLE);
                    orderStatus.setText("Order received by Customer");
                    date_complete.setText(order_status_date);

                }else if(order_status.equals("Receive Failed")){
                    received_btn.setVisibility(View.GONE);
                    failed_btn.setVisibility(View.GONE);
                    customer_status.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.red));
                    lower_panel.setVisibility(View.VISIBLE);
                    date_complete.setText(order_status_date);
                    orderStatus.setText("Order Failed to Receive.");
                }else{
                    received_btn.setVisibility(View.VISIBLE);
                    failed_btn.setVisibility(View.VISIBLE);
                    customer_status.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.red));
                    lower_panel.setVisibility(View.GONE);
                    date_complete.setText(order_status_date);
                }


                orderModels.add(new OrderModel(order_id,userId,store_id,item_id,order_qty,order_price,order_subtotal,order_method,order_method_price,order_total,
                        order_address,order_status,order_date,order_status_date,order_description,item_name,item_des,item_picture, count));


            }

            summaryAdapter = new summaryAdapter(this,R.layout.single_summary,orderModels,sqLiteDatabase);
            recyclerView.setAdapter(summaryAdapter);
            c.close();
        }else Toast.makeText(getApplicationContext(), "Empty Order.", Toast.LENGTH_SHORT).show();
    }

    private void displayCustomerInfo() {
        String id_cus = String.valueOf(customer_id);
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM users WHERE id = ?", new String[]{id_cus});
        if(c.getCount()>0){
            while(c.moveToNext()){
                customer_name.setText(c.getString(1));
                customer_contact.setText(c.getString(2));

            }
            c.close();
        }else Toast.makeText(getApplication(), "Failed to retrieve Customer Info.", Toast.LENGTH_SHORT).show();


    }

    private void findId() {
        back = findViewById(R.id.back);
        customer_name = findViewById(R.id.customer_name);
        customer_contact = findViewById(R.id.customer_contact);
        customer_status = findViewById(R.id.customer_status);
        subtotal = findViewById(R.id.subtotal);
        receive = findViewById(R.id.receive);
        total = findViewById(R.id.total);
        customer_location = findViewById(R.id.customer_location);
        payment_method = findViewById(R.id.payment_method);
        order_des = findViewById(R.id.order_des);
        received_btn = findViewById(R.id.received_btn);
        recyclerView = findViewById(R.id.customer_rv);
        failed_btn = findViewById(R.id.failed_btn);
        orderStatus = findViewById(R.id.orderStatus);
        view_profile = findViewById(R.id.view_profile);
        lower_panel = findViewById(R.id.lower_panel);
        date_complete = findViewById(R.id.date_complete);
    }
}