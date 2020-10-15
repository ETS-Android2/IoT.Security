package com.example.iotsecurity;

import android.bluetooth.BluetoothClass;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

public class SearchActivity<i> extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search2);

        TextView tv = findViewById(R.id.tv);
        try {
            readResourceInfo();
            readDeviceInfo();
            Log.d("!!!!!resource idx 1   ", String.valueOf(resources.get(1)));

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    //csv파일을 읽어서 devices에 정보 생성
    private ArrayList<Resource> resources = new ArrayList<Resource>();
    private void readResourceInfo() throws IOException {
        InputStream is  = getResources().openRawResource(R.raw.resource);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("EUC-KR")));
        String line="";
        try {
            while ((line = reader.readLine()) != null) {
                //Split by ','
                int idx = 0;
                String[] tokens = line.split(",");
                Resource sample = new Resource();
                //제품명 얻기
                sample.setIdx(idx++);
                sample.setName(tokens[0]);
                sample.setCategory(tokens[1]);
                sample.setManufactureName(tokens[2]);
                sample.setMovement(tokens[3]);
                sample.setConnection(tokens[4]);
                sample.setGatheringMethod(tokens[5]);
                sample.setServices(tokens[6]);
                sample.setDataList(tokens[7]);
                sample.setDataType(tokens[8]);
                sample.setAgreement(Integer.parseInt(tokens[9]));
                sample.setDeviceType(tokens[10]);
                sample.setDisplay(Integer.parseInt(tokens[11]));
                sample.setRiskScore(Double.parseDouble(tokens[12]));
                resources.add(sample);
                Log.d("SearchActivity", "Just created Resource: "+sample);
            }
        } catch (IOException e){
            Log.wtf("Search Activity", "error reading data file on line" + line,e);
        }
    }

    //csv파일을 읽어서 devices에 정보 생성
    private ArrayList<DeviceInfo2> devices = new ArrayList<DeviceInfo2>();
    private void readDeviceInfo() throws IOException {
        InputStream is  = getResources().openRawResource(R.raw.hue);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("EUC-KR")));
        String line="";
        try {
            while ((line = reader.readLine()) != null) {
                //Split by ','
                int idx = 0;
                String[] tokens = line.split(",");
                DeviceInfo2 sample = new DeviceInfo2();
                //제품명 얻기
                sample.setIdx(idx++);
                sample.setName(tokens[0]);
                //데이터 구축
                //벡터 생성
                Vector<Double> vector = new Vector<Double>(13);
                vector.addElement(Double.parseDouble(tokens[1]));
                vector.addElement(Double.parseDouble(tokens[2]));
                vector.addElement(Double.parseDouble(tokens[3]));
                vector.addElement(Double.parseDouble(tokens[4]));
                vector.addElement(Double.parseDouble(tokens[5]));
                vector.addElement(Double.parseDouble(tokens[6]));
                vector.addElement(Double.parseDouble(tokens[7]));
                vector.addElement(Double.parseDouble(tokens[8]));
                vector.addElement(Double.parseDouble(tokens[9]));
                vector.addElement(Double.parseDouble(tokens[10]));
                vector.addElement(Double.parseDouble(tokens[11]));
                vector.addElement(Double.parseDouble(tokens[12]));
                vector.addElement(Double.parseDouble(tokens[13]));
                sample.setVector(vector);
                devices.add(sample);
                Log.d("SearchActivity", "Just created: "+sample);
            }
        } catch (IOException e){
            Log.wtf("Search Activity", "error reading data file on line" + line,e);
        }
    }
    //코사인 유사도 계산 함수 input 벡터(사용자가 선택한 속성들을 벡터로 변환한 값 v1과 v2 우리가 구축한 devices에 있는 벡터값 유사도 계산)
    private double getScore(Vector<Double> v1, Vector<Double> v2) throws Exception{
        int v1Size = v1.size();
        if (v1Size != v2.size()){
            throw new Exception("Vectors not same size");
        }
        double numerator = 0;
        double v1squaresum = 0;
        double v2squaresum = 0;
        for (int i = 0; i < v1Size; i++){
            double v1Val = v1.get(i);
            double v2Val = v2.get(i);
            numerator += (v1Val * v2Val);
            v1squaresum += (v1Val * v1Val);
            v2squaresum += (v2Val * v2Val);
        }
        if (numerator == 0 || v1squaresum == 0 || v2squaresum == 0){
            return 0;
        }
        double denom = (Math.sqrt(v1squaresum) * Math.sqrt(v2squaresum));
        return numerator / denom;
    }

    //사용자 입력 벡터와 조사 데이터의 유사도계산
    private void CalculateCos(Vector<Double> vec, ArrayList<DeviceInfo2> devices){
        for(DeviceInfo2 di : devices){
            double score = 0;
            try {
                score = getScore(vec, di.getVector());
            } catch (Exception e) {
                e.printStackTrace();
            }
            //DeviceInfo에 유사도 점수 입력
            di.setSimScore(score);
        }
        //유사도가 높은 장치를 맨위로 하여 정렬 수행
        Collections.sort(devices);
    }
    //정렬된 리스트를 사용하여 top5 장치 출력
    private void top5Print( ArrayList<DeviceInfo2> deviceInfo2s){
        //리스트에 뿌려주는 내용 필요
        for(int i=0; i<5; i++){
            deviceInfo2s.get(i);
        }
        //리스트에서 해당 idx에 대한 클릭을 얻었을 때, 해당 productList의 해당 값의 idx를 얻어서 등록과정 종료
        //devices.get(productList.getIdx("사용자 선택 값"));
    }
    //만약 어떠한 값이 선택되었다고 하면 idx를 기반으로 원본 데이터 객체를 가져옴
    private void getIdxDPD(int idx){
        Resource r = resources.get(idx);
        r.getIdx();
        r.getCategory();
        r.getCategory();
        r.getManufactureName();
        r.getMovement();
        r.getConnection();
        r.getGatheringMethod();
        r.getServices();
        r.getDataList();
        r.getDataType();
        r.getAgreement();
        r.getDataType();
        r.getDisplay();
        r.getRiskScore();
    }
}