package com.example.iotsecurity;

import java.util.Vector;

public class DeviceInfo2 implements Comparable<DeviceInfo2>{
    private int Idx;
    private String Name;
    private Vector<Double> Vector;
    private double SimScore= 0.0;

    public int getIdx() {
        return Idx;
    }

    public void setIdx(int idx) {
        Idx = idx;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public java.util.Vector<Double> getVector() {
        return Vector;
    }

    public void setVector(java.util.Vector<Double> vector) {
        Vector = vector;
    }

    public double getSimScore() {
        return SimScore;
    }

    public void setSimScore(double score) {
        SimScore = score;
    }

    @Override
    public int compareTo(DeviceInfo2 di) {
        if(this.SimScore > di.getSimScore()){
            return -1;
        }else if(this.SimScore < di.getSimScore()){
            return 1;
        }
        return 0;
    }
}
