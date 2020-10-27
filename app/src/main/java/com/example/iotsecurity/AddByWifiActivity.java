package com.example.iotsecurity;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddByWifiActivity extends AppCompatActivity {
    RequestQueue requestQueue;
    Button checkWifi, searchProduct;
    TextView isWifiText, pushButtonTxt;
    ImageView pushButtonImg;

    public DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_by_wifi);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Products");

        pushButtonImg = findViewById(R.id.push_button_img);
        pushButtonTxt = findViewById(R.id.push_button_txt);
        isWifiText = findViewById(R.id.is_wifi);
        checkWifi = findViewById(R.id.check_wifi);
        searchProduct = findViewById(R.id.search_product);

        checkWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkWifi(getApplicationContext())) {
                    isWifiText.setText("Connected At Wi Fi");
                    pushButtonImg.setVisibility(View.VISIBLE);
                    pushButtonTxt.setVisibility(View.VISIBLE);
                }
                else
                    isWifiText.setText("Not Connected");
            }
        });
        // baseUrl -> "http://192.168.0.7/api/"
        searchProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                String hueURL = (String) intent.getExtras().get("baseUrl");
                makePostRequest(hueURL);
            }
        });



    }

    private boolean checkWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkCapabilities isWifi = cm.getNetworkCapabilities(cm.getActiveNetwork());
        if(isWifi != null) {
            if(isWifi.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                return true;
        }
        return false;
    }

    private void makePostRequest(final String baseUrl) {
        JSONObject userDevice = new JSONObject();
        try {
            userDevice.put("devicetype","cellphone");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        requestQueue = Volley.newRequestQueue(this);

        CustomRequest request = new CustomRequest(Request.Method.POST, baseUrl, userDevice,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            JSONObject responseJson = response.getJSONObject(0);
                            if(responseJson.has("success")) {
                                responseJson = responseJson.getJSONObject("success");
                                String userName = responseJson.getString("username");
                                String getUrl = baseUrl + userName + "/";

                                makeGetRequest(getUrl);
                            }
                            else if(responseJson.has("error"))
                                Toast.makeText(getApplicationContext(), "Push The Link Button on Hub!", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Push The Link Button on Hub!", Toast.LENGTH_SHORT).show();
                    }
                });

        request.setShouldCache(false);
        // 조작이 끝났으므로 url 초기화
        requestQueue.add(request);
    }

    private void makeGetRequest(String baseUrl){
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
        JSONObject lightList = response.getJSONObject("lights");
        JSONObject lightJson, state;
        Product light = new Product();
        boolean isNothing = true;       // 감지되는 제품이 있는지?
        for (int i = 1; i<lightList.length(); i++) {
            lightJson = lightList.getJSONObject(String.valueOf(i));
            state = lightJson.getJSONObject("state");
            if(state.getBoolean("reachable")) {
                isNothing = false;

                String name = lightJson.getString("name");
                String provider = lightJson.getString("manufacturername");
                String modelId = lightJson.getString("modelid");
                String piId = lightJson.getString("productid");
                String productName = lightJson.getString("productname");
                JSONObject usingData = new JSONObject();
                usingData.put("on", state.getString("on"));
                usingData.put("bri", state.getString("bri"));
                usingData.put("hue", state.getString("hue"));
                usingData.put("sat", state.getString("sat"));

                light = new Product();
                light.name = name;
                light.provider = provider;
                light.modelId = modelId;
                light.piId = piId;
                light.productName = productName;
                light.data = usingData.toString();

                light.category = "전구";
                light.connection = "wifi";
                light.display = false;
                light.portable = false;
                light.agree = false;
                light.deviceType = "";
//                light.resourceType = "oic.r.light.brigtness, oic.r.light.dimming, " +
//                        "oic.r.light.raptime, oic.r.switch.binary";
                light.resourceType = "";
                light.serviceType = "";
                light.always = 0;
                light.infoType = "";
                light.score = -1;

                /**
                 * DB에는 연결된 시각을 저장
                 * 불러오거나 넣을때 마다 period +1
                 * cycle은 연결해제(삭제) 할때까지 유지
                 * detail fragment 에서 데이터 출력할 때에는 연결 기간으로 (현재시각 - 연결시각)
                 */
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH:mm", java.util.Locale.getDefault());
                light.cycle = dateFormat.format(date);
                light.period = 1;

                Intent intent = new Intent(this, SearchActivity2.class);
                intent.putExtra("product", light);
                intent.putExtra("productNum", i);
                startActivity(intent);
                finish();
                mDatabase.child("" + i).setValue(light);
            }
        }
        if(isNothing)
            Toast.makeText(this, "Nothing Detected", Toast.LENGTH_SHORT).show();
    }
}
