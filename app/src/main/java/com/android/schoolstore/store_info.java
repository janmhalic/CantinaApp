package com.android.schoolstore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class store_info extends AppCompatActivity {
    ImageButton back;
    ImageView store_image;
    TextView store_name, store_address, store_opening, star_total, people;
    LinearLayout empty_reviews;
    int store_id = 0;
    DBHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;
    RecyclerView recyclerView;
    starsAdapter starsAdapter;
    SessionManager sessionManager;
    Button edit_info;
    String current_user_ID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_info);
        findID();
        try {
            dbHelper = new DBHelper(this);
            sqLiteDatabase = dbHelper.getWritableDatabase();
            sessionManager = new SessionManager(getApplicationContext());
            current_user_ID = sessionManager.getId();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error. database.", Toast.LENGTH_SHORT).show();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));

        if(getIntent().getBundleExtra("store_data")!=null) {
            Bundle bundle = getIntent().getBundleExtra("store_data");
            store_id = bundle.getInt("id");
            store_name.setText(bundle.getString("store_name"));

            String sId = String.valueOf(store_id);
            Cursor get = sqLiteDatabase.rawQuery("SELECT * FROM restaurants WHERE res_id =? and res_addedBy =?", new String[]{sId, current_user_ID});
            if(get.getCount()>0)edit_info.setVisibility(View.VISIBLE);
            else edit_info.setVisibility(View.GONE);
            get.close();

            try {
                displayStore(String.valueOf(store_id));
                displayReviews(String.valueOf(store_id));


            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error. getting store info.", Toast.LENGTH_SHORT).show();
            }
        }

        edit_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("id", store_id);
                bundle.putString("store_user", current_user_ID);
                Intent intent=new Intent(getApplicationContext(),fastfood.class);
                intent.putExtra("store_data",bundle);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




    }

    private void displayReviews(String valueOf) {
        Cursor review = sqLiteDatabase.rawQuery("SELECT * FROM ratings WHERE rate_store_id = ?", new  String[]{valueOf});
        ArrayList<starsModel> starsModels = new ArrayList<>();
        int count = review.getCount();
        people.setText(count+" People rated");



        if (count >  0){
            recyclerView.setVisibility(View.VISIBLE);
            empty_reviews.setVisibility(View.GONE);
            displayTotalStars(valueOf);
            while(review.moveToNext()){
                String name = review.getString(3);
                String context = review.getString(4);
                String date = review.getString(6);
                float star = review.getFloat(5);

                starsModels.add(new starsModel(name,context,date,star));


            }
            starsAdapter = new starsAdapter(this,R.layout.single_reviews,starsModels,sqLiteDatabase);
            recyclerView.setAdapter(starsAdapter);
            review.close();

        }else {
            recyclerView.setVisibility(View.GONE);
            empty_reviews.setVisibility(View.VISIBLE);
        }
    }

    private void displayTotalStars(String valueOf) {
        double total = dbHelper.getStarTotal(valueOf);
        star_total.setText(String.valueOf(total));
    }

    private void displayStore(String store) {
        Cursor fetch_store = sqLiteDatabase.rawQuery("SELECT * FROM restaurants WHERE res_id=?",new String[]{store});
        if(fetch_store.getCount()>0){
            while(fetch_store.moveToNext()){
                String Address = fetch_store.getString(2);
                byte[] image = fetch_store.getBlob(3);
                String opening = fetch_store.getString(6);

                store_address.setText(Address);
                store_opening.setText(opening);
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                store_image.setImageBitmap(bitmap);

            }
            fetch_store.close();
        }else Toast.makeText(getApplicationContext(), "Error: Selecting Store Data. Try restart.", Toast.LENGTH_SHORT).show();
    }

    private void findID() {
        back = findViewById(R.id.back);
        store_name = findViewById(R.id.store_name_info);
        store_address = findViewById(R.id.store_info_location);
        store_opening = findViewById(R.id.store_time_info);
        store_image = findViewById(R.id.store_info_image);
        recyclerView = findViewById(R.id.rv_reviews);
        star_total = findViewById(R.id.star_total);
        people = findViewById(R.id.store_people_rated);
        edit_info = findViewById(R.id.edit_info);
        empty_reviews = findViewById(R.id.review_empty);
    }
}