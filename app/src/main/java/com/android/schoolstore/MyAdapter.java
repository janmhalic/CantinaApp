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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    Context context;
    int singleData;
    ArrayList<Model>modelArrayList;
    SQLiteDatabase sqLiteDatabase;
    DBHelper dbHelper;
    //constructor


    public MyAdapter(Context context, int singleData, ArrayList<Model> modelArrayList, SQLiteDatabase sqLiteDatabase,DBHelper dbHelper) {
        this.context = context;
        this.singleData = singleData;
        this.modelArrayList = modelArrayList;
        this.sqLiteDatabase = sqLiteDatabase;
        this.dbHelper = dbHelper;

    }

    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.single_store,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final Model model = modelArrayList.get(position);
        byte[]image = model.getStorePicture();
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0,image.length);
        holder.store_image.setImageBitmap(bitmap);
        holder.store_name.setText(model.getStoreName());
        holder.store_address.setText(model.getStoreAddress());
        holder.store_email.setText(model.getStoreEmail());
        holder.store_time.setText(model.getStoreOpening());
        holder.store_by.setText(model.getStoreAdd());
        holder.store_fee.setText(model.getShipping());
        holder.store_contact.setText(model.getStoreContact());

        holder.store_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", model.getStoreId());
                    bundle.putString("store_owner", model.getStoreAdd());
                    Intent intent=new Intent(context,view_store.class);
                    intent.putExtra("store_data",bundle);
                    context.startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                    Intent i = new Intent(context, homepage.class);
                    context.startActivity(i);

                }



            }
        });

        String store_id = String.valueOf(model.getStoreId());
        Cursor r = sqLiteDatabase.rawQuery("SELECT * FROM ratings WHERE rate_store_id =?",new  String[]{store_id});
        int count = r.getCount();
        holder.rateNum.setText("("+count+")");

        dbHelper = new DBHelper(context);
        float total = dbHelper.getStarTotal(store_id);
        holder.rate.setRating(total);




    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView store_image;
        RatingBar rate;
        TextView store_name, store_address, store_email, store_contact, store_time, store_by, store_fee, rateNum;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            store_image=(ImageView)itemView.findViewById(R.id.storeImage);
            store_name=(TextView)itemView.findViewById(R.id.store_name);
            store_address=(TextView)itemView.findViewById(R.id.store_address);
            store_email=(TextView)itemView.findViewById(R.id.store_email);
            store_time=(TextView)itemView.findViewById(R.id.store_time);
            store_by=(TextView)itemView.findViewById(R.id.store_by);
            store_fee=(TextView)itemView.findViewById(R.id.shipping);
            store_contact=(TextView)itemView.findViewById(R.id.store_contact);
            rate = itemView.findViewById(R.id.ratings);
            rateNum = itemView.findViewById(R.id.rate_num);

        }
    }
}
