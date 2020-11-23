package com.example.iotsecurity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

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
    DatabaseReference mDatabase;
    SeekBar seekBar;

    ProductAdapter adapter;
    RecyclerView recyclerView;

    FloatingActionButton addProductByWifi, openFab, addProductByBluetooth;
    private boolean isFabOpen = false;

    static RequestQueue requestQueue;
    View.OnClickListener onClickListener;

    // hue api hub 아이피 주소
    String baseUrl;
    String addUrl = String.format("http://192.168.0.9/api/");
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.product_fragment, container, false);

        seekBar = rootView.findViewById(R.id.seekBar);
        addProductByBluetooth = rootView.findViewById(R.id.add_by_bluetooth);
        addProductByWifi = rootView.findViewById(R.id.add_by_wifi);
        openFab = rootView.findViewById(R.id.add_product);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                final int targetScore = seekBar.getProgress();
                mDatabase = FirebaseDatabase.getInstance().getReference().child("Products");
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Product temp;
                        ArrayList<Product> products = new ArrayList<>();
                        /**
                         * 전체 제품 리스트 출력
                         * 1번 제품이 없는 경우 예외 발생
                         * 테스트 기간 동안 총 3개 고정(전구 2개, 체중계 1개)
                         */
                        for(int i=1; i<=3; i++) {
                            temp = snapshot.child(String.valueOf(i)).getValue(Product.class);
                            if(temp != null) {
                                if (temp.score <= targetScore) {
                                    products.add(temp);
                                    adapter.addItem(temp);
                                }
                            }
                        }
                        adapter.setItems(products);
                        adapter.notifyDataSetChanged();
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        /**
         * 장치 추가 버튼 구현
         */
        onClickListener = new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.add_product:
                        if(!isFabOpen) {
                            addProductByWifi.setVisibility(View.VISIBLE);
                            addProductByWifi.setClickable(true);
                            addProductByBluetooth.setVisibility(View.VISIBLE);
                            addProductByBluetooth.setClickable(true);
                            isFabOpen = true;
                        } else {
                            addProductByWifi.setVisibility(View.INVISIBLE);
                            addProductByWifi.setClickable(false);
                            addProductByBluetooth.setVisibility(View.INVISIBLE);
                            addProductByBluetooth.setClickable(false);
                            isFabOpen = false;
                        }
                        break;

                    case R.id.add_by_wifi:
                        Intent intent = new Intent(getActivity(), AddByWifiActivity.class);
                        intent.putExtra("baseUrl", addUrl);
                        startActivity(intent);
                        break;
                    case R.id.add_by_bluetooth:
                        Intent intentForBT = new Intent(getActivity(), AddByBluetoothActivity.class);
                        startActivity(intentForBT);
                        break;
                }
            }
        };
        addProductByBluetooth.setOnClickListener(onClickListener);
        addProductByWifi.setOnClickListener(onClickListener);
        openFab.setOnClickListener(onClickListener);

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



        return rootView;
    }

}
