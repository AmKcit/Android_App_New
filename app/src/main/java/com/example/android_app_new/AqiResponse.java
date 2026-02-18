package com.example.android_app_new;
public class AqiResponse {
    public String status;
    public Data data;

    public Data getData() { return data; }

    public static class Data {
        public int aqi;
        public City city;

        public int getAqi() { return aqi; }
        public City getCity() { return city; }
    }

    public static class City {
        public String name;
        public String getName() { return name; }
    }
}