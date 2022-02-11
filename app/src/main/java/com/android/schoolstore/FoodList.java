//package com.android.schoolstore;
//
//import android.database.Cursor;
//import android.os.Bundle;
//import android.widget.GridView;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import java.util.ArrayList;
//
//public class FoodList extends AppCompatActivity{
//
//    GridView gridView;
//    ArrayList<food> list;
//    FoodAdapter adapter = null;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.food_list);
//
//        //Grid View
//        gridView = (GridView) findViewById(R.id.gridView);
//        list = new ArrayList<>();
//        adapter = new FoodAdapter(this, R.layout.food_item, list);
//        gridView.setAdapter(adapter);
//        //get al data from sqlite
//        Cursor cursor = homepage.dbHelper.getData("SELECT * FROM food");
//        list.clear();
//        while(cursor.moveToNext()){
//            int id = cursor.getInt(0);
//            String name = cursor.getString(1);
//            String des = cursor.getString(2);
//            int price = cursor.getInt(3);
//            int qty = cursor.getInt(4);
//            byte[] foodImage = cursor.getBlob(5);
//
//            list.add(new food(id,name,des,price,qty,foodImage));
//
//        }
//        adapter.notifyDataSetChanged();
//    }
//}
