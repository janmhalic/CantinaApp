package com.android.schoolstore;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Collection;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> implements View.OnLongClickListener{
    Context context;
    int food_item;
    ArrayList<food>foodArrayList;
    SQLiteDatabase sqLiteDatabase;
    SessionManager sessionManager;
    //generate constructor



    private View.OnLongClickListener onLongClickListener;

    public FoodAdapter(Context context, int food_item, ArrayList<food> foodArrayList, SQLiteDatabase sqLiteDatabase, SessionManager sessionManager) {
        this.context = context;
        this.food_item = food_item;
        this.foodArrayList = foodArrayList;
        this.sqLiteDatabase = sqLiteDatabase;
        this.sessionManager = sessionManager;
    }

    @NonNull
    @Override
    public FoodAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View ListItem = inflater.inflate(R.layout.food_item, null);
        ViewHolder view = new ViewHolder(ListItem);
        ListItem.setOnLongClickListener(this);
//        View view = inflater.inflate(R.layout.food_item, null);
//        return new ViewHolder(view);
        return view;
    }

    @Override
    public void onBindViewHolder(@NonNull FoodAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final food food= foodArrayList.get(position);
        holder.txtOwnID.setText(food.getOwner_id());
        holder.txtname.setText(food.getName());
        holder.txtdes.setText(food.getDescription());
        holder.txtprice.setText(food.getPrice());
        holder.txtqty.setText(food.getQty());
        byte[] food_image = food.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(food_image, 0, food_image.length);
        holder.imagefood.setImageBitmap(bitmap);
        String owner_id = sessionManager.getId();
        String user_id = food.getUser_id();
        if (owner_id.equals(user_id)) holder.flowMenu.setVisibility(View.VISIBLE);
        else holder.flowMenu.setVisibility(View.GONE);

        //to order menu
        holder.imagefood.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                Bundle bundle = new Bundle();
//                bundle.putInt("id", food.getId());
//                bundle.putString("name", food.getName());
//                bundle.putString("description",food.getDescription());
//                bundle.putString("price",food.getPrice());
//                bundle.putString("qty", food.getQty());
//                bundle.putByteArray("image", food.getImage());
//                bundle.putString("addBy", food.getAddedBy());
//                Intent intent=new Intent(context,view_food.class);
//                intent.putExtra("cart_food",bundle);
//                context.startActivity(intent);


            }
        });

        //flow menu
        holder.flowMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context,holder.flowMenu);
                popupMenu.inflate(R.menu.flow_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.edit_menu:
                                ///edit operation



                                Bundle bundle = new Bundle();
                                bundle.putInt("id", food.getId());
                                bundle.putString("name", food.getName());
                                bundle.putString("description",food.getDescription());
                                bundle.putString("price",food.getPrice());
                                bundle.putString("qty", food.getQty());
                                bundle.putByteArray("image", food.getImage());
                                bundle.putString("addBy", food.getAddedBy());
                                bundle.putString("storeId", food.getOwner_id());
                                bundle.putString("StoreName", food.getOwner());
                                Intent intent=new Intent(context,makeOrder.class);
                                intent.putExtra("order_data",bundle);
                                context.startActivity(intent);



                                break;
                            case R.id.delete_menu:
                                ///delete operation
                                DBHelper dbHelper = new DBHelper(context);
                                    sqLiteDatabase = dbHelper.getReadableDatabase();
                                    long rec_delete=sqLiteDatabase.delete("food", "id="+food.getId(),null);
                                    long cart_delete = sqLiteDatabase.delete("my_cart", "food_id="+food.getId(),null);
                                    if(rec_delete!= -1 || cart_delete != -1){
                                        Toast.makeText(context, "Menu Removed.", Toast.LENGTH_SHORT).show();
                                        //remove position after deleted
                                        foodArrayList.remove(position);
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
        return foodArrayList.size();
    }

    public boolean onLongClickListener(View.OnLongClickListener onLongClickListener)
    {
        this.onLongClickListener = onLongClickListener;
        return true;
    }

    @Override
    public boolean onLongClick(View v) {
        onLongClickListener.onLongClick(v);
        return false;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagefood;
        TextView txtname, txtdes, txtprice, txtqty, txtOwnID;
        ImageButton flowMenu;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagefood=(ImageView)itemView.findViewById(R.id.foodImage);
            txtname = (TextView) itemView.findViewById(R.id.foodName);
            txtdes = (TextView) itemView.findViewById(R.id.foodDes);
            txtprice = (TextView) itemView.findViewById(R.id.foodPrice);
            txtqty = (TextView) itemView.findViewById(R.id.foodQty);
            flowMenu=(ImageButton)itemView.findViewById(R.id.edit_menu);
            txtOwnID=(TextView)itemView.findViewById(R.id.ownId);
        }


    }
}
