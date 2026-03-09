package com.example.android_app_new;

import com.google.gson.annotations.SerializedName;

public class WaqiResponse {

    private String status;
    private Data data;

    public String getStatus() {
        return status;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        private int aqi = 0;

        @SerializedName("city")
        private CityInfo city;

        @SerializedName("iaqi")
        private IAQIData iaqi;

        // Fallback fields for simple API responses
        @SerializedName("pm25")
        private Double pm25Value;

        @SerializedName("pm10")
        private Double pm10Value;

        public int getAqi() {
            return aqi;
        }

        public String getCity() {
            if (city != null && city.getName() != null) {
                return city.getName();
            }
            return "Unknown";
        }

        public double getPm25() {
            if (iaqi != null && iaqi.pm25 != null) {
                return iaqi.pm25.getValue();
            }
            return pm25Value != null ? pm25Value : 0;
        }

        public double getPm10() {
            if (iaqi != null && iaqi.pm10 != null) {
                return iaqi.pm10.getValue();
            }
            return pm10Value != null ? pm10Value : 0;
        }

        public double getO3() {
            return (iaqi != null && iaqi.o3 != null) ? iaqi.o3.getValue() : 0;
        }

        public double getNo2() {
            return (iaqi != null && iaqi.no2 != null) ? iaqi.no2.getValue() : 0;
        }

        public double getSo2() {
            return (iaqi != null && iaqi.so2 != null) ? iaqi.so2.getValue() : 0;
        }

        public double getCo() {
            return (iaqi != null && iaqi.co != null) ? iaqi.co.getValue() : 0;
        }
    }

    public static class CityInfo {
        @SerializedName("name")
        private String name;

        public String getName() {
            return name;
        }
    }

    public static class IAQIData {
        @SerializedName("pm25")
        private PollutantValue pm25;

        @SerializedName("pm10")
        private PollutantValue pm10;

        @SerializedName("o3")
        private PollutantValue o3;

        @SerializedName("no2")
        private PollutantValue no2;

        @SerializedName("so2")
        private PollutantValue so2;

        @SerializedName("co")
        private PollutantValue co;
    }

    public static class PollutantValue {
        @SerializedName("v")
        private double value = 0;

        public double getValue() {
            return value;
        }
    }
}

