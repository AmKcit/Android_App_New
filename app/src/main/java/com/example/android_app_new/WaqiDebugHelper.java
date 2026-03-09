package com.example.android_app_new;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class WaqiDebugHelper {
    private static final String TAG = "WaqiDebugHelper";

    public static void debugResponse(WaqiResponse response) {
        if (response == null) {
            Log.e(TAG, "Response is null");
            return;
        }

        Log.d(TAG, "Status: " + response.getStatus());

        WaqiResponse.Data data = response.getData();
        if (data != null) {
            Log.d(TAG, "AQI: " + data.getAqi());
            Log.d(TAG, "City: " + data.getCity());
            Log.d(TAG, "PM2.5: " + data.getPm25());
            Log.d(TAG, "PM10: " + data.getPm10());
            Log.d(TAG, "O3: " + data.getO3());
            Log.d(TAG, "NO2: " + data.getNo2());
        } else {
            Log.e(TAG, "Data is null");
        }
    }

    public static void debugJson(String json) {
        Log.d(TAG, "Raw JSON Response: " + json);
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Object json2 = gson.fromJson(json, Object.class);
            Log.d(TAG, "Formatted JSON: " + gson.toJson(json2));
        } catch (Exception e) {
            Log.e(TAG, "Error formatting JSON: " + e.getMessage());
        }
    }
}

