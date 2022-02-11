package com.android.schoolstore;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class account extends AppCompatActivity {
    TextView email, full, con, access,user_id, s_id;
    ImageView profile;
    ImageButton back;
    CardView  my_stores_panel;
    RelativeLayout manager_panel;
    LinearLayout location_btn, orders_btn;
    Button add_fast_food,out, edit_profile;
    RecyclerView recyclerView;
    SessionManager sessionManager;
    SQLiteDatabase sqLiteDatabase;
    public static DBHelper dbHelper;
    MyStoresAdapter myStoresAdapter;
    String email1, name , acc , id ,number = "";
    Dialog dialog;
    loadingDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        findID();
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));

        try{
            dbHelper = new DBHelper(this);
            //initialize session manager
            sessionManager = new SessionManager(getApplicationContext());
            //get username from session
            email1 = sessionManager.getUsername();
            name = sessionManager.getFullname();
            acc = sessionManager.getAccess();
            id = sessionManager.getId();
            number = sessionManager.getContact();
            dialog = new Dialog(account.this);
            loadingDialog = new loadingDialog(this);

            displayMyStore(id);

        }catch (Exception e){
            e.printStackTrace();
        }
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL, false));
//        ///display sells




        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.showDialog("Please wait...");
                startActivity(new Intent(account.this, edit_profile.class));
                finish();
            }
        });





        //Set username on textview
        email.setText(email1);
        full.setText(name);
        access.setText(acc);
        user_id.setText(id);
        con.setText(number);

        //manager panel
        if(acc.equals("Seller")){
            manager_panel.setVisibility(View.VISIBLE);

        }else{
            manager_panel.setVisibility(View.GONE);
        }

        //location initialize
        location_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(account.this, location.class));
            }
        });

        orders_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(account.this, my_orders.class));
            }
        });


        Cursor result = dbHelper.fetchUser(email1);
        try{
            if(result.getCount()>0){
                while(result.moveToNext()){
                    byte[] profile_image = result.getBlob(6);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(profile_image, 0, profile_image.length);
                    profile.setImageBitmap(bitmap);
                    String id = result.getString(7);
                    s_id.setText(id);
                    if(id.equals("0000")) s_id.setVisibility(View.GONE);
                    else s_id.setVisibility(View.VISIBLE);
                }

            }else{
                Toast.makeText(getApplicationContext(), "no user found", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(result != null){
                result.close();
            }
        }







        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.logout_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(true);
                Button cancel = dialog.findViewById(R.id.cancel_btn);
                Button yes = dialog.findViewById(R.id.yes_btn);

                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadingDialog.showDialog("Logout...");

                        //set login false
                        sessionManager.setLogin(false);
                        //ser username empty
//                        sessionManager.setUsername("");
                        sessionManager.editor.clear();
                        sessionManager.editor.commit();
                        //redirect to activity
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        // finish activity
                        finish();

                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


                dialog.show();



            }
        });

        add_fast_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(account.this, fastfood.class));

            }
        });


    }

    private void displayMyStore(String user_id) {
        sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM restaurants WHERE res_addedBy=?", new String[]{user_id});
        ArrayList<Model>models = new ArrayList<>();
        if (cursor.getCount()>0){

            my_stores_panel.setVisibility(View.VISIBLE);
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
            myStoresAdapter = new MyStoresAdapter(this,R.layout.single_my_store,models,sqLiteDatabase,dialog);
            recyclerView.setAdapter(myStoresAdapter);
            cursor.close();

        }else my_stores_panel.setVisibility(View.GONE);



    }

    private void findID() {
        user_id = findViewById(R.id.user_id);
        out = findViewById(R.id.logOut);
        profile = findViewById(R.id.user_image);
        access = findViewById(R.id.user_access);
        con = findViewById(R.id.user_contact);
        full = findViewById(R.id.full_name);
        email = findViewById(R.id.user_email);
        back = findViewById(R.id.back);
        add_fast_food = findViewById(R.id.add_fast_food);
        manager_panel = findViewById(R.id.my_res);
        location_btn = findViewById(R.id.location_btn);
        my_stores_panel = findViewById(R.id.my_stores_panel);
        recyclerView = findViewById(R.id.myStores_rv);
        orders_btn = findViewById(R.id.order_btn);
        s_id = findViewById(R.id.s_id);
        edit_profile = findViewById(R.id.edit_profile);
    }




}

