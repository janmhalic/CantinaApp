package com.android.schoolstore;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class starsAdapter extends RecyclerView.Adapter<starsAdapter.ViewHolder> {
    Context context;
    int singleData;
    ArrayList<starsModel> starsModelArrayList;
    SQLiteDatabase sqLiteDatabase;

    public starsAdapter(Context context, int singleData, ArrayList<starsModel> starsModelArrayList, SQLiteDatabase sqLiteDatabase) {
        this.context = context;
        this.singleData = singleData;
        this.starsModelArrayList = starsModelArrayList;
        this.sqLiteDatabase = sqLiteDatabase;
    }

    @NonNull
    @Override
    public starsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.single_reviews,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull starsAdapter.ViewHolder holder, int position) {
        final starsModel starsModel = starsModelArrayList.get(position);
        holder.name.setText(starsModel.getName());
        holder.date.setText(starsModel.getDate());
        holder.context.setText(starsModel.getContext());
        holder.rated.setRating(starsModel.getStars());

    }

    @Override
    public int getItemCount() {
        return starsModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RatingBar rated;
        TextView name, context, date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rated = itemView.findViewById(R.id.ratings);
            name = itemView.findViewById(R.id.rev_name);
            context = itemView.findViewById(R.id.rev_content);
            date = itemView.findViewById(R.id.rev_date);
        }
    }
}
