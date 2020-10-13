package com.example.iotsecurity;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
 * 장치 정보를 받아와서 recyclerView로 장치별로 나눔.
 *
 * ver. 2020.09.26 : http request를 통해 장치 리스트를 json으로 받아옴.
 * 추후 db의 정보를 받아와서 json으로 재정의 후 정보 나누는 것으로 변경
 *
 * ver. 2020.10.10 http request -> DB
 * db 저장과 http request를 분리할 필요가 있음
 */
public class ProductFragment extends Fragment {
    public DatabaseReference mDatabase;

    ProductAdapter adapter;
    RecyclerView recyclerView;

    static RequestQueue requestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.product_fragment, container, false);


        adapter = new ProductAdapter();

        recyclerView= rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        recyclerView.setLayoutManager(layoutManager);

        adapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Product tempLight = adapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("product", tempLight);
                Intent intent = new Intent(getActivity(), ProductDetail.class);
                intent.putExtra("product", tempLight);

                startActivity(intent);
            }
        });

        requestQueue = Volley.newRequestQueue(getContext().getApplicationContext());
        String baseUrl = String.format("http://192.168.0.7/api/f-Rz07jDeVeeCZvfVJ-9lDzE051JzHcsLKrXJG0R/lights/");
        makeRequest(baseUrl);

//        /**
//         * DB에서 데이터 수신 테스트
//         * Value Event Listener
//         */
//        mDatabase = FirebaseDatabase.getInstance().getReference().child("Products");
//        mDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Product temp;
//                for(int i=1; i<=snapshot.getChildrenCount(); i++) {
//                    temp = snapshot.child(String.valueOf(i)).getValue(Product.class);
//                    Log.d("tmp.name : ",String.valueOf(temp.score));
//                    adapter.addItem(temp);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
        recyclerView.setAdapter(adapter);
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
        JSONObject lightJson;
        // response.lenth()가 1이 작게 나옴! 왜?!?!
        for (int i = 1; i<response.length()+1; i++) {
            lightJson = response.getJSONObject(String.valueOf(i));
            Product light = new Product();
            light.name = lightJson.getString("name");
            light.provider = lightJson.getString("manufacturername");
            light.category = "lights";
            light.score = 27.34;
            adapter.addItem(light);
//            mDatabase.child(""+i).setValue(light);
        }

        adapter.notifyDataSetChanged();
    }

}
