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
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SellsAdapter extends RecyclerView.Adapter<SellsAdapter.ViewHolder> {
    Context context;
    int sellsSingleData;
    ArrayList<SellsModel>modelArrayList;
    SQLiteDatabase sqLiteDatabase;

    //Generate Constructor

    public SellsAdapter(Context context, int sellsSingleData, ArrayList<SellsModel> modelArrayList, SQLiteDatabase sqLiteDatabase) {
        this.context = context;
        this.sellsSingleData = sellsSingleData;
        this.modelArrayList = modelArrayList;
        this.sqLiteDatabase = sqLiteDatabase;
    }

    @NonNull
    @Override
    public SellsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_sells,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SellsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final SellsModel sellsModel = modelArrayList.get(position);
        byte[]image = sellsModel.getSell_image();
        Bitmap bitmap = BitmapFactory.decodeByteArray(image,0,image.length);
        holder.sellsImage.setImageBitmap(bitmap);
        holder.sellsName.setText(sellsModel.getSell_name());
        holder.sellsDescription.setText(sellsModel.getSell_description());
        holder.sellsStocks.setText(sellsModel.getSell_qty());
        holder.sellsPrice.setText(sellsModel.getSell_price());

        //flow menu
        holder.flow_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context,holder.flow_menu);
                popupMenu.inflate(R.menu.sells_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.edit_sell:
                                ////edit operation

                                Bundle bundle = new Bundle();
                                bundle.putInt("id", sellsModel.getSell_id());
                                bundle.putString("name", sellsModel.getSell_name());
                                bundle.putString("description",sellsModel.getSell_description());
                                bundle.putString("price",sellsModel.getSell_price());
                                bundle.putString("qty", sellsModel.getSell_qty());
                                bundle.putByteArray("image", sellsModel.getSell_image());
                                bundle.putString("addBy", sellsModel.getSell_AddedBy());
                                Intent intent=new Intent(context,makeOrder.class);
                                intent.putExtra("order_data",bundle);
                                context.startActivity(intent);
                                break;
                            case R.id.delete_sell:
                                ///delete operation
                                DBHelper dbHelper = new DBHelper(context);
                                sqLiteDatabase = dbHelper.getReadableDatabase();

                                long sellDelete = sqLiteDatabase.delete("food","id="+sellsModel.getSell_id(),null);
                                if (sellDelete != -1) {
                                    Toast.makeText(context, "Sell Deleted", Toast.LENGTH_SHORT).show();
                                    //remove position after deleted
                                    modelArrayList.remove(position);
                                    //update Data
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
        return modelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView sellsImage;
        TextView sellsName,sellsDescription, sellsPrice, sellsStocks;
        ImageButton flow_menu;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sellsImage=(ImageView)itemView.findViewById(R.id.sellsImage);
            sellsName=(TextView)itemView.findViewById(R.id.sells_name);
            sellsDescription=(TextView)itemView.findViewById(R.id.sells_des);
            sellsPrice=(TextView)itemView.findViewById(R.id.sells_price);
            sellsStocks=(TextView)itemView.findViewById(R.id.sells_stocks);
            flow_menu=(ImageButton)itemView.findViewById(R.id.sells_menu);

        }
    }
}
