package com.example.android_app_new;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.components.XAxis;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class DashboardActivity extends AppCompatActivity {

    private Spinner citySpinner;
    private TextView tvAqiValue, tvStatus, tvPrediction;
    private LineChart lineChart;
    private ImageButton btnThemeToggle, btnLogout;
    private LinearLayout mainLayout;

    private ApiService service;

    private SharedPreferences sharedPreferences;
    private boolean isDarkMode = true;

    // Cities supported by WAQI
    String[] cities = {
            "lahore",
            "delhi",
            "mumbai",
            "bangkok",
            "jakarta",
            "shanghai",
            "beijing",
            "seoul",
            "tokyo",
            "london"
    };

    private static final String API_KEY = "87c3349785b993ad86d4b01fa941e94ebaf8f224";
    private static final String BASE_URL = "https://api.waqi.info/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        isDarkMode = sharedPreferences.getBoolean("isDarkMode", true);

        setContentView(R.layout.activity_dashboard);

        mainLayout = findViewById(R.id.mainLayout);
        citySpinner = findViewById(R.id.citySpinner);
        tvAqiValue = findViewById(R.id.tvAqiValue);
        tvStatus = findViewById(R.id.tvAqiStatus);
        tvPrediction = findViewById(R.id.tvPrediction);
        lineChart = findViewById(R.id.lineChart);
        btnThemeToggle = findViewById(R.id.btnThemeToggle);
        btnLogout = findViewById(R.id.btnLogout);

        applyTheme();

        btnThemeToggle.setOnClickListener(v -> toggleTheme());
        btnLogout.setOnClickListener(v -> logout());

        // Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(ApiService.class);

        // Spinner setup
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item,
                        cities);

        citySpinner.setAdapter(adapter);

        citySpinner.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(android.widget.AdapterView<?> parent,
                                               android.view.View view,
                                               int position,
                                               long id) {

                        String city = cities[position];
                        fetchAqi(city);
                    }

                    @Override
                    public void onNothingSelected(android.widget.AdapterView<?> parent) {}
                });
    }

    private void fetchAqi(String city) {

        service.getCityData(city, API_KEY)
                .enqueue(new Callback<WaqiResponse>() {

                    @Override
                    public void onResponse(Call<WaqiResponse> call,
                                           Response<WaqiResponse> response) {

                        Log.d("DashboardActivity", "Response Code: " + response.code());
                        Log.d("DashboardActivity", "Response Body: " + (response.body() != null ? response.body().getStatus() : "null"));

                        if (!response.isSuccessful() || response.body() == null) {
                            showError("API Error: " + response.code());
                            return;
                        }

                        WaqiResponse.Data data = response.body().getData();

                        if (data == null) {
                            showError("City not found");
                            return;
                        }

                        int aqi = data.getAqi();

                        updateUI(
                                aqi,
                                data.getPm25(),
                                data.getPm10(),
                                data.getNo2(),
                                data.getO3()
                        );

                        updateChart(aqi);
                    }

                    @Override
                    public void onFailure(Call<WaqiResponse> call, Throwable t) {
                        Log.e("DashboardActivity", "Network Error: " + t.getMessage(), t);
                        showError("Network Error: " + t.getMessage());
                    }
                });
    }

    private void updateUI(double aqi, double pm25, double pm10, double no2, double o3) {

        tvAqiValue.setText(String.valueOf((int) aqi));

        if (aqi <= 50) {
            tvStatus.setText("Good");
            tvStatus.setTextColor(Color.GREEN);
        }
        else if (aqi <= 100) {
            tvStatus.setText("Moderate");
            tvStatus.setTextColor(Color.YELLOW);
        }
        else if (aqi <= 150) {
            tvStatus.setText("Unhealthy (Sensitive)");
            tvStatus.setTextColor(Color.parseColor("#FF9800"));
        }
        else if (aqi <= 200) {
            tvStatus.setText("Unhealthy");
            tvStatus.setTextColor(Color.RED);
        }
        else {
            tvStatus.setText("Very Unhealthy");
            tvStatus.setTextColor(Color.parseColor("#8B0000"));
        }

        TextView tvPM25 = findViewById(R.id.tvPM25);
        TextView tvPM10 = findViewById(R.id.tvPM10);
        TextView tvNO2 = findViewById(R.id.tvNO2);
        TextView tvO3 = findViewById(R.id.tvO3);

        if (tvPM25 != null) tvPM25.setText(String.valueOf(pm25));
        if (tvPM10 != null) tvPM10.setText(String.valueOf(pm10));
        if (tvNO2 != null) tvNO2.setText(String.valueOf(no2));
        if (tvO3 != null) tvO3.setText(String.valueOf(o3));

        tvPrediction.setText("Next Hour Prediction: Stable");
    }

    private void updateChart(double aqi) {

        ArrayList<Entry> entries = new ArrayList<>();

        entries.add(new Entry(1, (float)(aqi - 10)));
        entries.add(new Entry(2, (float)(aqi - 5)));
        entries.add(new Entry(3, (float)aqi));
        entries.add(new Entry(4, (float)(aqi + 8)));
        entries.add(new Entry(5, (float)(aqi + 4)));

        LineDataSet dataSet = new LineDataSet(entries, "AQI Trend");

        dataSet.setColor(Color.CYAN);
        dataSet.setCircleColor(Color.CYAN);
        dataSet.setLineWidth(3f);
        dataSet.setCircleRadius(4f);

        LineData lineData = new LineData(dataSet);

        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        lineChart.getAxisRight().setEnabled(false);

        lineChart.animateY(800);
        lineChart.invalidate();
    }

    private void showError(String message) {

        tvAqiValue.setText("--");
        tvStatus.setText("Error");
        tvStatus.setTextColor(Color.RED);
        tvPrediction.setText(message);

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void applyTheme() {

        if (isDarkMode) {
            getWindow().getDecorView().setBackgroundColor(Color.parseColor("#0A0E27"));
        } else {
            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        }
    }

    private void toggleTheme() {

        isDarkMode = !isDarkMode;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isDarkMode", isDarkMode);
        editor.apply();

        recreate();
    }

    private void logout() {

        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }
}