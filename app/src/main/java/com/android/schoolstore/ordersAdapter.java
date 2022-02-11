package com.android.schoolstore;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Rating;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ordersAdapter extends RecyclerView.Adapter<ordersAdapter.ViewHolder> {
    Context context;
    int singleData;
    ArrayList<OrderModel>orderModels;
    SQLiteDatabase sqLiteDatabase;
    Dialog dialog;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    //Constructor

    public ordersAdapter(Context context, int singleData, ArrayList<OrderModel> orderModels, SQLiteDatabase sqLiteDatabase, Dialog dialog,Calendar calendar,SimpleDateFormat simpleDateFormat) {
        this.context = context;
        this.singleData = singleData;
        this.orderModels = orderModels;
        this.sqLiteDatabase = sqLiteDatabase;
        this.dialog = dialog;
        this.calendar = calendar;
        this.simpleDateFormat = simpleDateFormat;
    }

    @NonNull
    @Override
    public ordersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.single_order,null);
        return new ordersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ordersAdapter.ViewHolder holder, int position) {
        final OrderModel orderModel = orderModels.get(position);
        byte[] picture = orderModel.getItem_picture();
        Bitmap bitmap = BitmapFactory.decodeByteArray(picture, 0,picture.length);
        holder.item_image.setImageBitmap(bitmap);
        holder.item_name.setText(orderModel.getItem_name());
        holder.item_qty.setText(orderModel.getOrder_qty()+" Items");
        holder.item_id.setText("#C"+orderModel.getOder_id());
        holder.number.setText(orderModel.getCount());

        int qty = Integer.parseInt(orderModel.getOrder_qty());
        double price = Double.parseDouble(orderModel.getOrder_price());
        double total = qty * price;
        holder.item_total.setText("₱"+total);

        String status = orderModel.getOrder_status();
        if (status.equals("Finished"))holder.item_status.setTextColor(ContextCompat.getColor(context,R.color.green));
        else holder.item_status.setTextColor(ContextCompat.getColor(context,R.color.red));
        holder.item_status.setText(orderModel.getOrder_status());
        String stat = orderModel.getOrder_status();
        holder.item_date.setText(orderModel.getOrder_date());
        String userId = orderModel.getUser_id();
        String storeId = orderModel.getStore_id();
        Cursor resName = sqLiteDatabase.rawQuery("SELECT * FROM restaurants WHERE res_id = ?", new String[]{storeId});
        if(resName.getCount()>0){
            while(resName.moveToNext()){
                holder.res_name.setText(resName.getString(1));
            }
        }else holder.res_name.setText("Restaurant has been removed by the seller.");

        holder.res_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(resName.getCount()>0) {
                    try {
                        Bundle bundle = new Bundle();
                        bundle.putInt("id", Integer.parseInt(orderModel.getStore_id()));
                        bundle.putString("store_owner", holder.res_name.getText().toString());
                        Intent intent = new Intent(context, view_store.class);
                        intent.putExtra("store_data", bundle);
                        context.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Intent i = new Intent(context, homepage.class);
                        context.startActivity(i);

                    }
                }else Toast.makeText(context, "Restaurant has been removed by the seller.", Toast.LENGTH_SHORT).show();

                resName.close();

            }
        });

        if(stat.equals("Finished")) {
            Cursor checkRate = sqLiteDatabase.rawQuery("SELECT * FROM ratings WHERE rate_user_id = ? and rate_store_id = ?", new String[]{userId,storeId});
            if(checkRate.getCount()>0) {
                holder.rate_btn.setVisibility(View.GONE);
                holder.rate_title.setVisibility(View.VISIBLE);
                holder.rate.setVisibility(View.VISIBLE);
                while(checkRate.moveToNext()){
                    holder.rate.setRating(checkRate.getFloat(5));
                }
                checkRate.close();
            }else{
                holder.rate_btn.setVisibility(View.VISIBLE);
                holder.rate_title.setVisibility(View.GONE);
                holder.rate.setVisibility(View.GONE);
            }

        }
        else{
            holder.rate_btn.setVisibility(View.GONE);
        }



        holder.rate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor checkRate = sqLiteDatabase.rawQuery("SELECT * FROM ratings WHERE rate_user_id = ? and rate_store_id = ?", new String[]{userId,storeId});
                if(checkRate.getCount()>0) {
                    Toast.makeText(context, "You've already post your review", Toast.LENGTH_SHORT).show();
                }else{
                    dialog = new Dialog(context);

                    dialog.setContentView(R.layout.rate_dialog);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.setCancelable(false);


                    ImageButton close = dialog.findViewById(R.id.close);
                    RatingBar stars = dialog.findViewById(R.id.stars);
                    EditText name = dialog.findViewById(R.id.nickname);
                    EditText content = dialog.findViewById(R.id.context);
                    Button submit = dialog.findViewById(R.id.post_btn);

                    calendar = Calendar.getInstance();
                    simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");



                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String user = orderModel.getUser_id();
                            String store = orderModel.getStore_id();
                            String n = name.getText().toString().trim();
                            String c = content.getText().toString().trim();
                            double s = stars.getRating();
                            String Date = simpleDateFormat.format(calendar.getTime());

                            ContentValues contentValues = new ContentValues();
                            contentValues.put("rate_user_id", user);
                            contentValues.put("rate_store_id", store);
                            contentValues.put("rate_name", n);
                            contentValues.put("rate_text", c);
                            contentValues.put("rate_stars", s);
                            contentValues.put("rate_date", Date);

                            long result = sqLiteDatabase.insert("ratings",null,contentValues);
                            if(result==-1)Toast.makeText(context, "Rate Failed!", Toast.LENGTH_SHORT).show();
                            else Toast.makeText(context, "Thank for your Feedback.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();



                        }
                    });

                    dialog.show();
                }
                checkRate.close();


            }
        });

    }

    @Override
    public int getItemCount() {
        return orderModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RatingBar rate;
        Button rate_btn;
        ImageView item_image;
        TextView item_name, item_qty, item_total, item_status, item_id, res_name, item_date, rate_title, number;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_image = itemView.findViewById(R.id.item_image);
            item_name = itemView.findViewById(R.id.item_name);
            item_id = itemView.findViewById(R.id.item_id);
            item_status = itemView.findViewById(R.id.item_status);
            item_total = itemView.findViewById(R.id.item_total);
            item_qty = itemView.findViewById(R.id.item_qty);
            res_name = itemView.findViewById(R.id.res_name);
            item_date = itemView.findViewById(R.id.item_date);
            rate_btn = itemView.findViewById(R.id.rate_btn);
            rate = itemView.findViewById(R.id.rate);
            rate_title = itemView.findViewById(R.id.rate_title);
            number = itemView.findViewById(R.id.number);



        }
    }
}
