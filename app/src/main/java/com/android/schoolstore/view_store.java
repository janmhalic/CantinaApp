package com.android.schoolstore;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class view_store extends AppCompatActivity {
    ImageButton back;
    ImageView store_image,sheet_image;
    TextView store_name,store_id, sheet_name, sheet_des, sheet_price, sheet_value, sheet_total, sheet_stocks, sheet_foodId,sheet_storeId,more, total_cart,count_cart, rateNum;
    String owner_id;
    LinearLayout managerPanel;
    RelativeLayout check_cart_panel;
    DBHelper dbHelper;
    Button add_Menu, customers_btn;
    RecyclerView recyclerView;
    Dialog dialog;
    FoodAdapter foodAdapter;
    SQLiteDatabase sqLiteDatabase;
    CardView empty;
    int id = 0, stocks = 0,count = 0, getQty = 0, cart_id;
    double total = 0, price = 0, getTotal = 0;
    String store_owner = "", owner_in_session = "";
    SessionManager sessionManager;
    boolean storeCheck = false;
    private BottomSheetDialog bottomSheetDialog;
    private int position;
    RatingBar ratings;
    loadingDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_store);
        getID();


        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        //initialize session manager


        try{
            dbHelper = new DBHelper(this);
            sqLiteDatabase = dbHelper.getWritableDatabase();
            sessionManager = new SessionManager(getApplicationContext());
            dialog = new Dialog(view_store.this);
            owner_in_session = sessionManager.getId();
            if(owner_in_session.equals(""))errorDialog();
            storeCart();
            loadingDialog = new loadingDialog(this);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error: Database. Try Reload", Toast.LENGTH_SHORT).show();
        }



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadingDialog.showDialog("Loading...");
                finish();
            }
        });


        if(getIntent().getBundleExtra("store_data")!=null){
            Bundle bundle = getIntent().getBundleExtra("store_data");
            id=bundle.getInt("id");

            owner_id = String.valueOf(id);
            store_id.setText(owner_id);
            store_owner = bundle.getString("store_owner");
            String storeId = String.valueOf(id);

            //check owner if allowed to add menu
            if(store_owner.equals("") || owner_in_session.equals("")){
                managerPanel.setVisibility(View.GONE);
            }else if (store_owner.equals(owner_in_session)){
                managerPanel.setVisibility(View.VISIBLE);
            }else{
                managerPanel.setVisibility(View.GONE);
            }

            try{
                displayFoods();
                displayRatings(owner_id);
                displayStore(storeId);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error. getting store info.", Toast.LENGTH_SHORT).show();
            }



        }



        add_Menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.showDialog("Please wait...");

                //pass store data to add menu
                Bundle b = new Bundle();
                b.putString("store_id", String.valueOf(id));
                b.putString("store_name", store_name.getText().toString());


                Intent intent=new Intent(view_store.this,makeOrder.class);
                intent.putExtra("store_data",b);
                startActivity(intent);
                finish();


            }
        });

        customers_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.showDialog("PLease wait...");
                //pass store data to view customers

                Bundle b = new Bundle();
                b.putString("store_id", String.valueOf(id));
                b.putString("store_name", store_name.getText().toString());

                Intent intent=new Intent(view_store.this,my_customers.class);
                intent.putExtra("store_data",b);
                startActivity(intent);
                loadingDialog.hideDialog();

            }
        });

        //more info
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.showDialog("Please wait...");
                Bundle bundle = new Bundle();
                bundle.putInt("id", id);
                bundle.putString("store_name", store_name.getText().toString());
                Intent intent=new Intent(view_store.this ,store_info.class);
                intent.putExtra("store_data",bundle);
                startActivity(intent);
                loadingDialog.hideDialog();
            }
        });

        check_cart_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(view_store.this, mycart.class));
            }
        });



    }

    private void errorDialog() {
        dialog.setContentView(R.layout.error_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        Button go = dialog.findViewById(R.id.go);

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set login false
                sessionManager.setLogin(false);
                //ser username empty
                sessionManager.editor.clear();
                sessionManager.editor.commit();
                //redirect to activity
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                // finish activity
                finish();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void displayRatings(String owner_id) {
        Cursor rated = sqLiteDatabase.rawQuery("SELECT * FROM ratings WHERE rate_store_id =?", new String[]{owner_id});
        int counted = rated.getCount();
        rateNum.setText(counted+" ratings");
        float total = dbHelper.getStarTotal(owner_id);
        ratings.setRating(total);

    }

    private void displayStore(String storeId) {
        Cursor store = sqLiteDatabase.rawQuery("SELECT * FROM restaurants WHERE res_id = ?", new String[]{storeId});
        if(store.getCount()>0){
            while(store.moveToNext()){
                byte[] image = store.getBlob(3);
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                store_image.setImageBitmap(bitmap);
                store_name.setText(store.getString(1));
            }
            store.close();
        }else Toast.makeText(getApplicationContext(), "Store Info Failed to Fetch.", Toast.LENGTH_SHORT).show();

    }

    private void storeCart() {
        String cId = owner_in_session;
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM my_cart WHERE customer_id=?", new String[]{cId});
        if(c.getCount() >= 1){

            check_cart_panel.setVisibility(View.VISIBLE);
            storeCheck = true;
            displayCartCount();
        }
        else {
            check_cart_panel.setVisibility(View.GONE);
            storeCheck = false;}
        c.close();
    }

    private void displayCartCount() {
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM my_cart WHERE customer_id=?",new String[]{owner_in_session});
        count_cart.setText(String.valueOf(cursor.getCount()));
        total_cart.setText("₱"+dbHelper.getSubtotal(owner_in_session));
        cursor.close();

    }


    private void displayFoods() {
//        sqLiteDatabase = dbHelper.getWritableDatabase();
        Cursor cursor = dbHelper.getFoods("select * from food where own_id like '%"+owner_id+"%'");
        ArrayList<food> foods = new ArrayList<>();
        int number = cursor.getCount();
        Toast.makeText(getApplicationContext(), number+" Total Menu", Toast.LENGTH_SHORT).show();
        if(cursor.getCount()>0){
            empty.setVisibility(View.GONE);
                while(cursor.moveToNext()){
                int id = cursor.getInt(0);
                String name = cursor.getString(2);
                String des = cursor.getString(3);
                String price = (cursor.getString(4));
                String qty = cursor.getString(5);
                String by = cursor.getString(6);
                String by_id = cursor.getString(7);
                String own = cursor.getString(8);
                String ownID = cursor.getString(9);
                byte[] foodImage = cursor.getBlob(1);

                foods.add(new food(id,name,des,price,qty,foodImage,by,by_id,own,ownID));
            }

        }else{
            empty.setVisibility(View.VISIBLE);
        }
        foodAdapter = new FoodAdapter(this,R.layout.food_item,foods,sqLiteDatabase, sessionManager);
        foodAdapter.onLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                position = recyclerView.getChildAdapterPosition(v);

                bottomSheetDialog = new BottomSheetDialog(view_store.this, R.style.BottomSheetDialogTheme);
                View view = getLayoutInflater().from(view_store.this).inflate(R.layout.layout_bottom_sheet,null);
                sheet_name = view.findViewById(R.id.sheet_name);
                sheet_des = view.findViewById(R.id.sheet_des);
                sheet_price = view.findViewById(R.id.sheet_price);
                sheet_image = view.findViewById(R.id.sheet_image);
                sheet_foodId = view.findViewById(R.id.sheet_foodId);
                sheet_storeId = view.findViewById(R.id.sheet_storeId);
//                add = view.findViewById(R.id.increment);
//                minus = view.findViewById(R.id.decrement);
                sheet_value = view.findViewById(R.id.value);
                sheet_total = view.findViewById(R.id.sheet_total);
                sheet_stocks = view.findViewById(R.id.sheet_stocks);
                sheet_price.setText(String.valueOf(price));
                sheet_stocks.setText(String.valueOf(stocks));





                if(position != -1)
                {
                    byte[] food_image = foods.get(position).getImage();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(food_image, 0, food_image.length);
                    sheet_image.setImageBitmap(bitmap);
                    sheet_name.setText(foods.get(position).getName());
                    sheet_des.setText(foods.get(position).getDescription());

                    double food_price = Double.parseDouble(foods.get(position).getPrice());
                    price = food_price;
                    count = 0;
                    sheet_price.setText(String.valueOf(food_price));
                    sheet_stocks.setText(String.valueOf(stocks));
                    String fId = String.valueOf(foods.get(position).getId());
                    Cursor qty = sqLiteDatabase.rawQuery("SELECT * FROM food WHERE id = ?", new String[]{fId});
                    while(qty.moveToNext()){
                        stocks = Integer.parseInt(qty.getString(5));
                        sheet_stocks.setText(String.valueOf(stocks));

                    }
                    qty.close();

                    sheet_foodId.setText(fId);
                    sheet_storeId.setText(foods.get(position).getOwner_id());
                    storeCart();


                    Cursor button_cursor = sqLiteDatabase.rawQuery("SELECT * FROM my_cart WHERE food_id=? and customer_id=?",new String[]{fId,owner_in_session});
                    if(button_cursor.getCount()>0){
                        view.findViewById(R.id.sheet_submit).setVisibility(View.GONE);
                        view.findViewById(R.id.sheet_update).setVisibility(View.VISIBLE);

                        while(button_cursor.moveToNext()){
                            getTotal = button_cursor.getDouble(9);
                            getQty = Integer.parseInt(button_cursor.getString(8));
                            cart_id = button_cursor.getInt(0);
                        }
                        button_cursor.close();
                    }else{
                        view.findViewById(R.id.sheet_submit).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.sheet_update).setVisibility(View.GONE);


                    }







                }



                view.findViewById(R.id.increment).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(stocks >= 1){
                            count++;
                            stocks--;
                            total = count*price;
                        }
                        else if(stocks == 0){
                            Toast.makeText(getApplicationContext(), "Out of Stocks", Toast.LENGTH_SHORT).show();
                        }

                        sheet_value.setText(String.valueOf(count));
                        sheet_stocks.setText(String.valueOf(stocks));
                        sheet_total.setText(String.valueOf(total));
                    }
                });

                view.findViewById(R.id.decrement).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(count >=1){
                            count --;
                            stocks ++;
                            total = count*price;
                        }
                        sheet_value.setText(String.valueOf(count));
                        sheet_stocks.setText(String.valueOf(stocks));
                        sheet_total.setText(String.valueOf(total));
                    }
                });



                view.findViewById(R.id.sheet_submit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            loadingDialog.showDialog("Loading...");

                            String food_id = sheet_foodId.getText().toString();
                            String customer_id = owner_in_session;
                            String storeId = sheet_storeId.getText().toString();
                            Bitmap bitmap = ((BitmapDrawable) sheet_image.getDrawable()).getBitmap();
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] foodImage = stream.toByteArray();
                            String foodName = sheet_name.getText().toString();
                            String foodDescription = sheet_des.getText().toString();
                            String foodPrice = sheet_price.getText().toString();
                            String foodQuantity = sheet_value.getText().toString();
                            Double foodTotal = Double.parseDouble(sheet_total.getText().toString());

                        ContentValues contentValues = new ContentValues();
                        contentValues.put("food_id", food_id);
                        contentValues.put("customer_id", customer_id);
                        contentValues.put("store_id", storeId);
                        contentValues.put("Picture", foodImage);
                        contentValues.put("food_name", foodName);
                        contentValues.put("food_descriptions", foodDescription);
                        contentValues.put("Price", foodPrice);
                        contentValues.put("Quantity", foodQuantity);
                        contentValues.put("Total", foodTotal);

                        if(count > 0) {



                            if(storeCheck){
                                String search = "";
                                Cursor cart = sqLiteDatabase.rawQuery("SELECT * FROM my_cart WHERE store_id=? and customer_id=?",new String[]{storeId,customer_id});
                                while(cart.moveToNext()){
                                    search = cart.getString(3);
                                }
                                if (search.equals(storeId)){

                                    long result = sqLiteDatabase.insert("my_cart", null, contentValues);
                                    if (result == -1) {
                                        loadingDialog.hideDialog();
                                        Toast.makeText(getApplicationContext(), "Something problem in Adding to Cart", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Add to Cart Successful", Toast.LENGTH_SHORT).show();
                                        storeCart();
                                        bottomSheetDialog.dismiss();
                                        updateItemStocks(Integer.parseInt(food_id));
                                        loadingDialog.hideDialog();

                                    }
                                    cart.close();

                                }else {
                                    openDialog(customer_id);

                                }





                            }else {
                                long result = sqLiteDatabase.insert("my_cart", null, contentValues);
                                if (result == -1) {
                                    loadingDialog.hideDialog();
                                    Toast.makeText(getApplicationContext(), "Something problem in Adding to Cart", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Add to Cart Successful", Toast.LENGTH_SHORT).show();
                                    storeCart();
                                    updateItemStocks(Integer.parseInt(food_id));
                                    bottomSheetDialog.dismiss();
                                    loadingDialog.hideDialog();

                                }

                            }


                        }else{
                            loadingDialog.hideDialog();
                            Toast.makeText(getApplicationContext(), "Indicate Quantity!", Toast.LENGTH_SHORT).show();
                        }




                    }




                });


                view.findViewById(R.id.sheet_update).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadingDialog.showDialog("Please wait...");
                        String food_id = sheet_foodId.getText().toString();
                        int foodQuantity = Integer.parseInt(sheet_value.getText().toString());
                        double foodTotal = Double.parseDouble(sheet_total.getText().toString());

                        double updateTotal = foodTotal + getTotal;
                        int updateQty = foodQuantity + getQty;


                        ContentValues contentValues = new ContentValues();
                        contentValues.put("Quantity", String.valueOf(updateQty));
                        contentValues.put("Total", updateTotal);

                        if(count > 0) {
                            long result = sqLiteDatabase.update("my_cart", contentValues,"cart_id="+cart_id,null);
                            if (result == -1) {
                                loadingDialog.hideDialog();
                                Toast.makeText(getApplicationContext(), "Something problem in Updating Cart", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Cart Updated", Toast.LENGTH_SHORT).show();
                                bottomSheetDialog.dismiss();
                                storeCart();
                                updateItemStocks(Integer.parseInt(food_id));
                            }

                        }else{
                            loadingDialog.hideDialog();
                            Toast.makeText(getApplicationContext(), "Indicate Quantity!", Toast.LENGTH_SHORT).show();
                        }


                    }
                });


                bottomSheetDialog.setContentView(view);
                bottomSheetDialog.show();
                return true;
            }


        });
        recyclerView.setAdapter(foodAdapter);
        cursor.close();
    }

    private void updateItemStocks(int itemId) {
        String getStocks = String.valueOf(stocks);

        ContentValues contentValues = new ContentValues();
        contentValues.put("quantity", getStocks);
        sqLiteDatabase.update("food",contentValues,"id="+itemId,null);

    }

    private void openDialog(String customer) {
        dialog.setContentView(R.layout.remove_existing_store_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        Button remove = dialog.findViewById(R.id.remove_btn);
        Button no = dialog.findViewById(R.id.no_btn);
        ImageButton close = dialog.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long delete = sqLiteDatabase.delete("my_cart","customer_id = "+customer,null);
                if(delete != -1){

                    String food_id = sheet_foodId.getText().toString();
                    String customer_id = owner_in_session;
                    String storeId = sheet_storeId.getText().toString();
                    Bitmap bitmap = ((BitmapDrawable) sheet_image.getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] foodImage = stream.toByteArray();
                    String foodName = sheet_name.getText().toString();
                    String foodDescription = sheet_des.getText().toString();
                    String foodPrice = sheet_price.getText().toString();
                    String foodQuantity = sheet_value.getText().toString();
                    Double foodTotal = Double.parseDouble(sheet_total.getText().toString());

                    ContentValues contentValues = new ContentValues();
                    contentValues.put("food_id", food_id);
                    contentValues.put("customer_id", customer_id);
                    contentValues.put("store_id", storeId);
                    contentValues.put("Picture", foodImage);
                    contentValues.put("food_name", foodName);
                    contentValues.put("food_descriptions", foodDescription);
                    contentValues.put("Price", foodPrice);
                    contentValues.put("Quantity", foodQuantity);
                    contentValues.put("Total", foodTotal);

                    long result = sqLiteDatabase.insert("my_cart", null, contentValues);
                    if (result == -1) {
                        Toast.makeText(getApplicationContext(), "Something problem in Adding to Cart", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Add to Cart Successful", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                        updateItemStocks(Integer.parseInt(food_id));
                        storeCart();
                    }



                }else{
                    Toast.makeText(getApplicationContext(), "Failed to Remove Cart.", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();

            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }


    private void getID() {

        back= findViewById(R.id.store_back);
        store_image = findViewById(R.id.store_image);
        store_name = findViewById(R.id.store_name);
        store_id = findViewById(R.id.store_id);
        empty = findViewById(R.id.empty);
        add_Menu = findViewById(R.id.add_menu);
        recyclerView = findViewById(R.id.store_rv);
        managerPanel = findViewById(R.id.manager_panel);
        more = findViewById(R.id.more);
        customers_btn = findViewById(R.id.customers_btn);
        check_cart_panel = findViewById(R.id.check_cart_panel);
        total_cart = findViewById(R.id.cart_total);
        count_cart = findViewById(R.id.cart_count);
        rateNum = findViewById(R.id.rateNum);
        ratings = findViewById(R.id.ratings);
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        bottomSheetDialog.dismiss();
//    }
}