package com.example.android_app_new;

public class AqiResponse {

    public String status;
    public Data data;

    public String getStatus() {
        return status;
    }

    public Data getData() {
        return data;
    }

    public static class Data {

        public int aqi;
        public Iaqi iaqi;
        public City city;

        public int getAqi() {
            return aqi;
        }

        public Iaqi getIaqi() {
            return iaqi;
        }

        public City getCity() {
            return city;
        }
    }

    public static class City {
        public String name;

        public String getName() {
            return name;
        }
    }

    public static class Iaqi {
        public Value pm25;
        public Value pm10;
        public Value no2;

        public Value getPm25() { return pm25; }
        public Value getPm10() { return pm10; }
        public Value getNo2() { return no2; }
    }

    public static class Value {
        public float v;

        public float getV() {
            return v;
        }
    }
}