package com.example.iotsecurity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ProductFragment extends Fragment {
    ProductAdapter adpater;
    RecyclerView recyclerView;

    Product product;
    static RequestQueue requestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.product_list, container, false);

        adpater = new ProductAdapter();

        recyclerView= rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adpater);
        makeRequest();
        requestQueue = Volley.newRequestQueue(getContext().getApplicationContext());

        return rootView;
    }

    private void makeRequest() {
        String baseUrl = String.format("http://192.168.0.13/api/6dopn9py8UJkMiStzn0ps0c5ReQEy8kbeIQea6iY");
        StringRequest request = new StringRequest(Request.Method.GET, baseUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                processResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });
        request.setShouldCache(false);
        requestQueue.add(request);
    }

    private void processResponse(String response) {
        JsonParser jsonParser = new JsonParser();
//        JsonElement
//        Gson gson = new Gson();
//        ProductList productList = gson.fromJson(response, ProductList.class);
//        for (int i = 0; i < productList.products.size(); i++) {
//            Product product = productList.products.get(i);
//            adpater.addItem(product);
//        }
//        adpater.notifyDataSetChanged();
    }

}
