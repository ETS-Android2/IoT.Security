package com.example.iotsecurity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.icu.util.DateInterval;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    public DatabaseReference mDatabase;
    static RequestQueue requestQueue;

    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Products");

        requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        String baseUrl = String.format("http://192.168.0.7/api/f-Rz07jDeVeeCZvfVJ-9lDzE051JzHcsLKrXJG0R/lights/");
        makeRequest(baseUrl);

        pager = findViewById(R.id.pager);
        pager.setOffscreenPageLimit(4);

        // fragment 전용 adapter
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());

        // 제품 리스트 fragment
        ProductFragment productFragment = new ProductFragment();
        adapter.addItem(productFragment);   // adapter에 추가

        // home fragment
        HomeFragment homeFragment = new HomeFragment();
        adapter.addItem(homeFragment);

        // adapter 설정
        pager.setAdapter(adapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager));     // tab 선택 여부를 확인해 주는 listener
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));       // tab 선택에 따라 pager에 해당하는 page 정보를 tab에 넘겨주는 listener

    }

    class MyPagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> fragments = new ArrayList<Fragment>();

        public MyPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addItem(Fragment item) {
            fragments.add(item);
        }
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

        for (int i = 1; i<=response.length(); i++) {
            lightJson = response.getJSONObject(String.valueOf(i));
            Product light;
            String name = lightJson.getString("name");
            String provider = lightJson.getString("manufacturername");
            String modelId = lightJson.getString("modelid");
            String piId = lightJson.getString("productid");
            String productName = lightJson.getString("productname");

            light = new Product();
            light.name = name;
            light.provider = provider;
            light.modelId = modelId;
            light.piId = piId;
            light.productName = productName;

            light.category = "lights";
            light.connection = "wifi";
            light.display = false;
            light.portable = false;
            light.agree = false;
            light.deviceType = "actuator";
            light.resourceType = "oic.r.light.brigtness, oic.r.light.dimming, " +
                    "oic.r.light.raptime, oic.r.switch.binary";
            light.serviceType = "will be from csv";
            light.cycle = "20200901 - 20201010";
            light.period = 0;
            light.always = 2;
            light.infoType = "will be from csv too";
            light.score = 27.34;

            mDatabase.child(""+i).setValue(light);
        }
    }
}