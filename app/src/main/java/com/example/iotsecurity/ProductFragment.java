package com.example.iotsecurity;

import android.annotation.SuppressLint;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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

    FloatingActionButton addProductByWifi, openFab, addProductByBluetooth;

    static RequestQueue requestQueue;

    // hue api hub 아이피 주소
    String baseUrl;
    String addUrl = String.format("http://192.168.0.7/api/");
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.product_fragment, container, false);



        /**
         * 장치 추가 버튼 구현
         */
        addProductByBluetooth = rootView.findViewById(R.id.add_by_bluetooth);
        addProductByBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        addProductByWifi = rootView.findViewById(R.id.add_by_wifi);
        addProductByWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddByWifiActivity.class);
                intent.putExtra("baseUrl", addUrl);
                startActivity(intent);
            }
        });

        openFab = rootView.findViewById(R.id.add_product);
        openFab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                addProductByWifi.setVisibility(View.VISIBLE);
                addProductByWifi.setClickable(true);
                addProductByBluetooth.setVisibility(View.VISIBLE);
                addProductByBluetooth.setClickable(true);
            }
        });

        /**
         * 리사이클러 뷰 생성
         */
        recyclerView= rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemViewCacheSize(0);

        adapter = new ProductAdapter();
        adapter.clearItems();

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

        /**
         * Value Event Listener
         */
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Products");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Product temp;
                ArrayList<Product> products = new ArrayList<>();
                /**
                 * 전체 제품 리스트 출력
                 * 1번 제품이 없는 경우 예외 발
                 */
                for(int i=1; i<=snapshot.getChildrenCount(); i++) {
                    temp = snapshot.child(String.valueOf(i)).getValue(Product.class);
                    products.add(temp);
                    adapter.addItem(temp);
                }
                adapter.setItems(products);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return rootView;
    }

}
