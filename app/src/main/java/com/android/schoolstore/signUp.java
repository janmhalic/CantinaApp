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
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

public class signUp extends AppCompatActivity {
    TextView seller, student, toLogin;
    EditText access,fullName, contact, email, password, code, reType, student_id;
    Button signUp, send_sms_btn;
    ImageView profile;
    ImageButton choose_profile;
    Uri resultUri;
    DBHelper dbHelper;
    RelativeLayout code_panel;
    SQLiteDatabase sqLiteDatabase;
    loadingDialog loadingDialog;

    public static final int CAMERA_REQUEST = 100;
    public static final int STORAGE_REQUEST = 101;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        findID();
        try{
            dbHelper = new DBHelper(this);
            sqLiteDatabase = dbHelper.getWritableDatabase();
            loadingDialog = new loadingDialog(this);

        }catch (Exception e){
            e.printStackTrace();
        }
        //When Seller Click
        seller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                access.setText("Seller");
                student.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                seller.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.red_500));
                seller.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.user_access));
                student.setBackgroundResource(0);
                code_panel.setVisibility(View.VISIBLE);
                code.setText("");
                student_id.setVisibility(View.GONE);
                student_id.setText("0000");

            }
        });
        //When Student click
        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                access.setText("Student");
                seller.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
                student.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.red_500));
                student.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.user_access));
                seller.setBackgroundResource(0);
                code_panel.setVisibility(View.GONE);
                code.setText("R4574M4N");
                student_id.setVisibility(View.VISIBLE);
                student_id.setText("");
            }
        });
        //back to login
        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signUp.this, MainActivity.class));
                finish();
            }
        });

        //image inserting
        choose_profile.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
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
                    loadingDialog.hideDialog();
                    Toast.makeText(getApplicationContext(), "Permission Granted!", Toast.LENGTH_SHORT).show();
                }


            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap=((BitmapDrawable)profile.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[]image=stream.toByteArray();
                String name = fullName.getText().toString().trim();
                String email1 = email.getText().toString().trim();
                String pass = password.getText().toString().trim();
                String access1 = access.getText().toString().trim();
                String ver = code.getText().toString().trim();
                String contact1 = contact.getText().toString().trim();
                String rePass = reType.getText().toString().trim();
                String student = student_id.getText().toString().trim();
                String pin = "R4574M4N";
                loadingDialog.showDialog("Signing in...");


                ContentValues contentValues = new ContentValues();
                contentValues.put("fullName", name);
                contentValues.put("email", email1);
                contentValues.put("password", pass);
                contentValues.put("access", access1);
                contentValues.put("contact", contact1);
                contentValues.put("profile", image);
                contentValues.put("student_id", student);

                if(name.equals("") || email1.equals("") || pass.equals("") || contact1.equals("") || rePass.equals("") || student.equals("")){
                    Toast.makeText(getApplicationContext(), "Please Complete Details", Toast.LENGTH_SHORT).show();
                    loadingDialog.hideDialog();
                }

                else{
                    if(access1.equals("")) Toast.makeText(getApplicationContext(), "Select Access", Toast.LENGTH_SHORT).show();
                    else{
                        if(ver.equals(pin)){
                            if (rePass.equals(pass)){
                                boolean check_Email = dbHelper.check_email(email1);
                                if(!check_Email){
                                    long result = sqLiteDatabase.insert("users",null,contentValues);
                                    if(result==-1){
                                        Toast.makeText(getApplicationContext(), "Something problem in Inserting Data", Toast.LENGTH_SHORT).show();
                                        loadingDialog.hideDialog();
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(), "Register Successful!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(signUp.this, MainActivity.class));

                                        finish();
                                    }
                                }else {
                                    Toast.makeText(getApplicationContext(), "Email Already Exist!", Toast.LENGTH_SHORT).show();
                                    loadingDialog.hideDialog();
                                }

                            }else {
                                loadingDialog.hideDialog();
                                Toast.makeText(getApplicationContext(), "Password not match!", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            loadingDialog.hideDialog();
                            Toast.makeText(getApplicationContext(), "Invalid Verification Code", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        send_sms_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                        sendSMS();
                    }else{
                        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1 );
                    }
                }

            }
        });




    }

    private void sendSMS() {
        String phoneNum = contact.getText().toString().trim();
        String SMS = "Cantina: Your Verification code is: R4574M4N. Please don't reply.";
        if(phoneNum.equals(""))Toast.makeText(this, "Insert your Contact Number!", Toast.LENGTH_SHORT).show();
        else {

            try{
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNum, null,SMS,null,null);
                Toast.makeText(this, "Message Sent. Please Wait.", Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, "Failed to send message! Try Again.", Toast.LENGTH_SHORT).show();
            }
        }




    }


    private void findID() {
        seller=findViewById(R.id.seller);
        student=findViewById(R.id.student);
        toLogin=findViewById(R.id.toLogin);
        access = findViewById(R.id.access);
        fullName = findViewById(R.id.fullName);
        contact = findViewById(R.id.contact);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        code = findViewById(R.id.code);
        signUp = findViewById(R.id.signUpBtn);
        profile = findViewById(R.id.userProfile);
        choose_profile = findViewById(R.id.choose_profile);
        reType = findViewById(R.id.repeat);
        code_panel = findViewById(R.id.code_panel);
        send_sms_btn = findViewById(R.id.send_sms_btn);
        student_id = findViewById(R.id.student_id);
    }


    private void pickFromGallery() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_REQUEST);
    }

    private boolean checkStoragePermission() {
        boolean result=ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
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
}