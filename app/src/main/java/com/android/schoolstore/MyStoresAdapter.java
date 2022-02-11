package com.android.schoolstore;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyStoresAdapter extends RecyclerView.Adapter<MyStoresAdapter.ViewHolder>{
    Context context;
    int singleData;
    ArrayList<Model> modelArrayList;
    SQLiteDatabase sqLiteDatabase;
    Dialog dialog;
    //constructor


    public MyStoresAdapter(Context context, int singleData, ArrayList<Model> modelArrayList, SQLiteDatabase sqLiteDatabase, Dialog dialog) {
        this.context = context;
        this.singleData = singleData;
        this.modelArrayList = modelArrayList;
        this.sqLiteDatabase = sqLiteDatabase;
        this.dialog = dialog;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.single_my_store,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final Model model = modelArrayList.get(position);
        byte[]image = model.getStorePicture();
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0,image.length);
        holder.my_store_image.setImageBitmap(bitmap);
        holder.my_store_name.setText(model.getStoreName());

        holder.my_store_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("id", model.getStoreId());
                bundle.putString("store_owner", model.getStoreAdd());
                Intent intent=new Intent(context,view_store.class);
                intent.putExtra("store_data",bundle);
                context.startActivity(intent);
            }
        });

        holder.delete_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(context);
                dialog.setContentView(R.layout.remove_store_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(true);
                Button remove = dialog.findViewById(R.id.remove_btn);
                Button no = dialog.findViewById(R.id.no_btn);

                remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DBHelper dbHelper = new DBHelper(context);
                        sqLiteDatabase = dbHelper.getReadableDatabase();
                        long rec_delete=sqLiteDatabase.delete("restaurants", "res_id="+model.getStoreId(),null);
                        long food_delete = sqLiteDatabase.delete("food","own_id="+model.getStoreId(),null);
                        long cart_delete = sqLiteDatabase.delete("my_cart","store_id="+model.getStoreId(),null);
                        if(rec_delete!= -1 || food_delete != -1 || cart_delete != -1){
                            Toast.makeText(context, "Store Removed.", Toast.LENGTH_SHORT).show();
                            //remove position after deleted
                            modelArrayList.remove(position);
                            //update data
                            notifyDataSetChanged();
                            Intent i = new Intent(context, homepage.class);
                            context.startActivity(i);
                        }else Toast.makeText(context, "Remove Failed!", Toast.LENGTH_SHORT).show();
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
        });


    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView my_store_image;
        Button delete_store;
        TextView my_store_name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            my_store_image=(ImageView)itemView.findViewById(R.id.my_store_image);
            delete_store=(Button)itemView.findViewById(R.id.store_delete);
            my_store_name=(TextView)itemView.findViewById(R.id.my_store_name);
        }
    }
}
