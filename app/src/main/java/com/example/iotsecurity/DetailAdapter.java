package com.example.iotsecurity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.detailViewHolder> {

    @NonNull
    @Override
    public detailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull detailViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class detailViewHolder extends RecyclerView.ViewHolder {
        TextView title, content;
        CheckBox isItPrintable;
        public final View layout;

        public detailViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            content = itemView.findViewById(R.id.content);
            isItPrintable = itemView.findViewById(R.id.checkbox);
            layout = itemView;
        }

        public void setItem(Product item) {

        }
    }
}
