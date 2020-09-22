package com.example.iotsecurity;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    ArrayList<Product> products = new ArrayList<Product>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.product_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
        Product item = products.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void addItem(Product item) {
        products.add(item);
    }

    public void setItems(ArrayList<Product> items) {
        this.products = items;
    }

    public Product getItem(int position) {
        return products.get(position);
    }

    public void clearItems() {
        this.products.clear();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView score, name, provider, category;

        public final View layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            score = itemView.findViewById(R.id.scoreView);
            name = itemView.findViewById(R.id.name);
            provider = itemView.findViewById(R.id.provider);
            category = itemView.findViewById(R.id.category);
            layout = itemView;
        }

        public void setItem(Product item) {
            // 임시로 입력해 둔 값.
            score.setText("0.0");
            name.setText(item.name);
            provider.setText(item.provider);
            category.setText(item.category);
        }
    }
}
