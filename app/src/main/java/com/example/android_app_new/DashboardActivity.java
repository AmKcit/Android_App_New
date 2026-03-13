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
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
    private CardView cardOpenCalculator;

    private ApiService service;
    private SharedPreferences sharedPreferences;

    private boolean isDarkMode = true;
    private boolean isFirstSelection = true;

    String[] cities = {
            "Kathmandu","Pokhara","Bhaktapur","Lalitpur","Biratnagar",
            "Birgunj","Dhangadhi","Hetauda","Nepalgunj","Butwal",
            "Lahore","Delhi","Mumbai","Bangkok","Jakarta","New York","London"
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
        cardOpenCalculator = findViewById(R.id.cardOpenCalculator);

        applyTheme();

        btnThemeToggle.setOnClickListener(v -> toggleTheme());
        btnLogout.setOnClickListener(v -> logout());

        if (cardOpenCalculator != null) {
            cardOpenCalculator.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardActivity.this, AqiCalculatorActivity.class);
                startActivity(intent);
            });
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(ApiService.class);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                cities
        );

        citySpinner.setAdapter(adapter);

        citySpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {

                if (isFirstSelection) {
                    isFirstSelection = false;
                    return;
                }

                fetchAqi(cities[position]);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });
    }

    private void fetchAqi(String city) {

        service.getCityData(city, API_KEY).enqueue(new Callback<WaqiResponse>() {

            @Override
            public void onResponse(Call<WaqiResponse> call, Response<WaqiResponse> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    showError("API Error: " + response.code());
                    return;
                }

                WaqiResponse.Data data = response.body().getData();

                if (data == null) {
                    showError("City not found");
                    return;
                }

                double aqi = data.getAqi();

                double pm25 = data.getPm25();
                double pm10 = data.getPm10();
                double no2 = data.getNo2();
                double o3 = data.getO3();

                updateUI(aqi, pm25, pm10, no2, o3);
                updateChart(aqi);
            }

            @Override
            public void onFailure(Call<WaqiResponse> call, Throwable t) {

                Log.e("AQI_API_ERROR", t.getMessage());
                showError("Network Error: " + t.getMessage());
            }
        });
    }

    private void updateUI(double aqi, double pm25, double pm10, double no2, double o3) {

        tvAqiValue.setText(String.valueOf((int) aqi));

        if (aqi <= 50) {
            tvStatus.setText("Good");
            tvStatus.setTextColor(Color.parseColor("#00E400"));
        }
        else if (aqi <= 100) {
            tvStatus.setText("Moderate");
            tvStatus.setTextColor(Color.parseColor("#FFFF00"));
        }
        else if (aqi <= 150) {
            tvStatus.setText("Unhealthy (Sensitive)");
            tvStatus.setTextColor(Color.parseColor("#FF7E00"));
        }
        else if (aqi <= 200) {
            tvStatus.setText("Unhealthy");
            tvStatus.setTextColor(Color.parseColor("#FF0000"));
        }
        else if (aqi <= 300) {
            tvStatus.setText("Very Unhealthy");
            tvStatus.setTextColor(Color.parseColor("#8F3F97"));
        }
        else {
            tvStatus.setText("Hazardous");
            tvStatus.setTextColor(Color.parseColor("#7E0023"));
        }

        TextView tvPM25 = findViewById(R.id.tvPM25);
        TextView tvPM10 = findViewById(R.id.tvPM10);

        if (tvPM25 != null) tvPM25.setText(String.format("%.1f", pm25));
        if (tvPM10 != null) tvPM10.setText(String.format("%.1f", pm10));

        tvPrediction.setText("Prediction: Air quality monitoring active");
    }

    private void updateChart(double aqi) {

        ArrayList<Entry> entries = new ArrayList<>();

        entries.add(new Entry(1, (float) (aqi - 10)));
        entries.add(new Entry(2, (float) (aqi - 5)));
        entries.add(new Entry(3, (float) aqi));
        entries.add(new Entry(4, (float) (aqi + 8)));
        entries.add(new Entry(5, (float) (aqi + 4)));

        LineDataSet dataSet = new LineDataSet(entries, "AQI Trend");

        dataSet.setColor(Color.CYAN);
        dataSet.setCircleColor(Color.CYAN);
        dataSet.setLineWidth(3f);

        lineChart.clear();
        lineChart.setData(new LineData(dataSet));

        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getDescription().setEnabled(false);

        lineChart.animateY(800);
        lineChart.invalidate();
    }

    private void showError(String message) {

        tvAqiValue.setText("--");
        tvStatus.setText("Error");
        tvStatus.setTextColor(Color.RED);

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void applyTheme() {

        if (isDarkMode) {
            mainLayout.setBackgroundColor(Color.parseColor("#0A0E27"));
        } else {
            mainLayout.setBackgroundColor(Color.WHITE);
        }
    }

    private void toggleTheme() {

        isDarkMode = !isDarkMode;
        sharedPreferences.edit().putBoolean("isDarkMode", isDarkMode).apply();

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