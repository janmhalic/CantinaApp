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
import android.provider.MediaStore;
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
import java.util.Currency;

public class fastfood extends AppCompatActivity {
    EditText fastName, fastAddress, fastEmail, fastContact, opening, shipping;
    ImageView fastPic;
    ImageButton back, upload;
    Button fastSubmit, fastUpdate;
    Uri resultUri;
    DBHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;
    SessionManager sessionManager;
    String sId;
    TextView nav_title, name_title, address_title, email_title, contact_title, delivery_title, opening_title;
    loadingDialog loadingDialog;

    public static final int CAMERA_REQUEST=100;
    public static final int STORAGE_REQUEST=101;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fastfood);
        findID();

        try{
            dbHelper = new DBHelper(this);
            sqLiteDatabase = dbHelper.getWritableDatabase();
            loadingDialog = new loadingDialog(this);

        }catch (Exception e){
            e.printStackTrace();
        }



        //initialize session manager
        sessionManager = new SessionManager(getApplicationContext());
        //get username from session
//        String email1 = sessionManager.getUsername();

        if(getIntent().getBundleExtra("store_data")!=null){
            Bundle bundle = getIntent().getBundleExtra("store_data");
            int storeId=bundle.getInt("id");
            String user = bundle.getString("store_user");
            sId = String.valueOf(storeId);
            fastUpdate.setVisibility(View.VISIBLE);
            fastSubmit.setVisibility(View.GONE);
            name_title.setVisibility(View.VISIBLE);
            address_title.setVisibility(View.VISIBLE);
            contact_title.setVisibility(View.VISIBLE);
            delivery_title.setVisibility(View.VISIBLE);
            opening_title.setVisibility(View.VISIBLE);
            email_title.setVisibility(View.VISIBLE);

            nav_title.setText("Update My Restaurant");
            try{
                displayStoreInfo(sId);

            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error. getting store info.", Toast.LENGTH_SHORT).show();
            }



        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(fastfood.this, account.class));
                finish();
            }
        });


        //image inserting
        upload.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                loadingDialog.showDialog("Please wait...");

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
            }
        });

        fastSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.showDialog("Please Wait...");
                Bitmap bitmap= ((BitmapDrawable)fastPic.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[]image=stream.toByteArray();
                String name = fastName.getText().toString().trim();
                String address = fastAddress.getText().toString().trim();
                String email = fastEmail.getText().toString().trim();
                String contact = fastContact.getText().toString().trim();
                String time = opening.getText().toString().trim();
                String fee = shipping.getText().toString().trim();
                String owner_id = sessionManager.getId().trim();

                ContentValues contentValues = new ContentValues();
                contentValues.put("res_name", name);
                contentValues.put("res_address", address);
                contentValues.put("res_contact", contact);
                contentValues.put("res_email", email);
                contentValues.put("res_addedBy", owner_id);
                contentValues.put("res_photo", image);
                contentValues.put("opening_times", time);
                contentValues.put("shipping", fee);

                if(name.equals("") || address.equals("") || email.equals("") || contact.equals("") || time.equals("") || fee.equals("") ){
                    Toast.makeText(getApplicationContext(), "Please Complete Details!", Toast.LENGTH_SHORT).show();
                    loadingDialog.hideDialog();
                }else{
                    long result = sqLiteDatabase.insert("restaurants",null,contentValues);
                    if(result==-1){
                        loadingDialog.hideDialog();
                        Toast.makeText(getApplicationContext(), "Something problem in inserting Data", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Restaurant Successfully added!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(fastfood.this, homepage.class));
                        finish();

                    }
                }



            }
        });

        fastUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.showDialog("Updating Restaurant...");
                Bitmap bitmap= ((BitmapDrawable)fastPic.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[]image=stream.toByteArray();
                String name = fastName.getText().toString().trim();
                String address = fastAddress.getText().toString().trim();
                String email = fastEmail.getText().toString().trim();
                String contact = fastContact.getText().toString().trim();
                String time = opening.getText().toString().trim();
                String fee = shipping.getText().toString().trim();
                String owner_id = sessionManager.getId().trim();

                ContentValues contentValues = new ContentValues();
                contentValues.put("res_name", name);
                contentValues.put("res_address", address);
                contentValues.put("res_contact", contact);
                contentValues.put("res_email", email);
                contentValues.put("res_addedBy", owner_id);
                contentValues.put("res_photo", image);
                contentValues.put("opening_times", time);
                contentValues.put("shipping", fee);

                if(name.equals("") || address.equals("") || email.equals("") || contact.equals("") || time.equals("") || fee.equals("") ){
                    Toast.makeText(getApplicationContext(), "Please Complete Details!", Toast.LENGTH_SHORT).show();
                    loadingDialog.hideDialog();
                }else{
                    long result = sqLiteDatabase.update("restaurants",contentValues, "res_id="+sId, null);
                    if(result==-1){
                        loadingDialog.hideDialog();
                        Toast.makeText(getApplicationContext(), "Something problem in updating Data", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Restaurant Updated!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(fastfood.this, homepage.class));
                        finish();

                    }

                }



            }
        });






    }

    private void displayStoreInfo(String sId) {
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM restaurants WHERE res_id = ?", new String[]{sId});
        if (c.getCount()>0){
            while(c.moveToNext()){
                fastName.setText(c.getString(1));
                fastAddress.setText(c.getString(2));
                fastContact.setText(c.getString(4));
                fastEmail.setText(c.getString(5));
                opening.setText(c.getString(6));
                shipping.setText(c.getString(8));
                byte[] store_image = c.getBlob(3);
                Bitmap bitmap = BitmapFactory.decodeByteArray(store_image, 0, store_image.length);
                fastPic.setImageBitmap(bitmap);
            }
            c.close();
        }else Toast.makeText(getApplicationContext(), "Failed to fetch restaurant data.", Toast.LENGTH_SHORT).show();
    }

    private void findID() {
        fastName = findViewById(R.id.fast_name);
        fastAddress = findViewById(R.id.fast_address);
        fastEmail = findViewById(R.id.fast_email);
        fastContact = findViewById(R.id.fast_contact);
        fastPic = findViewById(R.id.fast_pic);
        back = findViewById(R.id.fast_back);
        fastSubmit = findViewById(R.id.fast_submit);
        opening = findViewById(R.id.time);
        shipping = findViewById(R.id.fee);
        upload = findViewById(R.id.upload);
        fastUpdate = findViewById(R.id.fast_update);
        nav_title = findViewById(R.id.nav_title);
        name_title = findViewById(R.id.name_title);
        address_title = findViewById(R.id.address_title);
        contact_title = findViewById(R.id.contact_title);
        email_title = findViewById(R.id.email_title);
        delivery_title = findViewById(R.id.delivery_title);
        opening_title = findViewById(R.id.opening_title);


    }



    private void pickFromGallery() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
//                .setAspectRatio(1,1)
                .start(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_REQUEST);
    }

    private boolean checkStoragePermission() {
        boolean result= ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
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
                    if(camera_accepted ){
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
                Picasso.with(this).load(resultUri).into(fastPic);

            }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
                Toast.makeText(getApplicationContext(), (CharSequence) error, Toast.LENGTH_SHORT).show();
            }
            loadingDialog.hideDialog();
        }
    }

}