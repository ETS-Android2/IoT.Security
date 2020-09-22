package com.example.iotsecurity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;


public class HomeFragment extends Fragment {
    RequestQueue requestQueue;
    TextView textView;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        textView = rootView.findViewById(R.id.textView);

        requestQueue = Volley.newRequestQueue(this.getContext().getApplicationContext());
        String baseUrl = String.format("http://192.168.0.13/api/6dopn9py8UJkMiStzn0ps0c5ReQEy8kbeIQea6iY");
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, baseUrl, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Gson gson = new Gson();
//                ProductList productList = gson.fromJson(response, ProductList.class);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                System.out.println(error);
//            }
//        });
//        request.setShouldCache(false);
//        requestQueue.add(request);

        return rootView;
    }

}
