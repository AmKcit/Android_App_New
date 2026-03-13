package com.example.android_app_new;

public class AQICalculator {

    public static int calculateOverallAQI(double pm25, double pm10) {
        int aqiPm25 = calculatePm25AQI(pm25);
        int aqiPm10 = calculatePm10AQI(pm10);

        return Math.max(aqiPm25, aqiPm10);
    }

    private static int calculatePm25AQI(double c) {
        if (c <= 12) return scale(c,0,12,0,50);
        else if (c <= 35.4) return scale(c,12.1,35.4,51,100);
        else if (c <= 55.4) return scale(c,35.5,55.4,101,150);
        else if (c <= 150.4) return scale(c,55.5,150.4,151,200);
        else if (c <= 250.4) return scale(c,150.5,250.4,201,300);
        else return scale(c,250.5,350.4,301,400);
    }

    private static int calculatePm10AQI(double c) {
        if (c <= 54) return scale(c,0,54,0,50);
        else if (c <= 154) return scale(c,55,154,51,100);
        else if (c <= 254) return scale(c,155,254,101,150);
        else if (c <= 354) return scale(c,255,354,151,200);
        else if (c <= 424) return scale(c,355,424,201,300);
        else return scale(c,425,504,301,400);
    }

    private static int scale(double c,double bpLo,double bpHi,int iLo,int iHi){
        return (int)Math.round(((iHi-iLo)/(bpHi-bpLo))*(c-bpLo)+iLo);
    }

    public static String getAQICategory(int aqi) {

        if (aqi <= 50) return "Good";
        else if (aqi <= 100) return "Moderate";
        else if (aqi <= 150) return "Unhealthy for Sensitive Groups";
        else if (aqi <= 200) return "Unhealthy";
        else if (aqi <= 300) return "Very Unhealthy";
        else return "Hazardous";

    }
}