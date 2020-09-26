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
import com.google.gson.JsonParser;

import org.json.JSONException;
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
        makeRequest();
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

    private void makeRequest() {
        String baseUrl = String.format("http://192.168.0.13/api/6dopn9py8UJkMiStzn0ps0c5ReQEy8kbeIQea6iY/lights");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, baseUrl,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    processResponse(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    private void processResponse(JSONObject response) throws JSONException {
        JSONObject lightJson = new JSONObject(); // 테스트용 초기
        for (int i = 1; i<response.length(); i++) {
            lightJson = response.getJSONObject(String.valueOf(i));
            Product light = new Product();
            light.name = lightJson.getString("name");
            light.provider = lightJson.getString("manufacturername");
            light.category = "lights";
        }
        textView.setText(response.toString()); // 테스트화용 텍스트뷰
//        Gson gson = new Gson();
//        ProductList productList = gson.fromJson(response, ProductList.class);
//        for (int i = 0; i < productList.products.size(); i++) {
//            Product product = productList.products.get(i);
//            adpater.addItem(product);
//        }
//        adpater.notifyDataSetChanged();
    }

}