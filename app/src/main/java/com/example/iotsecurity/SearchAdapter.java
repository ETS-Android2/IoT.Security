package com.example.iotsecurity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.myViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    // 객체 참조 저장
    private ProductAdapter.OnItemClickListener itemListener = null;

    // OnItemClickListener 객체 참조를 어댑터에 전달
    public void setOnItemClickListener(ProductAdapter.OnItemClickListener listener) {
        this.itemListener = listener;
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        TextView score, name, provider, category;
        ImageView iconRecommend;


        public final View layout;

        //
        /**
         * 각 Product에 해당하는 itemView마다 Click Listner를 설정해 둠.
         * OnClick ->
         * @param itemView
         */
        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            score = itemView.findViewById(R.id.scoreView);
            name = itemView.findViewById(R.id.name);
            provider = itemView.findViewById(R.id.provider);
            category = itemView.findViewById(R.id.category);
            iconRecommend = itemView.findViewById(R.id.best);


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

        /**
         * 받아온 product 객체를 view에 대입
         * @param item
         */
        public void setItem(Product item) {
            final Product tmp = item;

            score.setText(String.valueOf(tmp.score));
            name.setText(tmp.name);
            provider.setText(tmp.provider);
            category.setText(tmp.category);

            int pos = getAdapterPosition();
            if(pos != RecyclerView.NO_POSITION) {
                // 상위 2개에는 추천 아이콘 출력
                if(pos<2)
                    iconRecommend.setVisibility(View.VISIBLE);
            }
        }
    }

    ArrayList<Product> products = new ArrayList<Product>();

    @NonNull
    @Override
    public SearchAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.search_item, parent, false);
        SearchAdapter.myViewHolder mViewHolder = new SearchAdapter.myViewHolder(itemView);

        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.myViewHolder holder, int position) {
        Product item = products.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void addItem(Product item) { products.add(item); }

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
