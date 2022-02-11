package com.android.schoolstore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class view_food extends AppCompatActivity {
    ImageView foodImage,sellerImage;
    TextView name,des,price,qty,email,proId,value, by,total,tTitle,p,sellerName;
    ImageButton back,add,minus;
    Button add_to_cart;
    int id=0,count = 0,stocks = 0;
    double cost = 0;
    SessionManager sessionManager;
    SQLiteDatabase sqLiteDatabase;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_food);
        //initialize session manager
        sessionManager = new SessionManager(getApplicationContext());
        //get username from session
        String email1 = sessionManager.getUsername();

        try{
            dbHelper = new DBHelper(this);

            sqLiteDatabase = dbHelper.getWritableDatabase();

        }catch (Exception e){
            e.printStackTrace();
        }

        initId();
        if(getIntent().getBundleExtra("cart_food")!=null){
            Bundle bundle = getIntent().getBundleExtra("cart_food");
            id=bundle.getInt("id");
            //for image
            byte[]bytes=bundle.getByteArray("image");
            Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            foodImage.setImageBitmap(bitmap);
            //for set name

            name.setText(bundle.getString("name"));
            des.setText(bundle.getString("description"));
            String owner = bundle.getString("addBy");
            by.setText(owner);
            seller_picture(owner);
            double price1 = Double.parseDouble(bundle.getString("price"));
            int stock = Integer.parseInt(bundle.getString("qty"));
            cost = price1;
            stocks = stock;
            proId.setText(String.valueOf(id));
            email.setText(email1);

        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), homepage.class);
                startActivity(i);
                finish();
            }
        });

        //solving arithmetic problem

        price.setText(String.valueOf(cost));
        qty.setText(String.valueOf(stocks));
        if(count == 0){
            total.setVisibility(View.GONE);
            tTitle.setVisibility(View.GONE);
            p.setVisibility(View.GONE);
        }


        add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap bitmap=((BitmapDrawable)foodImage.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] pic = stream.toByteArray();
                int ids = Integer.parseInt(proId.getText().toString());
                String cs_em = email.getText().toString();
                String name1 = name.getText().toString();
                String desc = des.getText().toString();
                String price1 = price.getText().toString();
                String stock = qty.getText().toString();
                String quantity1 = value.getText().toString();
                String sum = total.getText().toString();

                ContentValues contentValues = new ContentValues();
                contentValues.put("ID", ids);
                contentValues.put("Email", cs_em);
                contentValues.put("Picture", pic);
                contentValues.put("Name", name1);
                contentValues.put("Descriptions", desc);
                contentValues.put("Price", price1);
                contentValues.put("Stocks", stock);
                contentValues.put("Quantity", quantity1);
                contentValues.put("Total", sum);

                if(count > 0){
                    long result = sqLiteDatabase.insert("my_cart",null,contentValues);
                    if(result==-1){
                        Toast.makeText(getApplicationContext(), "Add to Cart Failed!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Added to Cart Successful!", Toast.LENGTH_SHORT).show();
                        update_product();
                        startActivity(new Intent(view_food.this, homepage.class));
                        finish();
                    }

                }else Toast.makeText(getApplicationContext(), "Need Quantity", Toast.LENGTH_SHORT).show();

            }
        });








    }

    private void update_product() {
        int update_stock = stocks;
        String stocks = String.valueOf(update_stock);
        ContentValues contentValues = new ContentValues();
        contentValues.put("quantity", stocks);
        sqLiteDatabase.update("food",contentValues,"id="+id,null);
    }

    private void seller_picture(String owner) {

        Cursor result = dbHelper.fetchUser(owner);
        try{
            if(result.getCount()>0){
                while(result.moveToNext()){
                    String name = result.getString(1);
                    byte[] profile_image = result.getBlob(6);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(profile_image, 0, profile_image.length);
                    sellerImage.setImageBitmap(bitmap);
                    sellerName.setText(name);
                }

            }else{
                Toast.makeText(getApplicationContext(), "No Image to Display", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(result != null){
                result.close();
            }
        }
    }

    public void increment(View v){


        if(stocks >= 1) {
            count++;
            stocks--;
            double summary = count*cost;
            total.setText(String.valueOf(summary));
            total.setVisibility(View.VISIBLE);
            tTitle.setVisibility(View.VISIBLE);
            p.setVisibility(View.VISIBLE);

        }else if(stocks == 0){
            Toast.makeText(getApplicationContext(), "Out of Stocks", Toast.LENGTH_SHORT).show();
        }

        value.setText(String.valueOf(count));
        qty.setText(String.valueOf(stocks));

    }

    public void decrement(View v){


        if(count >= 1){
            count--;
            stocks++;
            double summary = count*cost;
            total.setText(String.valueOf(summary));

        }
        qty.setText(String.valueOf(stocks));
        value.setText(String.valueOf(count));

        if(count == 0){
            total.setVisibility(View.GONE);
            tTitle.setVisibility(View.GONE);
            p.setVisibility(View.GONE);
        }

    }


    private void initId() {
        foodImage=findViewById(R.id.foodImage);
        name = findViewById(R.id.foodName);
        des =  findViewById(R.id.foodDescription);
        price = findViewById(R.id.foodPrice);
        qty = findViewById(R.id.foodQty);
        back = findViewById(R.id.back);
        email = findViewById(R.id.email);
        proId = findViewById(R.id.pro_idd);
        add_to_cart = findViewById(R.id.submit_to_cart);
        value = findViewById(R.id.value);
        add = findViewById(R.id.increment);
        minus = findViewById(R.id.decrement);
        by = findViewById(R.id.addBy);
        total = findViewById(R.id.total);
        tTitle = findViewById(R.id.totalTitle);
        p = findViewById(R.id.P);
        sellerName = findViewById(R.id.sellerName);
        sellerImage = findViewById(R.id.sellerImage);
    }
}