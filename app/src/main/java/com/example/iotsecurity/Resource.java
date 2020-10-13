package com.example.iotsecurity;

public class Resource {
    private int idx;
    private String Name;
    private String Category;
    private String manufactureName;
    private String Movement;
    private String Connection;
    private String GatheringMethod;
    private String Services;
    private String DataList;
    private String DataType;
    private int Agreement;
    private String DeviceType;
    private int Display;
    private double RiskScore=0.0;

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getManufactureName() {
        return manufactureName;
    }

    public void setManufactureName(String manufactureName) {
        this.manufactureName = manufactureName;
    }

    public String getMovement() {
        return Movement;
    }

    public void setMovement(String movement) {
        Movement = movement;
    }

    public String getConnection() {
        return Connection;
    }

    public void setConnection(String connection) {
        Connection = connection;
    }

    public String getGatheringMethod() {
        return GatheringMethod;
    }

    public void setGatheringMethod(String gatheringMethod) {
        GatheringMethod = gatheringMethod;
    }

    public String getServices() {
        return Services;
    }

    public void setServices(String services) {
        Services = services;
    }

    public String getDataList() {
        return DataList;
    }

    public void setDataList(String dataList) {
        DataList = dataList;
    }

    public String getDataType() {
        return DataType;
    }

    public void setDataType(String dataType) {
        DataType = dataType;
    }

    public int getAgreement() {
        return Agreement;
    }

    public void setAgreement(int agreement) {
        Agreement = agreement;
    }

    public String getDeviceType() {
        return DeviceType;
    }

    public void setDeviceType(String deviceType) {
        DeviceType = deviceType;
    }

    public int getDisplay() {
        return Display;
    }

    public void setDisplay(int display) {
        Display = display;
    }

    public double getRiskScore() {
        return RiskScore;
    }

    public void setRiskScore(double riskScore) {
        RiskScore = riskScore;
    }
}
