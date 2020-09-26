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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailFragment extends Fragment {
    TextView name, category, provider, data;
    Product product;
    String baseUrl;

    static RequestQueue requestQueue;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.detail_fragment, container, false);
        product = (Product)this.getArguments().getSerializable("product");

        name = rootView.findViewById(R.id.name_content);
        category = rootView.findViewById(R.id.category_content);
        provider = rootView.findViewById(R.id.provider_content);
        data = rootView.findViewById(R.id.data_content);

        // 현재 전구의 JSON 데이터 받아옴
        String lightNum = product.name.replaceAll("[^0-9]", "");
        requestQueue = Volley.newRequestQueue(getContext().getApplicationContext());
        baseUrl = String.format("http://192.168.0.13/api/CNvVAzMQxpTl2FNN12ipOCvqxbA7X0HEbMoGXoht/lights/");
        baseUrl = baseUrl + lightNum + "/";
        makeRequest(baseUrl);

        return rootView;
    }

    private void makeRequest(String baseUrl){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, baseUrl,null,  new Response.Listener<JSONObject>() {
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
                System.out.println(error.toString());
            }
        });
        request.setShouldCache(false);
        requestQueue.add(request);
    }

    private void processResponse(JSONObject response) throws JSONException {
        JSONObject state = response.getJSONObject("state");
        JSONObject usingData = new JSONObject();
        usingData.put("on", state.getString("on"));
        usingData.put("bri", state.getString("bri"));
        usingData.put("hue", state.getString("hue"));
        usingData.put("sat", state.getString("sat"));
        // response.lenth()가 1이 작게 나옴! 왜?!?!
        name.setText(response.getString("name"));
        provider.setText(response.getString("manufacturername"));
        category.setText("lights");
        data.setText(usingData.toString());
    }
}
