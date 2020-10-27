package com.example.iotsecurity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;


/**
 * ver. 2020.09.27 : 기능 동작 테스트 페이지로 사용 중
 * 현재 저장된 장치에 대한 그래프, 평균 risk score 등등 전체적인 통계를 다룰 페이지
 */
public class HomeFragment extends Fragment {

    private DatabaseReference mDatabase;



    RequestQueue requestQueue;
    BarChart deviceList, providerList;
    PieChart riskScore;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);
        riskScore = (PieChart)rootView.findViewById(R.id.risk_score);
        deviceList = (BarChart)rootView.findViewById(R.id.device_list);
        providerList = (BarChart)rootView.findViewById(R.id.provider_list);

        /**
         * DB데이터 시각화
         * MP Android Chart Library
         * Risk Score : Pie Chart
         * Device List : Horizontal Bar Chart
         * Provider List : Bar Chart
         *
         */
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Products");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Product temp;
                ArrayList<Product> products = new ArrayList<Product>();

                /**
                 * 전체 제품 리스트 출력
                 * 1번 제품이 없는 경우 예외 발
                 */
                for(int i=1; i<=snapshot.getChildrenCount(); i++) {
                    temp = snapshot.child(String.valueOf(i)).getValue(Product.class);
                    products.add(temp);
                }

                ArrayList<BarEntry> valuesDevice = new ArrayList<>();
                ArrayList<BarEntry> valuesProvider = new ArrayList<>();
                HashMap<String, Integer> contentCount = new HashMap<String, Integer>();
                HashMap<String, Integer> providerCount = new HashMap<>();
                double avgRisk = 0.0;

                // 각 제품이 몇개인지? DATA to hashMap
                for(int i=0; i<products.size(); i++) {
                    String currentCategory = products.get(i).category;
                    String currentProvider = products.get(i).provider;
                    if(!contentCount.containsKey(currentCategory))
                        contentCount.put(currentCategory, 1);
                    else
                        contentCount.put(currentCategory, contentCount.get(currentCategory)+1);

                    if(!providerCount.containsKey(currentProvider))
                        providerCount.put(currentProvider, 1);
                    else
                        providerCount.put(currentProvider, providerCount.get(currentProvider)+1);

                    avgRisk += products.get(i).score;
                }

                Description descrpt = new Description();
                descrpt.setText("");

                // 평균 Risk Score
                avgRisk = avgRisk / products.size();
                ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();
                yValues.add(new PieEntry((float)avgRisk));
                yValues.add(new PieEntry(100-(float)avgRisk));

                PieDataSet dataSetForRisk = new PieDataSet(yValues, "");
                dataSetForRisk.setDrawValues(false);
                dataSetForRisk.setColors(Color.RED, Color.WHITE);
                PieData dataForRisk = new PieData(dataSetForRisk);
                riskScore.setDescription(descrpt);
                riskScore.setData(dataForRisk);
                riskScore.invalidate();

                // hashMap의 key -> labels, value -> values
                final ArrayList<String> labelsDevice = new ArrayList<String>();
                final ArrayList<String> labelsProvider = new ArrayList<String>();
                labelsDevice.addAll(contentCount.keySet());
                labelsProvider.addAll(providerCount.keySet());

                ArrayList<Integer> countsDevice = new ArrayList<Integer>();
                countsDevice.addAll(contentCount.values());
                for(int i=0; i<countsDevice.size(); i++) {
                    valuesDevice.add(new BarEntry(i, countsDevice.get(i)));
                }

                ArrayList<Integer> countsProvider = new ArrayList<Integer>();
                countsProvider.addAll(contentCount.values());
                for(int i=0; i<countsProvider.size(); i++) {
                    valuesProvider.add(new BarEntry(i, countsProvider.get(i)));
                }

                BarDataSet setDevice, setProvider;
                setDevice = new BarDataSet(valuesDevice, "단위 : 개");
                setProvider = new BarDataSet(valuesProvider, "단위 : 개");
                ArrayList<IBarDataSet> dataSetsDevice = new ArrayList<>();
                ArrayList<IBarDataSet> dataSetsProvider = new ArrayList<>();
                dataSetsDevice.add(setDevice);
                dataSetsProvider.add(setProvider);

                BarData dataDevice = new BarData(dataSetsDevice);
                BarData dataProvider = new BarData(dataSetsProvider);

                dataProvider.setBarWidth(0.3f);
                dataDevice.setBarWidth(0.3f);

                // device list x축 설정
                XAxis xAxis = deviceList.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1f);
                xAxis.setDrawGridLines(false);
                xAxis.setCenterAxisLabels(false);
                xAxis.setValueFormatter(new IndexAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        if((int)value<0 || (int)value > labelsDevice.size())
                            return " ";
                        else
                            return labelsDevice.get((int)value);
                    }
                });
                // provider list x축 설정
                xAxis = providerList.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1f);
                xAxis.setDrawGridLines(false);
                xAxis.setCenterAxisLabels(false);
                xAxis.setValueFormatter(new IndexAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        if((int)value<0 || (int)value > labelsProvider.size())
                            return " ";
                        else
                            return labelsProvider.get((int)value);
                    }
                });
                deviceList.getAxisLeft().setDrawLabels(false);
                deviceList.getAxisRight().setDrawLabels(true);
                deviceList.getXAxis().setDrawGridLines(false);

                deviceList.setDescription(descrpt);
                deviceList.setData(dataDevice);
                deviceList.notifyDataSetChanged();
                deviceList.invalidate();

                providerList.getAxisLeft().setDrawLabels(false);
                providerList.getAxisRight().setDrawLabels(true);
                providerList.getXAxis().setDrawGridLines(false);
                providerList.setDescription(descrpt);
                providerList.setData(dataProvider);
                providerList.notifyDataSetChanged();
                providerList.invalidate();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("DB ERROR!! ", error.toString());
            }
        });

        //                /**
//                 * MPAndroidChart TEST
//                 * BarChart 데이터가 뜨지 않아 테스트로 임의 데이터 넣어봄
//                 */
//                ArrayList<BarEntry> values = new ArrayList<>();
//                Random random = new Random();
//
//                for(int i=0; i<3; i++) {
//                    values.add(new BarEntry(i, random.nextInt(5)));
//                }
//
//                BarDataSet set;
//                set = new BarDataSet(values, "counts");
//                ArrayList<IBarDataSet> dataSets = new ArrayList<>();
//                dataSets.add(set);
//
//                BarData data = new BarData(dataSets);
//                data.setBarWidth(0.3f);
//
//                deviceList.setData(data);




        return rootView;
    }

}
//class mValueEventListener implements ValueEventListener {
//    public ArrayList<Product> getProducts() {
//        return products;
//    }
//
//    ArrayList<Product> products;
//
//    mValueEventListener() {
//        products = new ArrayList<>();
//    }
//
//    @Override
//    public void onDataChange(@NonNull DataSnapshot snapshot) {
//        Product temp;
//
//        /**
//         * 전체 제품 리스트 출력
//         * 1번 제품이 없는 경우 예외 발
//         */
//        for(int i=1; i<=snapshot.getChildrenCount(); i++) {
//            temp = snapshot.child(String.valueOf(i)).getValue(Product.class);
//            products.add(temp);
//        }
//
//        ArrayList<BarEntry> values = new ArrayList<>();
//        HashMap<String, Integer> contentCount = new HashMap<String, Integer>();
//
//        Log.d("!!!!!!!!", ""+products.size());
//        // 각 제품이 몇개인지? DATA to hashMap
//        for(int i=0; i<products.size(); i++) {
//            String currentCategory = products.get(i).category;
//
//            if(!contentCount.containsKey(currentCategory))
//                contentCount.put(products.get(i).category, 0);
//            else
//                contentCount.put(currentCategory, contentCount.get(contentCount)+1);
//
//        }
//
//        // hashMap의 key -> labels, value -> values
//        ArrayList<String> labels = new ArrayList<String>();
//        labels.addAll(contentCount.keySet());
//
//        ArrayList<Integer> counts = new ArrayList<Integer>();
//        counts.addAll(contentCount.values());
//
//        for(int i=0; i<counts.size(); i++) {
//            values.add(new BarEntry(i, counts.get(i)));
//        }
//
//        BarDataSet set;
//        set = new BarDataSet(values, "counts");
//        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
//        dataSets.add(set);
//
//        BarData data = new BarData(dataSets);
//        data.setBarWidth(0.9f);
//
//        deviceList.setData(data);
//        deviceList.notifyDataSetChanged();
//        deviceList.invalidate();
//
//    }
//
//    @Override
//    public void onCancelled(@NonNull DatabaseError error) {
//
//    }
//}
