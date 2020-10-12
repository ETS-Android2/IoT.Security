package com.example.iotsecurity;

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

/**
 * DPD 프래그먼트
 * url뒤에 lights/1(or 2 or 3)등을 붙임으로서 개별 제어 가능(개별 정보 get은 불가능)
 * get은 전체 전구 목록 받는 것만 가능
 *
 */
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

        // 몇번째 전구인지 받아옴
        String lightNum = product.name.replaceAll("[^0-9]", "");

        requestQueue = Volley.newRequestQueue(getContext().getApplicationContext());
        baseUrl = String.format("http://192.168.0.13/api/CNvVAzMQxpTl2FNN12ipOCvqxbA7X0HEbMoGXoht/lights/");
        baseUrl = baseUrl + lightNum + "/";
        makeRequest(baseUrl);

        return rootView;
    }

    /**
     * get 요청 생성 및 처리
     * @param baseUrl = get을 수행할 타겟 url
     */
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

    /**
     * url로 부터 받은 json 객체를 ui에 맞게 적용
     * Data의 경우 "state" 라는 객체 속에 포함되어 있으므로 따로 저장
     * @param response = url 로 부터 받은 json 객체
     * @throws JSONException
     */
    private void processResponse(JSONObject response) throws JSONException {
        JSONObject state = response.getJSONObject("state");
        JSONObject usingData = new JSONObject();
        usingData.put("on", state.getString("on"));
        usingData.put("bri", state.getString("bri"));
        usingData.put("hue", state.getString("hue"));
        usingData.put("sat", state.getString("sat"));

        name.setText(response.getString("name"));
        provider.setText(response.getString("manufacturername"));
        category.setText("lights");
        data.setText(usingData.toString());
    }
}
