package com.example.iotsecurity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * ver. 2020.09.27 : 기능 동작 테스트 페이지로 사용 중
 * 현재 저장된 장치에 대한 그래프, 평균 risk score 등등 전체적인 통계를 다룰 페이지
 */
public class HomeFragment extends Fragment {

    private DatabaseReference mDatabase;

    RequestQueue requestQueue;
    TextView textView;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        textView = rootView.findViewById(R.id.textView);


        /**
         * Firebase DB TEST
         * ver. 2020.10.08 : 연결 및 생성 테스트
         */
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Products");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Product temp = snapshot.child("1").getValue(Product.class);
//
//                textView.setText(temp.name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        /**
         * Volley Connection Request TEST
         *
         * ver. 2020.09.24 : 연결 생성 및 get 테스트
         */
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
        String baseUrl = String.format("http://192.168.0.7/api/6dopn9py8UJkMiStzn0ps0c5ReQEy8kbeIQea6iY/lights");
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
            Product light;
            String name = lightJson.getString("name");
            String provider = lightJson.getString("manufacturername");
            String modelId = lightJson.getString("modelid");
            String category = "lights";
            String connection = "wifi";
            boolean display = false;
            light = new Product(name, provider, category, connection, display, modelId);
        }
        textView.setText(response.toString()); // 테스트화용 텍스트뷰
    }

}
