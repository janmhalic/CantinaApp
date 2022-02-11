package com.android.schoolstore;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class summaryAdapter extends RecyclerView.Adapter<summaryAdapter.ViewHolder> {
    Context context;
    int singleData;
    ArrayList<OrderModel> orderModels;
    SQLiteDatabase sqLiteDatabase;
    //Constructor


    public summaryAdapter(Context context, int singleData, ArrayList<OrderModel> orderModels, SQLiteDatabase sqLiteDatabase) {
        this.context = context;
        this.singleData = singleData;
        this.orderModels = orderModels;
        this.sqLiteDatabase = sqLiteDatabase;
    }

    @NonNull
    @Override
    public summaryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.single_summary,null);
        return new summaryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull summaryAdapter.ViewHolder holder, int position) {
        final OrderModel orderModel = orderModels.get(position);
        holder.qty.setText(orderModel.getOrder_qty());
        holder.item_name.setText(orderModel.getItem_name());
        holder.item_des.setText(orderModel.getItem_des());
        int num = Integer.parseInt(orderModel.getOrder_qty());
        double price = Double.parseDouble(orderModel.getOrder_price());
        double sum = num * price;
        holder.item_total.setText("₱"+sum);
    }

    @Override
    public int getItemCount() {
        return orderModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView qty, item_name, item_des, item_total;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            qty = itemView.findViewById(R.id.qty);
            item_name = itemView.findViewById(R.id.item_name);
            item_total = itemView.findViewById(R.id.item_total);
            item_des = itemView.findViewById(R.id.item_des);
        }
    }
}
