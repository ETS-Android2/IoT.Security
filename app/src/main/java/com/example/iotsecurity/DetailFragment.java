package com.example.iotsecurity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DetailFragment extends Fragment {
    TextView name, category, provider, data;
    Product product;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.detail_fragment, container, false);
//        product = (Product)savedInstanceState.getSerializable("product");
        product = (Product)this.getArguments().getSerializable("product");
        name = rootView.findViewById(R.id.name_content);
        category = rootView.findViewById(R.id.category_content);
        provider = rootView.findViewById(R.id.provider_content);
        data = rootView.findViewById(R.id.data_content);

        name.setText(product.name);
        category.setText(product.category);
        provider.setText(product.provider);

        return rootView;
    }
}
