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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class customerAdapter extends RecyclerView.Adapter<customerAdapter.ViewHolder>{
    Context context;
    int singleData;
    ArrayList<customerModel> customerModelArrayList;
    SQLiteDatabase sqLiteDatabase;
    //constructor


    public customerAdapter(Context context, int singleData, ArrayList<customerModel> customerModelArrayList, SQLiteDatabase sqLiteDatabase) {
        this.context = context;
        this.singleData = singleData;
        this.customerModelArrayList = customerModelArrayList;
        this.sqLiteDatabase = sqLiteDatabase;
    }

    @NonNull
    @Override
    public customerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.single_customer,null);
        return new customerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull customerAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final customerModel customerModel = customerModelArrayList.get(position);
        String customer_id = customerModel.getCustomer_id();
        String restaurant_id = customerModel.getRestaurant_id();

        holder.c_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("customer_id", Integer.parseInt(customer_id));
                bundle.putString("restaurant_id", restaurant_id);
                bundle.putString("dateAdded", customerModel.getTime());
                bundle.putString("c_id", customerModel.getC_id());
                Intent intent=new Intent(context ,order_summary.class);
                intent.putExtra("store_data",bundle);
                context.startActivity(intent);
            }
        });


        Cursor user = sqLiteDatabase.rawQuery("SELECT * FROM users WHERE id=?",new String[]{customer_id});
        while (user.moveToNext()){
            holder.c_name.setText(user.getString(1));
            byte[]image = user.getBlob(6);
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0,image.length);
            holder.c_image.setImageBitmap(bitmap);
        }
        user.close();

        holder.c_date.setText(customerModel.getDate());
        holder.c_status.setText(customerModel.getStatus());
        holder.c_orders.setText(customerModel.getCount()+" Orders");
        String stat = holder.c_status.getText().toString();
        if(stat.equals("Finished"))holder.c_status.setTextColor(ContextCompat.getColor(context,R.color.green));
        else holder.c_status.setTextColor(ContextCompat.getColor(context,R.color.red));



        //flow menu
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context,holder.menu);
                popupMenu.inflate(R.menu.customer_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.c_summary:
                                ///edit operation
                                Bundle bundle = new Bundle();
                                bundle.putInt("customer_id", Integer.parseInt(customer_id));
                                bundle.putString("restaurant_id", restaurant_id);
                                bundle.putString("dateAdded", customerModel.getTime());
                                bundle.putString("c_id", customerModel.getC_id());
                                Intent intent=new Intent(context ,order_summary.class);
                                intent.putExtra("store_data",bundle);
                                context.startActivity(intent);

                                break;
                            case R.id.c_view:
                                ///view profile
                                Bundle b = new Bundle();
                                b.putInt("id", Integer.parseInt(customer_id));
                                b.putString("store_name", holder.c_name.getText().toString());
                                Intent i=new Intent(context,view_profile.class);
                                i.putExtra("profile_data",b);
                                context.startActivity(i);

                                break;

                            case R.id.c_remove:
                                ///remove
                                DBHelper dbHelper = new DBHelper(context);
                                sqLiteDatabase = dbHelper.getWritableDatabase();
                                long rec_delete = sqLiteDatabase.delete("customers", "c_id="+customerModel.getCustomer_id(),null);
                                if(rec_delete!= -1 ){
                                    Toast.makeText(context, "Customer Removed", Toast.LENGTH_SHORT).show();
                                    //remove position after deleted
                                    customerModelArrayList.remove(position);
                                    //update data
                                    notifyDataSetChanged();

                                }


                                break;
                            default:
                                return false;
                        }
                        return false;
                    }
                });
                //display menu
                popupMenu.show();
            }
        });



    }

    @Override
    public int getItemCount() {
        return customerModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView c_image;
        TextView c_name, c_status, c_orders, c_date;
        ImageButton menu;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            c_image = itemView.findViewById(R.id.c_image);
            c_name = itemView.findViewById(R.id.c_name);
            c_status = itemView.findViewById(R.id.c_status);
            c_orders = itemView.findViewById(R.id.c_orders);
            c_date = itemView.findViewById(R.id.c_date);
            menu = itemView.findViewById(R.id.customer_more);
        }


    }
}
