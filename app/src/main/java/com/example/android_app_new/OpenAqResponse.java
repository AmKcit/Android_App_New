package com.example.android_app_new;

import java.util.List;

public class OpenAqResponse {

    private List<Result> results;

    public List<Result> getResults() {
        return results;
    }

    public static class Result {
        private String city;
        private List<Measurement> measurements;

        public String getCity() {
            return city;
        }

        public List<Measurement> getMeasurements() {
            return measurements;
        }
    }

    public static class Measurement {
        private String parameter;
        private double value;

        public String getParameter() {
            return parameter;
        }

        public double getValue() {
            return value;
        }
    }
}