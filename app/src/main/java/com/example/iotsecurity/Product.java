package com.example.iotsecurity;

import android.icu.util.DateInterval;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 제품 객체
 * name = 제품명
 * provider = 장치 제공자
 * category = 제품 종류
 * connection = 연결 방식
 * score = 위험도 점수
 * display = 디스플레이 유무
 * data = 제품이 다루는 데이터
 */
public class Product implements Serializable {
    public String name;         // 장치 이름
    public String provider;     // 장치 제공자
    public String category;     // 장치 종류
    public String connection;   // 연결방식
    public boolean display;     // 디스플레이 유무

    public String OCFspec = "ocf.1.3.0";    // 값 고정
    public String modelId;      // 모델 번호
    public String resourceType;
    public String serviceType;  // 서비스 타입
    public Map<String, String> data; // 데이터
    public DateInterval cycle;      // 수집 주기
    public long period;         // 연결 횟수 (count)
    public boolean portable;    // 장치 이동성
    public boolean agree;       // 동의 필요 여부
    public String deviceType;    // 디바이스 타입


    public String description = " ";

    public double score;

    //ArrayList<String> data;

    public Product() {
    }

    public Product(String name, String provider, String category, String connection, boolean display, String modelId) {
        this.name = name;
        this.provider = provider;
        this.category = category;
        this.connection = connection;
        this.display = display;
        this.modelId = modelId;
        //this.data = data;
    }

}
