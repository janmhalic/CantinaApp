package com.android.schoolstore;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class view_item_cart extends AppCompatActivity {
    Button update, remove;
    ImageView item_image;
    ImageButton back;
    TextView item_name, item_des, item_price, value;
    int count, stocks = 0, cart_id;
    double total, cost = 0 ;
    SQLiteDatabase sqLiteDatabase;
    DBHelper dbHelper;
    SessionManager sessionManager;
    String current_user_ID, store_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item_cart);
        getID();

        try{
            sessionManager = new SessionManager(getApplicationContext());
            current_user_ID = sessionManager.getId();
            dbHelper = new DBHelper(this);
            sqLiteDatabase = dbHelper.getWritableDatabase();


        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error. Reload App!", Toast.LENGTH_SHORT).show();

        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(getIntent().getBundleExtra("item_data")!=null){
            Bundle bundle = getIntent().getBundleExtra("item_data");
            cart_id = bundle.getInt("cart_id");
            store_id = bundle.getString("store_id");
            String food_id = bundle.getString("item_id");

            try{
                getStocks(food_id);
                displayCartItem(cart_id);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error. All item info.", Toast.LENGTH_SHORT).show();
            }



        }else Toast.makeText(getApplicationContext(), "No item info. Try reload", Toast.LENGTH_SHORT).show();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String qty = String.valueOf(count);

                ContentValues contentValues = new ContentValues();
                contentValues.put("Quantity", qty);
                contentValues.put("Total", total);


                long result = sqLiteDatabase.update("my_cart",contentValues,"cart_id="+cart_id,null);
                if(result==-1){
                    Toast.makeText(getApplicationContext(), "Item Failed Update", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Item Updated", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(view_item_cart.this, mycart.class));
                    finish();
                }


            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqLiteDatabase = dbHelper.getReadableDatabase();
                long cart_delete = sqLiteDatabase.delete("my_cart", "cart_id="+cart_id,null);
                if(cart_delete != -1) {
                    Toast.makeText(getApplicationContext(), "Item Remove from Cart", Toast.LENGTH_SHORT).show();

                    Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM my_cart WHERE customer_id =? and store_id=?",new String[]{current_user_ID,store_id});
                    if (c.getCount() > 0) startActivity(new Intent(view_item_cart.this, mycart.class));
                    else startActivity(new Intent(view_item_cart.this, homepage.class));

                    c.close();
                }
            }
        });



    }

    private void displayCartItem(int cart_id) {
        String id = String.valueOf(cart_id);
        Cursor item = sqLiteDatabase.rawQuery("SELECT * FROM my_cart WHERE cart_id = ?", new String[]{id});
        if (item.getCount()>0){
            while(item.moveToNext()){
                item_name.setText(item.getString(5));
                item_des.setText(item.getString(6));
                cost = Double.parseDouble(item.getString(7));
                item_price.setText("₱"+cost);
                count = Integer.parseInt(item.getString(8));
                total = item.getDouble(9);
                value.setText(String.valueOf(count));

                byte[] itemImage = item.getBlob(4);
                Bitmap bitmap = BitmapFactory.decodeByteArray(itemImage, 0, itemImage.length);
                item_image.setImageBitmap(bitmap);

            }
            item.close();

        }else Toast.makeText(getApplicationContext(), "Failed to get Item info. Try reload.", Toast.LENGTH_SHORT).show();
    }

    private void getStocks(String foodID) {
        Cursor s = sqLiteDatabase.rawQuery("SELECT * FROM food WHERE id =? ", new String[]{foodID});
        if (s.getCount()>0){
            while (s.moveToNext()){
                stocks = Integer.parseInt(s.getString(5));
            }


        }else Toast.makeText(getApplicationContext(), "Failed to fetch stocks", Toast.LENGTH_SHORT).show();
        s.close();
    }

    public void increment(View v){
        if(stocks >= 1) {
            count++;
            stocks--;
            double summary = count*cost;
            total = summary;

        }else if(stocks == 0){
            Toast.makeText(getApplicationContext(), "Out of Stocks", Toast.LENGTH_SHORT).show();
        }

        value.setText(String.valueOf(count));

        if(count > 0){
            remove.setVisibility(View.GONE);
            update.setVisibility(View.VISIBLE);
        }

    }

    public void decrement(View v){
        if(count >= 1){
            count--;
            stocks++;
            double summary = count*cost;
            total = summary;

        }

        value.setText(String.valueOf(count));

        if(count == 0){
            remove.setVisibility(View.VISIBLE);
            update.setVisibility(View.GONE);
        }

    }

    private void getID() {
        update = findViewById(R.id.update_item);
        remove = findViewById(R.id.remove_item);
        item_image = findViewById(R.id.item_image);
        back = findViewById(R.id.back);
        item_name = findViewById(R.id.item_name);
        item_des = findViewById(R.id.item_des);
        item_price = findViewById(R.id.item_price);
        value = findViewById(R.id.value);
    }
}