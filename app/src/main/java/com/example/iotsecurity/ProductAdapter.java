package com.example.iotsecurity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

/**
 * Product에 대한 정보를 layout에 적용
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.myViewHolder> {

    // item 클릭했을 때 반응
    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
    // 객체 참조 저장
    private OnItemClickListener itemListener = null;
    // OnItemClickListener 객체 참조를 어댑터에 전달
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemListener = listener;
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        TextView score, name, provider, category;

        public final View layout;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);


            score = itemView.findViewById(R.id.scoreView);
            name = itemView.findViewById(R.id.name);
            provider = itemView.findViewById(R.id.provider);
            category = itemView.findViewById(R.id.category);
            layout = itemView;
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메소드 호출
                        if (itemListener != null) {
                            itemListener.onItemClick(v, pos);
                        }
                    }
                }
            });
        }

        public void setItem(Product item) {
            // 임시 score.
            score.setText(String.valueOf(item.score));
            name.setText(item.name);
            provider.setText(item.provider);
            category.setText(item.category);
        }
    }


    ArrayList<Product> products = new ArrayList<Product>();

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.product_item, parent, false);

        return new myViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
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


}
