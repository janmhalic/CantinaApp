package com.android.schoolstore;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class view_profile extends AppCompatActivity {
    int profile_id;
    TextView full_name, profile_contact, profile_email, profile_location, student_id;
    Button end;
    ImageView profile;
    SQLiteDatabase sqLiteDatabase;
    DBHelper dbHelper;
    loadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        end = findViewById(R.id.end);
        full_name = findViewById(R.id.name);
        profile_email = findViewById(R.id.profile_email);
        student_id = findViewById(R.id.profile_id);
        profile_contact = findViewById(R.id.profile_contact);
        profile_location = findViewById(R.id.profile_location);
        profile = findViewById(R.id.profile_image);

        try{

            dbHelper = new DBHelper(this);
            sqLiteDatabase = dbHelper.getReadableDatabase();
            loadingDialog = new loadingDialog(this);

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error. Reload App!", Toast.LENGTH_SHORT).show();

        }



        if(getIntent().getBundleExtra("profile_data")!=null){
            Bundle bundle = getIntent().getBundleExtra("profile_data");
            profile_id=bundle.getInt("id");
            String convert = String.valueOf(bundle.getInt("id"));
            sqLiteDatabase = dbHelper.getWritableDatabase();
            Cursor user = sqLiteDatabase.rawQuery("SELECT * FROM users WHERE id LIKE ?", new String[]{convert});
            if (user.getCount()>0){
                while(user.moveToNext()){
                    full_name.setText(user.getString(1));
                    student_id.setText(user.getString(7));
                    profile_email.setText(user.getString(3));
                    profile_contact.setText(user.getString(2));

                    byte[] photo = user.getBlob(6);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(photo, 0, photo.length);
                    profile.setImageBitmap(bitmap);
                }


            }else Toast.makeText(getApplicationContext(), "User info failed to fetch!. try to reload", Toast.LENGTH_SHORT).show();
            user.close();
            displayLocation(profile_id);
        }



        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.showDialog("Loading...");
                finish();
            }
        });


    }

    private void displayLocation(int profile_id) {
        String convert = String.valueOf(profile_id);
        sqLiteDatabase = dbHelper.getWritableDatabase();
        Cursor loc = sqLiteDatabase.rawQuery("SELECT * FROM locations WHERE user_id =?", new String[]{convert});
        if (loc.getCount()>0){
            while(loc.moveToNext()){
                profile_location.setText(loc.getString(2));
            }


        }else profile_location.setText("No location Added yet");
        loc.close();
    }




}