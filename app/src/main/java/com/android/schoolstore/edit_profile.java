package com.android.schoolstore;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

public class edit_profile extends AppCompatActivity {
    ImageView profile;
    ImageButton close;
    Button update_profile, done_edit;
    EditText edit_name, edit_contact, edit_email, edit_password;
    TextView student_id;
    DBHelper dbHelper;
    Uri resultUri;
    SQLiteDatabase sqLiteDatabase;
    SessionManager sessionManager;
    String current_user_ID;
    loadingDialog loadingDialog;

    public static final int CAMERA_REQUEST = 100;
    public static final int STORAGE_REQUEST = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getID();

        try{
            sessionManager = new SessionManager(getApplicationContext());
            current_user_ID = sessionManager.getId();

            dbHelper = new DBHelper(this);
            sqLiteDatabase = dbHelper.getWritableDatabase();
            displayInfo(current_user_ID);
            loadingDialog = new loadingDialog(this);

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error. Reload App!", Toast.LENGTH_SHORT).show();

        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(edit_profile.this, account.class));
                finish();
            }
        });

        done_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.showDialog("Updating Account...");
                Bitmap bitmap=((BitmapDrawable)profile.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[]image=stream.toByteArray();

                String name = edit_name.getText().toString().trim();
                String contact = edit_contact.getText().toString().trim();
                String email = edit_email.getText().toString().trim();
                String password = edit_password.getText().toString().trim();

                ContentValues contentValues = new ContentValues();
                contentValues.put("fullName", name);
                contentValues.put("email", email);
                contentValues.put("password", password);
                contentValues.put("contact", contact);
                contentValues.put("profile", image);

                if(name.equals("") || contact.equals("") || email.equals("") || password.equals(""))
                    Toast.makeText(getApplicationContext(), "Complete Details!", Toast.LENGTH_SHORT).show();
                else{

                        long result = sqLiteDatabase.update("users",contentValues,"id="+current_user_ID,null);
                        if(result==-1){
                            Toast.makeText(getApplicationContext(), "Account Failed to Update", Toast.LENGTH_SHORT).show();
                            loadingDialog.hideDialog();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Account Updated", Toast.LENGTH_SHORT).show();
                            sessionManager.setUsername(email);
                            session(email);
                            startActivity(new Intent(edit_profile.this, account.class));
                            finish();
                        }


                }

            }

            private void session(String email) {
                Cursor c = dbHelper.fetchUser(email);
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
        });

        update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.showDialog("Please wait...");

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    int avatar=0;
                    if(avatar==0){



                        if(!checkCameraPermission()){
                            requestCameraPermission();
                            loadingDialog.hideDialog();
                        }else{
                            pickFromGallery();
                        }

                    }else if(avatar==1){


                        if(!checkStoragePermission()){
                            requestStoragePermission();
                            loadingDialog.hideDialog();
                        }else{
                            pickFromGallery();
                        }
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Permission Granted!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_REQUEST);
    }

    private boolean checkStoragePermission() {
        boolean result=ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void pickFromGallery() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA},CAMERA_REQUEST);

    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void displayInfo(String current_user_id) {
        Cursor edit = sqLiteDatabase.rawQuery("SELECT * FROM users WHERE id =?", new String[]{current_user_id});
        if(edit.getCount()>0){
            while(edit.moveToNext()){
                byte[] userImage = edit.getBlob(6);
                Bitmap bitmap = BitmapFactory.decodeByteArray(userImage, 0, userImage.length);
                profile.setImageBitmap(bitmap);

                edit_name.setText(edit.getString(1));
                edit_contact.setText(edit.getString(2));
                edit_email.setText(edit.getString(3));
                edit_password.setText(edit.getString(4));
                String sId = edit.getString(7);
                if(sId.equals("0000")) student_id.setText("Seller");
                else student_id.setText(sId);
            }
        }else Toast.makeText(getApplicationContext(), "Failed to fetch info", Toast.LENGTH_SHORT).show();
        edit.close();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        switch(requestCode){
            case CAMERA_REQUEST:{
                if(grantResults.length>0){
                    boolean camera_accepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean storage_accepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(camera_accepted && storage_accepted){
                        pickFromGallery();
                    }else{
                        Toast.makeText(this, "Enable Camera and Storage permission.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST:{
                if(grantResults.length>0){
                    boolean storage_accepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(storage_accepted){
                        pickFromGallery();
                    }else{
                        Toast.makeText(this, "Please Enable Storage Permission!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);


            if(resultCode==RESULT_OK){
                resultUri=result.getUri();
                Picasso.with(this).load(resultUri).into(profile);

            }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
                Toast.makeText(getApplicationContext(), (CharSequence) error, Toast.LENGTH_SHORT).show();
            }
            loadingDialog.hideDialog();
        }
    }



    private void getID() {
        close = findViewById(R.id.close);
        profile = findViewById(R.id.profile);
        update_profile = findViewById(R.id.profile_btn);
        done_edit = findViewById(R.id.done);
        edit_name = findViewById(R.id.edit_name);
        edit_contact = findViewById(R.id.edit_contact);
        edit_email = findViewById(R.id.edit_email);
        edit_password = findViewById(R.id.edit_password);
        student_id =findViewById(R.id.school_id);
    }



}