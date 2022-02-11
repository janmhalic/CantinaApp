package com.android.schoolstore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.database.CursorWindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class homepage extends AppCompatActivity{
    public static DBHelper dbHelper;
    SessionManager sessionManager;
    Dialog dialog;
    ImageButton to_cart,to_acc;
    SQLiteDatabase sqLiteDatabase;
    RecyclerView recyclerView;
    CardView empty;
    String check_user;
    MyAdapter myAdapter;
    LinearLayout no_restaurant;
    loadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);


        findId();
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        try{
            dbHelper = new DBHelper(this);
            dialog = new Dialog(homepage.this);
            displayShops();
            sessionManager = new SessionManager(getApplicationContext());
            check_user = sessionManager.getId();
            if(check_user.equals(""))errorDialog();
            loadingDialog = new loadingDialog(this);

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error. Reload App!", Toast.LENGTH_SHORT).show();

        }




        to_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.showDialog("Loading...");
                String current_name = sessionManager.getFullname();
                if (current_name.equals("")) {
                    errorDialog();
                } else {

                    startActivity(new Intent(homepage.this, account.class));
                    loadingDialog.hideDialog();
                }


            }
        });

        to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.showDialog("Loading...");
                String current_name = sessionManager.getFullname();
                if(current_name.equals("")) {
                    errorDialog();
                }
                else{
                    startActivity(new Intent(homepage.this, mycart.class));
                    loadingDialog.hideDialog();
                }



            }
        });



    }


    private void displayShops() {
        sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM restaurants", null);
        ArrayList<Model>models = new ArrayList<>();
        int count = cursor.getCount();
        if(count>0){
            no_restaurant.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), count+" Restaurants", Toast.LENGTH_SHORT).show();
            if(cursor.moveToFirst()){
                while(!cursor.isAfterLast()){
                    int id = cursor.getInt(0);
                    String storeName = cursor.getString(1);
                    String storeAddress = cursor.getString(2);
                    byte[] Image = cursor.getBlob(3);
                    String storeContact = cursor.getString(4);
                    String storeEmail = cursor.getString(5);
                    String storeOpening = cursor.getString(6);
                    String storeAdd = cursor.getString(7);
                    String storeShip = cursor.getString(8);
                    models.add(new Model(id,storeName,storeAddress,Image,storeContact,storeEmail,storeOpening,storeAdd,storeShip));
                    cursor.moveToNext();
                }

            }
            myAdapter = new MyAdapter(this,R.layout.single_store,models,sqLiteDatabase,dbHelper);
            recyclerView.setAdapter(myAdapter);
            cursor.close();

        }else{
            no_restaurant.setVisibility(View.VISIBLE);
        }


    }



    private void errorDialog() {
        dialog.setContentView(R.layout.error_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        Button go = dialog.findViewById(R.id.go);

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set login false
                sessionManager.setLogin(false);
                //ser username empty
                sessionManager.editor.clear();
                sessionManager.editor.commit();
                //redirect to activity
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                // finish activity
                finish();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void findId() {

//        user = findViewById(R.id.current);
        recyclerView = findViewById(R.id.rv);
        to_cart = findViewById(R.id.go_to_cart);
        empty = findViewById(R.id.empty);
        to_acc = findViewById(R.id.acc_btn);
        no_restaurant = findViewById(R.id.no_restaurant);
    }


}