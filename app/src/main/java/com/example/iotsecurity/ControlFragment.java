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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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


    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Products");

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
            final String idForDB = lightNum;

            requestQueue = Volley.newRequestQueue(getContext().getApplicationContext());
            baseUrl = String.format("http://192.168.0.7/api/f-Rz07jDeVeeCZvfVJ-9lDzE051JzHcsLKrXJG0R/lights/");
            baseUrl = baseUrl + lightNum + "/";
            makeRequest(baseUrl, idForDB);

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
                    makePutRequest(baseUrl, idForDB);
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
                    makePutRequest(baseUrl, idForDB);
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
                    makePutRequest(baseUrl, idForDB);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { hue = seekBar.getProgress(); }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) { hue = seekBar.getProgress(); }
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

                    try {
                        JSONObject productData = new JSONObject();

                        productData.put("age", age);
                        productData.put("height", height);
                        productData.put("weight", weight);
                        productData.put("gender", gender);


                        product.data = productData.toString();

                        intent.putExtra("product", product);

                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                    mDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            // 하드 코딩
                            Product temp = (Product)snapshot.child("3").getValue(Product.class);
                            try {
                                JSONObject tempData = new JSONObject(temp.data);

                                heightTV.setText(tempData.get("height").toString());
                                ageTV.setText(tempData.get("age").toString());
                                weightTV.setText(tempData.get("weight").toString());
                                genderTV.setText(tempData.get("gender").toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                }
            });
            calculate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    double temp = 0.0;

                    double heightTmp = Double.parseDouble((String)heightTV.getText());
                    double ageTmp = Double.parseDouble((String)ageTV.getText());
                    double weightTmp = Double.parseDouble((String)weightTV.getText());
                    String genderTmp = (String)genderTV.getText();

                    temp = getBMI(heightTmp, ageTmp, weightTmp, genderTmp);
                    bmiTV.setText("" + temp);

                    temp = getBMR(heightTmp, ageTmp, weightTmp, genderTmp);
                    bmrTV.setText("" + temp);

                    temp = getIdealWeight(heightTmp, ageTmp, weightTmp, genderTmp);
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

    private double getIdealWeight(double height, double age, double weight, String gender) {
        double result = 0.0;

        if(gender.equals("Male"))
            result = (height / 100) * (height / 100) * 22;
        else if(gender.equals("Female"))
            result = (height / 100) * 100 * 21;
        else
            result = 2; // ideal weight 구하는 식은 여러 종류이며 종류별로 결과가 다름.

        result = Math.round(result * 100) / 100.0;
        return result;
    }

    private double getBMR(double height, double age, double weight, String gender) {
        double result = 0.0;
        if(gender.equals("Female")) {
            result = 447.593;
            result += weight * 9.247;
            result += height * 3.098;
            result -= age * 4.330;
        }
        else if(gender.equals("Male")) {
            result = 88.362;
            result += weight * 13.397;
            result += height * 4.799;
            result -= age * 5.677;
        }
        else
            result = -1;

        // Capping
        if(gender.equals("Female") && result > 2996)
            result = 5000;
        else if(gender.equals("Male") && result > 2322)
            result = 5000;

//        // set maximum or minimum
//        if(result > 10000 || result <500)
//            result = -1;

        result = Math.round(result * 100) / 100.0;

        return result;
    }

    private double getBMI(double height, double age, double weight, String gender) {
        double result = weight / ((height/100) * (height/100));

        result = Math.round(result*100) / 100.0;
        // set maximum or minimum
//        if(result > 90 || result < 10)
//            result = -1;

        return result;
    }

    private void makeRequest(String baseUrl, String deviceId) {
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
        addPeriod(deviceId);
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

    private void makePutRequest(String baseUrl, String deviceId) {
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
        addPeriod(deviceId);
    }

    private void addPeriod(final String idForDB) {

        /**
         * 연결될 때(제품 제어 및 데이터 조회)마다 횟수 증가
         * database 연결 후 period +1 해서 다시 저장
         */
        mDatabase.child(idForDB).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Product temp = snapshot.getValue(Product.class);
                temp.period += 1;


                mDatabase.child(idForDB).setValue(temp);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
