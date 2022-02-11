package com.android.schoolstore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    Context context;
    int singleData;
    ArrayList<CartModel>modelArrayList;
    SQLiteDatabase sqLiteDatabase;

    //generate Constructor

    public CartAdapter(Context context, int singleData, ArrayList<CartModel> modelArrayList, SQLiteDatabase sqLiteDatabase) {
        this.context = context;
        this.singleData = singleData;
        this.modelArrayList = modelArrayList;
        this.sqLiteDatabase = sqLiteDatabase;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.cartsingledata,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
    final CartModel model = modelArrayList.get(position);
    byte[]image=model.getFood_image();
    Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
    holder.image_cart.setImageBitmap(bitmap);
    holder.cartName.setText(model.getFood_name());
    holder.cartDes.setText(model.getFood_description());
    holder.cartQty.setText(model.getFood_quantity());
    holder.cartPrice.setText(model.getFood_price());
    String sId = model.getStore_id();
    String cId = model.getCustomer_id();

    holder.cartName.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putInt("cart_id", model.getCart_id());
            bundle.putString("item_id", model.getFood_id());
            bundle.putString("store_id", model.getStore_id());
            Intent intent=new Intent(context,view_item_cart.class);
            intent.putExtra("item_data",bundle);
            context.startActivity(intent);
        }
    });

    //delete
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper dbHelper = new DBHelper(context);
                sqLiteDatabase=dbHelper.getReadableDatabase();
                long cart_delete = sqLiteDatabase.delete("my_cart", "cart_id="+model.getCart_id(),null);
                if(cart_delete != -1) {
                    Toast.makeText(context, "Item Remove from Cart", Toast.LENGTH_SHORT).show();
                    //remove position after deleted
                    modelArrayList.remove(position);
                    //update data
                    notifyDataSetChanged();
                    Intent intent=new Intent(context,mycart.class);
                    context.startActivity(intent);
                    Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM my_cart WHERE customer_id =? and store_id=?",new String[]{cId,sId});
                    if (c.getCount() == 0){
                        Intent i=new Intent(context,homepage.class);
                        context.startActivity(i);
                    }
                }


            }
        });


    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image_cart;
        TextView cartName, cartDes, cartPrice, cartQty;
        ImageButton delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_cart=(ImageView)itemView.findViewById(R.id.cartImage);
            cartName=(TextView)itemView.findViewById(R.id.cart_productName);
            cartDes=(TextView)itemView.findViewById(R.id.cart_productDes);
            cartQty=(TextView)itemView.findViewById(R.id.cart_quantity);
            cartPrice=(TextView)itemView.findViewById(R.id.cart_price);
            delete=(ImageButton)itemView.findViewById(R.id.cart_delete);
        }
    }
}
