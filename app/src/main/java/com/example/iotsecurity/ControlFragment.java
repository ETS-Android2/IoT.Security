package com.example.iotsecurity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
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
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
    Button inputData, calculate;
    boolean on = true;
    int bri, hue, sat;

    TextView heightTV, ageTV, weightTV, genderTV;
    TextView bmiTV, bmrTV, idealWeightTV;

    public ControlFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        product = (Product)this.getArguments().getSerializable("product");
        ViewGroup rootView = null;

        // 전구일 때 동작할 화면
        if(product.category.equals("전구")) {

            rootView = (ViewGroup) inflater.inflate(R.layout.control_fragment_lights, container, false);


            // 전구 번호 추출
            String lightNum = product.name.replaceAll("[^0-9]", "");

            requestQueue = Volley.newRequestQueue(getContext().getApplicationContext());
            baseUrl = String.format("http://192.168.0.9/api/f-Rz07jDeVeeCZvfVJ-9lDzE051JzHcsLKrXJG0R/lights/");
            baseUrl = baseUrl + lightNum + "/";
            makeRequest(baseUrl);

            // Chart 세팅
            pieChart = (PieChart) rootView.findViewById(R.id.risk_score);
            ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();
            yValues.add(new PieEntry((float) product.score));
            yValues.add(new PieEntry(100 - (float) product.score));

            PieDataSet dataSet = new PieDataSet(yValues, "");
            dataSet.setDrawValues(false);
            dataSet.setColors(Color.RED, Color.WHITE);
            PieData data = new PieData(dataSet);
            pieChart.setData(data);


            // bri 조절
            intensitySeekBar = (SeekBar) rootView.findViewById(R.id.intensity_seekbar);
            intensitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    bri = seekBar.getProgress();
                    if (bri == 0)
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
            saturationSeekBar = (SeekBar) rootView.findViewById(R.id.saturation_seekbar);
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
            hueSeekBar = (SeekBar) rootView.findViewById(R.id.hue_seekbar);
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

        }

        // 체중계일 때 동작할 화면
        else if(product.category.equals("체중계")) {
            rootView = (ViewGroup) inflater.inflate(R.layout.control_fragment_scale, container, false);
            final double height, age, weight;
            final String gender;
            JSONObject productData = null;
            try {
                productData = new JSONObject(product.data);


                heightTV = rootView.findViewById(R.id.height);
                ageTV = rootView.findViewById(R.id.age);
                weightTV = rootView.findViewById(R.id.weight);
                genderTV = rootView.findViewById(R.id.gender);
                bmiTV = rootView.findViewById(R.id.bmi);
                bmrTV = rootView.findViewById(R.id.bmr);
                idealWeightTV = rootView.findViewById(R.id.ideal_weight);



                heightTV.setText(productData.get("height").toString());
                ageTV.setText(productData.get("age").toString());
                weightTV.setText(productData.get("weight").toString());
                genderTV.setText(productData.get("gender").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }


            height = Double.parseDouble((String)heightTV.getText());
            age = Double.parseDouble((String)ageTV.getText());
            weight = Double.parseDouble((String)weightTV.getText());
            gender = (String)genderTV.getText();

            inputData = (Button)rootView.findViewById(R.id.input_data);
            calculate = (Button)rootView.findViewById(R.id.calculate);

            inputData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), InputForScaleActivity.class);
                    intent.putExtra("height", height);
                    intent.putExtra("age", age);
                    intent.putExtra("weight", weight);
                    intent.putExtra("gender", gender);

                    startActivityForResult(intent, 0);
                }
            });
            calculate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    double temp = 0.0;
                    temp = getBMI(height, age, weight, gender);
                    bmiTV.setText("" + temp);

                    temp = getBMR(height, age, weight, gender);
                    bmrTV.setText("" + temp);

                    temp = getIdealWeight(height, age, weight, gender);
                    idealWeightTV.setText("" + temp);

                }
            });

            // Chart 세팅
            pieChart = (PieChart) rootView.findViewById(R.id.risk_score);
            ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();
            yValues.add(new PieEntry((float) product.score));
            yValues.add(new PieEntry(100 - (float) product.score));

            PieDataSet dataSet = new PieDataSet(yValues, "");
            dataSet.setDrawValues(false);
            dataSet.setColors(Color.RED, Color.WHITE);
            PieData data = new PieData(dataSet);
            pieChart.setData(data);
        }
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bundle bundle = data.getExtras();

        heightTV.setText("" + bundle.get("height"));
        ageTV.setText("" + bundle.get("age"));
        weightTV.setText("" + bundle.get("weight"));
        genderTV.setText(bundle.get("gender").toString());

    }

    private double getIdealWeight(double height, double age, double weight, String gender) {
        double result = 0.0;

        if(gender.equals("male"))
            result = (height - 80) * 0.7;
        else if(gender.equals("female"))
            result = (height - 70) * 0.6;
        else
            result = 2; // ideal weight 구하는 식은 여러 종류이며 종류별로 결과가 다름.

        return result;
    }

    private double getBMR(double height, double age, double weight, String gender) {
        double result = 0.0;
        if(gender.equals("female")) {
            result = 864.6 + weight * 10.2036;
            result -= height * 0.39336;
            result -= age * 6.204;
        }
        else if(gender.equals("male")) {
            result = 877.8 + weight * 14.916;
            result -= height * 0.726;
            result -= age * 8.976;
        }
        else
            result = -1;

        // Capping
        if(gender.equals("female") && result > 2996)
            result = 5000;
        else if(gender.equals("male") && result > 2322)
            result = 5000;

        // set maximum or minimum
        if(result > 10000 || result <500)
            result = -1;

        return result;
    }


    private double getBMI(double height, double age, double weight, String gender) {
        double result = weight / ((height/100) * (height/100));

        // set maximum or minimum
        if(result > 90 || result < 10)
            result = -1;

        return result;
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
