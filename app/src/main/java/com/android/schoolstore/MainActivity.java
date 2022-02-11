package com.android.schoolstore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.file.Paths;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView toSignup;
    EditText email, password;
    Button login;
    DBHelper dbHelper;
    SessionManager sessionManager;
    loadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findId();

        //Initialize SessionManager
        sessionManager = new SessionManager(getApplicationContext());


        try{
            dbHelper = new DBHelper(this);
            loadingDialog = new loadingDialog(this);

        }catch (Exception e){
            e.printStackTrace();
        }

        loadingDialog.showDialog("Please Wait...");
        boolean log = sessionManager.getLogin();
        if(log) startActivity(new Intent(MainActivity.this, homepage.class));
        else loadingDialog.hideDialog();

        toSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, signUp.class));
            }
        });





        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.showDialog("Login...");
                String email1 = email.getText().toString().trim();
                String pass1 = password.getText().toString();



                if(TextUtils.isEmpty(email1) || pass1.equals("")){
                    Toast.makeText(MainActivity.this, "Insert Email and Password", Toast.LENGTH_SHORT).show();
                    loadingDialog.hideDialog();
                }

                else{
                    Boolean check_user = dbHelper.check_emails_pass(email1, pass1);
                    if(check_user){
                        Toast.makeText(MainActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                        //Store login in session
                        sessionManager.setLogin(true);
                        //Store email in session
                        sessionManager.setUsername(email1);
                        session(email1);
                        //redirect to activity
                        startActivity(new Intent(MainActivity.this, homepage.class));
                        password.setText("");
                        //finish activity
                        finish();
                    }else {
                        loadingDialog.hideDialog();
                        Toast.makeText(MainActivity.this, "User Account doesn't Exist", Toast.LENGTH_SHORT).show(); }
                }

            }
        });

    }

    private void findId() {
        toSignup = findViewById(R.id.toSignup);
        email = findViewById(R.id.userEmail);
        password = findViewById(R.id.userPassword);
        login = findViewById(R.id.loginBtn);
    }

    public void session(String userEmail){
        Cursor c = dbHelper.fetchUser(userEmail);
            try{
                if(c.getCount()>0){
                    while(c.moveToNext()){
                            int id = c.getInt(0);
                            String name = c.getString(1);
                            String contact1 = c.getString(2);
                            String access1 = (c.getString(5));

                        sessionManager.setId(String.valueOf(id));
                        sessionManager.setFullname(name);
                        sessionManager.setContact(contact1);
                        sessionManager.setAccess(access1);

                        }

                }else{
                    Toast.makeText(getApplicationContext(), "Empty User", Toast.LENGTH_SHORT).show();
                }
                c.close();

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                c.close();
            }

        }


    }
