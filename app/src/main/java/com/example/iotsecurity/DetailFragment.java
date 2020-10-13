package com.example.iotsecurity;

import android.content.Intent;
import android.database.DatabaseErrorHandler;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * DPD 프래그먼트
 * url뒤에 lights/1(or 2 or 3)등을 붙임으로서 개별 제어 가능(개별 정보 get은 불가능)
 * get은 전체 전구 목록 받는 것만 가능
 *
 */
public class DetailFragment extends Fragment {
    TextView name, category, provider, data, modelId, piId, productName, connection, display,
            portable, agree, deviceType, serviceType, cycle, period, always, infoType;
    CheckBox checkName, checkCategory, checkProvider, checkData, checkModelId, checkPiId, checkProductName, checkConnection, checkDisplay,
            checkPortable, checkAgree, checkDeviceType, checkServiceType, checkCycle, checkPeriod, checkAlways, checkInfoType;
    FloatingActionButton out;
    Product product, temp = null;
    String baseUrl;
    private String lightNum;

    JSONObject jsonForOut = new JSONObject();

    private DatabaseReference mDatabase;
    static RequestQueue requestQueue;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.detail_fragment, container, false);
        product = (Product)this.getArguments().getSerializable("product");

        //textView 설정
        name = rootView.findViewById(R.id.name_content);
        category = rootView.findViewById(R.id.category_content);
        provider = rootView.findViewById(R.id.provider_content);
        data = rootView.findViewById(R.id.data_content);
        modelId = rootView.findViewById(R.id.model_id_content);
        piId = rootView.findViewById(R.id.pi_id_content);
        productName = rootView.findViewById(R.id.product_name_content);
        connection = rootView.findViewById(R.id.connection_content);
        display = rootView.findViewById(R.id.display_content);
        portable = rootView.findViewById(R.id.portable_content);
        agree = rootView.findViewById(R.id.agree_content);
        deviceType = rootView.findViewById(R.id.device_type_content);
        serviceType = rootView.findViewById(R.id.service_type_content);
        cycle = rootView.findViewById(R.id.cycle_content);
        period = rootView.findViewById(R.id.period_content);
        always = rootView.findViewById(R.id.always_content);
        infoType = rootView.findViewById(R.id.info_type_content);
        // checkBox 설정
        checkName = rootView.findViewById(R.id.checkbox_name);
        checkCategory = rootView.findViewById(R.id.checkbox_category);
        checkProvider = rootView.findViewById(R.id.checkbox_provider);
        checkData = rootView.findViewById(R.id.checkbox_data);
        checkModelId = rootView.findViewById(R.id.checkbox_model_id);
        checkPiId = rootView.findViewById(R.id.checkbox_pi_id);

        // 몇번째 전구인지 받아옴
        lightNum = product.name.replaceAll("[^0-9]", "");

        // DB에서 해당 전구 불러옴
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Products");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                temp = snapshot.child(lightNum).getValue(Product.class);

                // 불러온 값을 채우기
                name.setText(temp.name);
                provider.setText(temp.provider);
                category.setText(temp.category);
                modelId.setText(temp.modelId);
                piId.setText(temp.piId);
                productName.setText(temp.productName);
                connection.setText(temp.connection);
                display.setText(String.valueOf(temp.display));
                portable.setText(String.valueOf(temp.portable));
                agree.setText(String.valueOf(temp.agree));
                deviceType.setText(temp.deviceType);
                serviceType.setText(temp.serviceType);
                cycle.setText(temp.cycle);
                period.setText(String.valueOf(temp.period));
                infoType.setText(temp.infoType);
                if(temp.always == 1)
                    always.setText("수집안함");
                else if(temp.always == 2)
                    always.setText("조건 수집");
                else
                    always.setText("상시 수집");

                // 출력용 JSON
                try {
                    if(checkName.isChecked())
                        jsonForOut.put("name", temp.name);
                    if(checkProvider.isChecked())
                        jsonForOut.put("provider", temp.provider);
                    if(checkCategory.isChecked())
                        jsonForOut.put("category", temp.category);
                    if(checkModelId.isChecked())
                        jsonForOut.put("modelId", temp.modelId);
                    if(checkPiId.isChecked())
                        jsonForOut.put("piId", temp.piId);
                    if(checkProductName.isChecked())
                        jsonForOut.put("productName", temp.productName);
                    if(checkConnection.isChecked())
                        jsonForOut.put("connection", temp.connection);
                    if(checkDisplay.isChecked())
                        jsonForOut.put("display", String.valueOf(temp.display));
                    if(checkPortable.isChecked())
                        jsonForOut.put("portable", String.valueOf(temp.portable));
                    if(checkAgree.isChecked())
                        jsonForOut.put("agree", String.valueOf(temp.agree));
                    if(checkDeviceType.isChecked())
                        jsonForOut.put("deviceType", temp.deviceType);
                    if(checkServiceType.isChecked())
                        jsonForOut.put("serviceType", temp.serviceType);
                    if(checkCycle.isChecked())
                        jsonForOut.put("cycle", temp.cycle);
                    if(checkPeriod.isChecked())
                        jsonForOut.put("period", String.valueOf(temp.period));
                    if(checkInfoType.isChecked())
                        jsonForOut.put("infoType", temp.infoType);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        requestQueue = Volley.newRequestQueue(getContext().getApplicationContext());
        baseUrl = String.format("http://192.168.0.7/api/f-Rz07jDeVeeCZvfVJ-9lDzE051JzHcsLKrXJG0R/lights/");
        baseUrl = baseUrl + lightNum + "/";
        makeRequest(baseUrl);

        // 출력 버튼 동작
        out = (FloatingActionButton)rootView.findViewById(R.id.output);
        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OutputActivity.class);
                intent.putExtra("output", jsonForOut.toString());
                startActivity(intent);
            }
        });

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
     *
     * 개선점 : request가 아닌 DB에서 로드
     * @param response = url 로 부터 받은 json 객체
     * @throws JSONException
     */
    private void processResponse(JSONObject response) throws JSONException {
        // Data 항목 바인딩
        JSONObject state = response.getJSONObject("state");
        JSONObject usingData = new JSONObject();
        usingData.put("on", state.getString("on"));
        usingData.put("bri", state.getString("bri"));
        usingData.put("hue", state.getString("hue"));
        usingData.put("sat", state.getString("sat"));

        data.setText(usingData.toString());
    }

}
