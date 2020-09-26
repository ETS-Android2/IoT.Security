package com.example.iotsecurity;

import java.io.Serializable;
import java.util.ArrayList;

public class Product implements Serializable {
    String name;
    String provider;
    String category;
    String connection;
    double score;
    boolean display;
    ArrayList<String> data;

    public Product() {
    }

    public Product(String name, String provider, String category, String connection, boolean display, ArrayList<String> data) {
        this.name = name;
        this.provider = provider;
        this.category = category;
        this.connection = connection;
        this.display = display;
        this.data = data;
    }

}
