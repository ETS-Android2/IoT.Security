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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddByWifiActivity extends AppCompatActivity {
    RequestQueue requestQueue;
    Button checkWifi, searchProduct;
    TextView isWifiText, pushButtonTxt;
    ImageView pushButtonImg;

    JSONObject errorJson = new JSONObject();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_by_wifi);

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
        // baseUrl = "http://192.168.0.7/api/"
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

    private void makePostRequest(String baseUrl) {
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
                            Log.d("!!!!!!!!!success check ", responseJson.toString());
                            if(responseJson.has("success")) {
                                responseJson = responseJson.getJSONObject("success");
                                String userName = responseJson.getString("username");

                            }
                            else
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
//        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, baseUrl, userDevice.toString(), new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                Log.d("!!!!!!!!!!!!success check ", response.toString());
////                try {
////                    String userName = response.get(0).toString();
////                    Toast.makeText(getApplicationContext(), userName, Toast.LENGTH_SHORT);
////                } catch (JSONException e) {
////                    e.printStackTrace();
////                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getApplicationContext(), "Push The Link Button on Hub!", Toast.LENGTH_SHORT).show();
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                HashMap<String, String> userDevice = new HashMap<>();
//                userDevice.put("devicetype", "cellphone");
//                return userDevice;
//            }
//        };
        request.setShouldCache(false);
        // 조작이 끝났으므로 url 초기화
        requestQueue.add(request);
    }
}
