package com.example.android_app_new;

import com.google.gson.annotations.SerializedName;

public class AqiResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private Data data;

    public String getStatus() {
        return status;
    }

    public Data getData() {
        return data;
    }

    public static class Data {

        @SerializedName("aqi")
        private int aqi;

        @SerializedName("city")
        private City city;   // ✅ ADD THIS

        @SerializedName("iaqi")
        private Iaqi iaqi;

        public int getAqi() {
            return aqi;
        }

        public City getCity() {     // ✅ ADD THIS
            return city;
        }

        public Iaqi getIaqi() {
            return iaqi;
        }
    }

    // ✅ NEW CITY CLASS
    public static class City {

        @SerializedName("name")
        private String name;

        public String getName() {
            return name;
        }
    }

    public static class Iaqi {

        @SerializedName("pm25")
        private Value pm25;

        @SerializedName("pm10")
        private Value pm10;

        @SerializedName("no2")
        private Value no2;

        public Value getPm25() { return pm25; }
        public Value getPm10() { return pm10; }
        public Value getNo2() { return no2; }
    }

    public static class Value {

        @SerializedName("v")
        private float v;

        public float getV() {
            return v;
        }
    }
}