package com.android.schoolstore;

import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class mycart extends AppCompatActivity {
    ImageButton back;
    TextView store_name, cartText, text_subtotal, text_total, store_ids, textLocation, shipping;
    CheckBox cod, pickup;
    LinearLayout empty, summary_panel;
    RelativeLayout location_panel;
    DBHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;
    RecyclerView recyclerView;
    CartAdapter cartAdapter;
    String current_user_ID, gettingMethod, methodPrice, Date, sec;
    double subtotal = 0, total = 0;
    Button checkOut, browse;
    SessionManager sessionManager;
    double payment = 0;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat, time;
    boolean locationStatus = false;
    Dialog dialog;
    loadingDialog loadingDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycart);
        declareIds();

        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL, false));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        try{
            sessionManager = new SessionManager(getApplicationContext());
            current_user_ID = sessionManager.getId();
            calendar = Calendar.getInstance();
            simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            time = new SimpleDateFormat("ss");
            Date = simpleDateFormat.format(calendar.getTime());
            sec = time.format(calendar.getTime());
            dbHelper = new DBHelper(this);
            dialog = new Dialog(mycart.this);
            loadingDialog = new loadingDialog(this);

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error. Reload App!", Toast.LENGTH_SHORT).show();

        }




        Subtotal();
        //display subtotal
        text_subtotal.setText("₱"+subtotal);
        total = subtotal;
        text_total.setText("₱"+total);

        cod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cod.isChecked()){
                    pickup.setChecked(false);
                    total = subtotal + payment;
                    shipping.setVisibility(View.VISIBLE);
                }else{
                    total = subtotal + 0;
                    shipping.setVisibility(View.GONE);
                }
                text_total.setText("₱"+total);
                gettingMethod = "Delivery";
                methodPrice = String.valueOf(payment);
            }
        });

        pickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pickup.isChecked()){
                    cod.setChecked(false);
                    total = subtotal + 0;
                    text_total.setText("₱"+total);
                }
                shipping.setVisibility(View.GONE);
                gettingMethod = "Pick Up";
                methodPrice = "0";
            }
        });

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), homepage.class));
                finish();
            }
        });






        //display data
        sqLiteDatabase = dbHelper.getReadableDatabase();
        try{

            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM my_cart WHERE customer_id=?",new String[]{current_user_ID});
            if (cursor.getCount()>0){
                empty.setVisibility(View.GONE);
                location_panel.setVisibility(View.VISIBLE);
                summary_panel.setVisibility(View.VISIBLE);
                store_name.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                cartText.setTextSize(18);
                indicateLocation();

                ArrayList<CartModel>models = new ArrayList<>();

                while(cursor.moveToNext()){
                    int cartId = cursor.getInt(0);
                    String food_id = cursor.getString(1);
                    String customer_id = cursor.getString(2);
                    String store_id = cursor.getString(3);
                    byte[] food_pic = cursor.getBlob(4);
                    String food_name = cursor.getString(5);
                    String food_description = cursor.getString(6);
                    String food_price = cursor.getString(7);
                    String food_qty = cursor.getString(8);
                    Double food_total = cursor.getDouble(9);
                    store_ids.setText(store_id);

                    DisplayStore();
                    models.add(new CartModel(cartId,food_id,customer_id,store_id,food_pic,food_name,food_description,food_price,food_qty,food_total));
                }
                cursor.close();
                cartAdapter = new CartAdapter(this,R.layout.cartsingledata,models,sqLiteDatabase);
                recyclerView.setAdapter(cartAdapter);

            }else {
                empty.setVisibility(View.VISIBLE);
                store_name.setVisibility(View.GONE);
                cartText.setTextSize(20);
                location_panel.setVisibility(View.GONE);
                summary_panel.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            }

        }catch (Exception e){
            e.printStackTrace();

        }

        textLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mycart.this, location.class));
            }
        });


        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!locationStatus){
                    confirmLocationDialog();

                }else {
                    if(cod.isChecked() || pickup.isChecked()) {

                    confirmDialog();
                    }else Toast.makeText(getApplicationContext(), "Choose Delivery or Pick Up.", Toast.LENGTH_SHORT).show();

                }
            }
        });


    }

    private void confirmDialog() {
        dialog.setContentView(R.layout.place_order_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        Button ok = dialog.findViewById(R.id.ok_btn);
        EditText des = dialog.findViewById(R.id.order_des);
        ImageButton close = dialog.findViewById(R.id.close);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.showDialog("Placing order...");



                    String instruction = des.getText().toString().trim();


                    String user_id = current_user_ID;
                    String store_id = store_ids.getText().toString();
                    String order_method = gettingMethod;
                    String order_method_price = methodPrice;
                    String order_total = text_total.getText().toString();
                    String order_address = textLocation.getText().toString();
                    String order_subtotal = text_subtotal.getText().toString();
                    String order_status = "Waiting";
                    String order_date = Date;
                    String time = sec;
                    String order_date_status = "0000";
                    if (instruction.equals("")) instruction = "None";


                    Cursor place = sqLiteDatabase.rawQuery("SELECT * FROM my_cart WHERE customer_id=?", new String[]{current_user_ID});
                    int getCount = place.getCount();

                    if (getCount > 0) {
                        while (place.moveToNext()) {
                            String item_id = place.getString(1);
                            String order_qty = place.getString(8);
                            String order_price = place.getString(7);
                            String itemName = place.getString(5);
                            String itemDescription = place.getString(6);
                            byte[] itemImage = place.getBlob(4);

                            ContentValues contentValues = new ContentValues();
                            contentValues.put("user_id", user_id);
                            contentValues.put("store_id", store_id);
                            contentValues.put("item_id", item_id);
                            contentValues.put("order_qty", order_qty);
                            contentValues.put("order_price", order_price);
                            contentValues.put("order_subtotal", order_subtotal);
                            contentValues.put("order_method", order_method);
                            contentValues.put("order_method_price", order_method_price);
                            contentValues.put("order_total", order_total);
                            contentValues.put("order_address", order_address);
                            contentValues.put("order_status", order_status);
                            contentValues.put("order_date", order_date);
                            contentValues.put("order_status_date", order_date_status);
                            contentValues.put("order_description", instruction);
                            contentValues.put("item_name", itemName);
                            contentValues.put("item_des", itemDescription);
                            contentValues.put("item_picture", itemImage);
                            contentValues.put("order_count", time);

                            long result = sqLiteDatabase.insert("orders", null, contentValues);
                            if (result == -1) {
                                Toast.makeText(getApplicationContext(), "Something problem in Placing Order.", Toast.LENGTH_SHORT).show();
                                loadingDialog.hideDialog();
                            } else {
                                Toast.makeText(getApplicationContext(), "Place order successful.", Toast.LENGTH_SHORT).show();

                                long cart_delete = sqLiteDatabase.delete("my_cart", "customer_id="+current_user_ID,null);
                                if (cart_delete != -1) {

                                    startActivity(new Intent(mycart.this, my_orders.class));

                                    finish();
                                }
                                else{
                                    loadingDialog.hideDialog();
                                    Toast.makeText(getApplicationContext(), "Failed to Remove Cart items", Toast.LENGTH_SHORT).show();
                                }

                            }


                        }
                        place.close();
                    } else
                        Toast.makeText(getApplicationContext(), "Failed to Fetch Cart. Try Reload.", Toast.LENGTH_SHORT).show();
                String num = String.valueOf(getCount);
                insertCustomer(user_id,store_id,order_date,num,order_status, time);




            }

            private void insertCustomer(String user_id, String store_id, String order_date, String num, String status, String time) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("c_user_id", user_id);
                contentValues.put("c_store_id", store_id);
                contentValues.put("c_order_count", num);
                contentValues.put("c_order_date", order_date);
                contentValues.put("c_status", status);
                contentValues.put("c_count", time);
                sqLiteDatabase.insert("customers", null, contentValues);

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    /// end of activity



    private void indicateLocation() {
        Cursor location = sqLiteDatabase.rawQuery("SELECT * FROM locations WHERE user_id = ?", new String[]{current_user_ID});
        if (location.getCount()>0){
            locationStatus = true;
            while(location.moveToNext()){
                textLocation.setText(location.getString(2));

            }
            location.close();
        }else {
            confirmLocationDialog();
            locationStatus = false;

        }

    }

    private void confirmLocationDialog() {
        dialog.setContentView(R.layout.confirm_location_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        Button go_to_location = dialog.findViewById(R.id.to_location_btn);
        ImageButton close = dialog.findViewById(R.id.close);
        go_to_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mycart.this, location.class));
                finish();

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void DisplayStore() {
        sqLiteDatabase = dbHelper.getReadableDatabase();
        String store = store_ids.getText().toString();

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM restaurants WHERE res_id=?",new String[]{store});
        if(cursor.getCount()>0){
            while(cursor.moveToNext()){
                store_name.setText(cursor.getString(1));
                double fee = Double.parseDouble(cursor.getString(8));
                payment = fee;
                shipping.setText("₱"+payment);
            }
            cursor.close();
        }else{
            Toast.makeText(getApplicationContext(), "not get", Toast.LENGTH_SHORT).show();
        }
    }


    private void Subtotal() {
        double sub = dbHelper.getSubtotal(current_user_ID);
        subtotal = sub;

    }

    private void declareIds() {
        recyclerView = findViewById(R.id.cart_rv);
        back = findViewById(R.id.back);
        empty = findViewById(R.id.empty_cart);
        store_name = findViewById(R.id.store_name);
        cartText = findViewById(R.id.cartText);
        summary_panel = findViewById(R.id.summary_panel);
        location_panel = findViewById(R.id.location_panel);
        text_subtotal = findViewById(R.id.subtotal);
        text_total = findViewById(R.id.total);
        store_ids = findViewById(R.id.store_Idd);
        textLocation = findViewById(R.id.textLocation);
        shipping = findViewById(R.id.fee);
        cod = findViewById(R.id.Cod);
        pickup = findViewById(R.id.pickup);
        checkOut = findViewById(R.id.checkOut);
        browse = findViewById(R.id.browse);
    }


}