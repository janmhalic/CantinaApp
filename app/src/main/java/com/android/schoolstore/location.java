package com.android.schoolstore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class location extends AppCompatActivity {
   ImageButton close;
   LinearLayout editPanel;
   CardView currentPanel;
   TextView textCurrent, textTitle;
   EditText Input;
   Button editBtn, submit, update;
    SessionManager sessionManager;
    public static DBHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;
    String idSession;
    loadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        initID();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        try{
            dbHelper = new DBHelper(this);
            sqLiteDatabase = dbHelper.getWritableDatabase();
            sessionManager = new SessionManager(getApplicationContext());
            idSession = sessionManager.getId();
            loadingDialog = new loadingDialog(this);


        }catch (Exception e){
            e.printStackTrace();
        }

        panelView(idSession);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.showDialog("Please wait...");
                String input = Input.getText().toString();
                String my_id = idSession;
                if (input.equals("")) {
                    loadingDialog.hideDialog();
                    Toast.makeText(getApplicationContext(), "Insert Location!", Toast.LENGTH_SHORT).show();
                }
                else{
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("user_id", my_id);
                    contentValues.put("user_loc", input);

                    long result = sqLiteDatabase.insert("locations",null,contentValues);
                    if(result==-1){
                        loadingDialog.hideDialog();
                        Toast.makeText(getApplicationContext(), "Something problem in Inserting Location", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Insert Location Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(location.this, location.class));
                        finish();
                    }
                }
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.showDialog("Please wait...");
                String input = Input.getText().toString();
                String my_id = idSession;
                if (input.equals("")) Toast.makeText(getApplicationContext(), "Insert Location!", Toast.LENGTH_SHORT).show();
                else{
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("user_loc", input);

                    long result = sqLiteDatabase.update("locations",contentValues,"user_id="+my_id,null);
                    if(result==-1){
                        loadingDialog.hideDialog();
                        Toast.makeText(getApplicationContext(), "Something problem in Updating Location", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Location Updated!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(location.this,location.class));
                        finish();
                    }
                }

            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentPanel.setVisibility(View.GONE);
                editPanel.setVisibility(View.VISIBLE);
                String current = textCurrent.getText().toString();
                Input.setText(current);
                submit.setVisibility(View.GONE);
                update.setVisibility(View.VISIBLE);
                textTitle.setText("Update your location.");


            }
        });



    }

    private void panelView(String current_id) {
        sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor current = sqLiteDatabase.rawQuery("SELECT * FROM locations WHERE user_id = ?",new String[]{current_id});
        if(current.getCount()>0){
            currentPanel.setVisibility(View.VISIBLE);
            editPanel.setVisibility(View.GONE);

            while(current.moveToNext()){
                textCurrent.setText(current.getString(2));
            }
            current.close();
        }else{
            currentPanel.setVisibility(View.GONE);
            editPanel.setVisibility(View.VISIBLE);
        }
    }


    private void initID() {
        close = findViewById(R.id.close);
        editPanel = findViewById(R.id.insert_panel);
        currentPanel = findViewById(R.id.current_panel);
        textCurrent = findViewById(R.id.current_location);
        editBtn = findViewById(R.id.editBtn);
        Input = findViewById(R.id.location_input);
        submit = findViewById(R.id.submit_location);
        update = findViewById(R.id.update_location);
        textTitle = findViewById(R.id.textTitle);
    }
}