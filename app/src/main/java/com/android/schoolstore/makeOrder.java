package com.android.schoolstore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.text.Editable;
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

public class makeOrder extends AppCompatActivity {
    ImageView foodImage;
    EditText name,price,quantity, description;
    TextView owner,owner_id, title;
    Button submit,edit;
    ImageButton back, upload;
    DBHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;
    SessionManager sessionManager;
    Uri resultUri;
    String Store_owner, store_id, user_id;
    int id = 0;
    loadingDialog loadingDialog;



    public static final int CAMERA_REQUEST=100;
    public static final int STORAGE_REQUEST=101;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_order);
        findID();
        editData();
        try{
            dbHelper = new DBHelper(this);
            sqLiteDatabase = dbHelper.getWritableDatabase();
            //initialize session manager
            sessionManager = new SessionManager(getApplicationContext());
            user_id = sessionManager.getId();
            loadingDialog = new loadingDialog(this);

        }catch (Exception e){
            e.printStackTrace();
        }

        // get store data
        if(getIntent().getBundleExtra("store_data")!=null){
            Bundle bundle = getIntent().getBundleExtra("store_data");
            store_id = bundle.getString("store_id");
            //for set name
            Store_owner = bundle.getString("store_name");



        }

        owner.setText(Store_owner);
        owner_id.setText(store_id);

        upload.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
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

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.showDialog("Adding Menu...");
                Bitmap bitmap=((BitmapDrawable)foodImage.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] image = stream.toByteArray();
                String name1 = name.getText().toString();
                String price1 = price.getText().toString();
                String qty = quantity.getText().toString();
                String des = description.getText().toString();
                String addedBy = sessionManager.getUsername();
                String own = Store_owner;
                String own_id = store_id;


                ContentValues contentValues = new ContentValues();
                contentValues.put("name", name1);
                contentValues.put("price", price1);
                contentValues.put("quantity", qty);
                contentValues.put("description", des);
                contentValues.put("added_by", addedBy);
                contentValues.put("own_by", own);
                contentValues.put("user_id", user_id);
                contentValues.put("picture", image);
                contentValues.put("own_id", own_id);

                if(qty.equals("") || price1.equals("") || name1.equals("") || des.equals("")){
                    Toast.makeText(getApplicationContext(), "Please Complete Details!", Toast.LENGTH_SHORT).show();
                    loadingDialog.hideDialog();
                }else{
                    long result = sqLiteDatabase.insert("food",null,contentValues);
                    if(result==-1){
                        Toast.makeText(getApplicationContext(), "Something problem in Inserting Menu", Toast.LENGTH_SHORT).show();
                        loadingDialog.hideDialog();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Menu is Ready", Toast.LENGTH_SHORT).show();
                        Bundle bundle = new Bundle();
                        bundle.putInt("id", Integer.parseInt(store_id));
                        bundle.putString("store_owner", user_id);
                        Intent intent=new Intent(getApplicationContext(),view_store.class);
                        intent.putExtra("store_data",bundle);
                        startActivity(intent);
                        finish();

                    }
                }


            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.showDialog("Updating Menu...");
                Bitmap bitmap=((BitmapDrawable)foodImage.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] image = stream.toByteArray();
                String name1 = name.getText().toString().trim();
                String price1 = price.getText().toString().trim();
                String qty = quantity.getText().toString().trim();
                String des = description.getText().toString().trim();
                String own = owner.getText().toString().trim();
                String own_id = owner_id.getText().toString().trim();


                ContentValues contentValues = new ContentValues();
                contentValues.put("name", name1);
                contentValues.put("price", price1);
                contentValues.put("quantity", qty);
                contentValues.put("description", des);
                contentValues.put("picture", image);
                contentValues.put("own_by", own);
                contentValues.put("own_id", own_id);


                long result = sqLiteDatabase.update("food",contentValues,"id="+id,null);
                if(result==-1){
                    Toast.makeText(getApplicationContext(), "Menu Doesn't Update", Toast.LENGTH_SHORT).show();
                    loadingDialog.hideDialog();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Menu is Updated", Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", Integer.parseInt(store_id));
                    bundle.putString("store_owner", user_id);
                    Intent intent=new Intent(getApplicationContext(),view_store.class);
                    intent.putExtra("store_data",bundle);
                    startActivity(intent);
                    finish();
                }


            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putInt("id", Integer.parseInt(store_id));
                bundle.putString("store_owner", user_id);
                Intent intent=new Intent(getApplicationContext(),view_store.class);
                intent.putExtra("store_data",bundle);
                startActivity(intent);
                finish();
            }
        });



    }

    private void editData() {
        if (getIntent().getBundleExtra("order_data")!=null){
            Bundle bundle = getIntent().getBundleExtra("order_data");
            id=bundle.getInt("id");
            //for image
            byte[]bytes = bundle.getByteArray("image");
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            foodImage.setImageBitmap(bitmap);
            //for set name
            name.setText(bundle.getString("name"));
            description.setText(bundle.getString("description"));
            price.setText(bundle.getString("price"));
            quantity.setText(bundle.getString("qty"));
            //visible edit button
            submit.setVisibility(View.GONE);
            edit.setVisibility(View.VISIBLE);
            title.setText("Update your menu from you list");
            store_id = bundle.getString("storeId");
            //for set name
            Store_owner = bundle.getString("StoreName");

        }
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

    private void pickFromGallery() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(this);
    }

    private void findID() {
        foodImage = findViewById(R.id.foodImage);
        name = findViewById(R.id.pro_name);
        price = findViewById(R.id.price);
        quantity = findViewById(R.id.quantity);
        submit = findViewById(R.id.submit_btn);
        description = findViewById(R.id.description);
        edit = findViewById(R.id.edit_btn);
        back = findViewById(R.id.back);
        owner = findViewById(R.id.store_name);
        owner_id = findViewById(R.id.store_id);
        upload = findViewById(R.id.upload);
        title = findViewById(R.id.title);
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
                Picasso.with(this).load(resultUri).into(foodImage);

            }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
            loadingDialog.hideDialog();
        }
    }





}