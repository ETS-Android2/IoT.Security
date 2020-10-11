package com.example.iotsecurity;

import java.io.Serializable;
import java.util.ArrayList;

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
    public String name;
    public String provider;
    public String category;
    public String connection;
    public double score;
    public boolean display;
    //ArrayList<String> data;

    public Product() {
    }

    public Product(String name, String provider, String category, String connection, boolean display) {
        this.name = name;
        this.provider = provider;
        this.category = category;
        this.connection = connection;
        this.display = display;
        //this.data = data;
    }

}
