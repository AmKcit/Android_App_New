package com.example.android_app_new;

public class AqiTrendData {
    private String date;
    private double aqi;
    private String status;
    private double pm25;
    private double pm10;
    private double no2;
    private double o3;

    /**
     * Constructor for AQI Trend Data
     */
    public AqiTrendData(String date, double aqi, String status, double pm25, double pm10, double no2, double o3) {
        this.date = date;
        this.aqi = aqi;
        this.status = status;
        this.pm25 = pm25;
        this.pm10 = pm10;
        this.no2 = no2;
        this.o3 = o3;
    }

    // Getters
    public String getDate() {
        return date;
    }

    public double getAqi() {
        return aqi;
    }

    public String getStatus() {
        return status;
    }

    public double getPm25() {
        return pm25;
    }

    public double getPm10() {
        return pm10;
    }

    public double getNo2() {
        return no2;
    }

    public double getO3() {
        return o3;
    }

    // Setters
    public void setDate(String date) {
        this.date = date;
    }

    public void setAqi(double aqi) {
        this.aqi = aqi;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPm25(double pm25) {
        this.pm25 = pm25;
    }

    public void setPm10(double pm10) {
        this.pm10 = pm10;
    }

    public void setNo2(double no2) {
        this.no2 = no2;
    }

    public void setO3(double o3) {
        this.o3 = o3;
    }
}

