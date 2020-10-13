package com.example.iotsecurity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Control Panel
 * 전구를 제어하는 기능
 * progress bar 통해 제어
 *
 * ver. 2020.09.29 : 제어 기능 동작 확인
 * 개선점 : 제어 완료 후 db에 저장, cycle/period 계산
 * */
public class ControlFragment extends Fragment {
    SeekBar intensitySeekBar, saturationSeekBar, hueSeekBar;
    PieChart pieChart;
    static RequestQueue requestQueue;
    Product product;
    String baseUrl;
    boolean on = true;
    int bri, hue, sat;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.control_fragment, container, false);
        product = (Product)this.getArguments().getSerializable("product");

        // 전구 번호 추출
        String lightNum = product.name.replaceAll("[^0-9]", "");

        requestQueue = Volley.newRequestQueue(getContext().getApplicationContext());
        baseUrl = String.format("http://192.168.0.13/api/CNvVAzMQxpTl2FNN12ipOCvqxbA7X0HEbMoGXoht/lights/");
        baseUrl = baseUrl + lightNum + "/";
        makeRequest(baseUrl);

        // Chart 세팅
        pieChart = (PieChart)rootView.findViewById(R.id.risk_score);
        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();
        yValues.add(new PieEntry((float)product.score));
        yValues.add(new PieEntry(100-(float)product.score));

        PieDataSet dataSet = new PieDataSet(yValues, "");
        dataSet.setDrawValues(false);
        dataSet.setColors(Color.RED, Color.WHITE);
        PieData data = new PieData(dataSet);
        pieChart.setData(data);


        // bri 조절
        intensitySeekBar = (SeekBar)rootView.findViewById(R.id.intensity_seekbar);
        intensitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                bri = seekBar.getProgress();
                if(bri == 0)
                    on = false;
                else {
                    on = true;
                }
                makePutRequest(baseUrl);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                bri = seekBar.getProgress();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                bri = seekBar.getProgress();
            }
        });
        // sat 조절
        saturationSeekBar = (SeekBar)rootView.findViewById(R.id.saturation_seekbar);
        saturationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sat = seekBar.getProgress();
                makePutRequest(baseUrl);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                sat = seekBar.getProgress();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sat = seekBar.getProgress();
            }
        });
        // hue 조절
        hueSeekBar = (SeekBar)rootView.findViewById(R.id.hue_seekbar);
        hueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                hue = seekBar.getProgress();
                makePutRequest(baseUrl);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                makePutRequest(baseUrl);
            }
        });

        return rootView;
    }

    private void makeRequest(String baseUrl) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, baseUrl, null, new Response.Listener<JSONObject>() {
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
        JSONObject lightJson = response.getJSONObject("state");
        bri = Integer.parseInt(lightJson.getString("bri"));
        sat = Integer.parseInt(lightJson.getString("sat"));
        hue = Integer.parseInt(lightJson.getString("hue"));
        intensitySeekBar.setProgress(bri);
        saturationSeekBar.setProgress(sat);
        hueSeekBar.setProgress(hue);
    }

    private void makePutRequest(String baseUrl) {
        // 조작 정보 json은 state에 추가
        baseUrl = baseUrl + "state/";
        JSONObject putState = new JSONObject();
        try {
            putState.put("on", on);
            putState.put("bri", bri);
            putState.put("hue", hue);
            putState.put("sat", sat);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, baseUrl, putState, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        // 조작이 끝났으므로 url 초기화
        baseUrl = baseUrl.replaceAll("state/", "");
        request.setShouldCache(false);
        requestQueue.add(request);
    }
}
