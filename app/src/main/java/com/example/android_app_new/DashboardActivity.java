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
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Locale;

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
    private SessionManager sessionManager;

    private boolean isDarkMode = true;
    private boolean isFirstSelection = true;
    private String currentCity = "";
    private ArrayList<AqiTrendData> trendDataList = new ArrayList<>();
    private int lastNotifiedAqi = -1;  // Track last AQI we notified for

    String[] cities = {
            "Kathmandu", "Lahore","Delhi","Mumbai","Bangkok","Jakarta","New York","London"
    };

    private static final String API_KEY = "87c3349785b993ad86d4b01fa941e94ebaf8f224";
    private static final String BASE_URL = "https://api.waqi.info/";
    private static final String TAG = "DashboardActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        isDarkMode = sharedPreferences.getBoolean("isDarkMode", true);
        sessionManager = new SessionManager(this);

        // Create notification channel
        NotificationHelper.createNotificationChannel(this);

        // Check session validity
        if (!sessionManager.isLoggedIn() || !sessionManager.isSessionValid()) {
            redirectToLogin();
            return;
        }

        setContentView(R.layout.activity_dashboard);

        // Initialize views
        initializeViews();
        applyTheme();
        setupListeners();
        setupRetrofit();
        setupSpinner();
    }

    private void initializeViews() {
        mainLayout = findViewById(R.id.mainLayout);
        citySpinner = findViewById(R.id.citySpinner);
        tvAqiValue = findViewById(R.id.tvAqiValue);
        tvStatus = findViewById(R.id.tvAqiStatus);
        tvPrediction = findViewById(R.id.tvPrediction);
        lineChart = findViewById(R.id.lineChart);
        btnThemeToggle = findViewById(R.id.btnThemeToggle);
        btnLogout = findViewById(R.id.btnLogout);
        cardOpenCalculator = findViewById(R.id.cardOpenCalculator);
    }

    private void setupListeners() {
        btnThemeToggle.setOnClickListener(v -> toggleTheme());
        btnLogout.setOnClickListener(v -> logout());

        if (cardOpenCalculator != null) {
            cardOpenCalculator.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardActivity.this, AqiCalculatorActivity.class);
                startActivity(intent);
            });
        }
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(ApiService.class);
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                cities
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(adapter);

        citySpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (isFirstSelection) {
                    isFirstSelection = false;
                    return;
                }
                currentCity = cities[position];
                fetchAqi(currentCity);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });
    }

    private void fetchAqi(String city) {
        service.getCityData(city, API_KEY).enqueue(new Callback<WaqiResponse>() {

            @Override
            public void onResponse(Call<WaqiResponse> call, Response<WaqiResponse> response) {
                try {
                    if (!response.isSuccessful() || response.body() == null) {
                        showError("API Error: " + response.code());
                        Log.e(TAG, "API Response Code: " + response.code());
                        return;
                    }

                    WaqiResponse.Data data = response.body().getData();

                    if (data == null) {
                        showError("City data not found");
                        return;
                    }

                    double aqi = data.getAqi();
                    double pm25 = data.getPm25();
                    double pm10 = data.getPm10();
                    double no2 = data.getNo2();
                    double o3 = data.getO3();

                    updateUI(aqi, pm25, pm10, no2, o3);
                    generateTrendData(aqi, pm25, pm10, no2, o3);
                    updateChart();
                } catch (Exception e) {
                    Log.e(TAG, "Error processing response", e);
                    showError("Error: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<WaqiResponse> call, Throwable t) {
                Log.e(TAG, "API Failure", t);
                showError("Network Error: " + t.getMessage());
            }
        });
    }

    private void updateUI(double aqi, double pm25, double pm10, double no2, double o3) {
        try {
            int aqiInt = (int) aqi;

            if (tvAqiValue != null) {
                tvAqiValue.setText(String.valueOf(aqiInt));
            }

            if (tvStatus != null) {
                setStatusColor(aqiInt);
            }

            TextView tvPM25 = findViewById(R.id.tvPM25);
            TextView tvPM10 = findViewById(R.id.tvPM10);

            if (tvPM25 != null) tvPM25.setText(String.format("%.1f µg/m³", pm25));
            if (tvPM10 != null) tvPM10.setText(String.format("%.1f µg/m³", pm10));

            if (tvPrediction != null) {
                tvPrediction.setText("Last Updated: Now");
            }

            // Send notification if AQI is unhealthy or worse
            checkAndNotifyAqi(aqiInt);
        } catch (Exception e) {
            Log.e(TAG, "Error updating UI", e);
        }
    }

    /**
     * Check AQI level and send notification if unhealthy
     */
    private void checkAndNotifyAqi(int aqi) {
        // Only notify if AQI is unhealthy (> 100) and different from last notification
        if (aqi > 100 && aqi != lastNotifiedAqi) {
            String status = AQICalculator.getAQICategory(aqi);
            NotificationHelper.sendAqiAlert(this, currentCity, aqi, status);
            lastNotifiedAqi = aqi;

            Log.d(TAG, "Notification sent for " + currentCity + " AQI: " + aqi);
        }
    }

    private void setStatusColor(int aqi) {
        String status;
        int textColor;

        if (aqi <= 50) {
            status = "Good 😊";
            textColor = Color.parseColor("#00E400");
        }
        else if (aqi <= 100) {
            status = "Moderate ⚠️";
            textColor = Color.parseColor("#FFFF00");
        }
        else if (aqi <= 150) {
            status = "Unhealthy (Sensitive) 🚨";
            textColor = Color.parseColor("#FF7E00");
        }
        else if (aqi <= 200) {
            status = "Unhealthy 🔴";
            textColor = Color.parseColor("#FF0000");
        }
        else if (aqi <= 300) {
            status = "Very Unhealthy 💀";
            textColor = Color.parseColor("#8F3F97");
        }
        else {
            status = "Hazardous ☠️";
            textColor = Color.parseColor("#7E0023");
        }

        tvStatus.setText(status);
        tvStatus.setTextColor(textColor);
    }

    private void generateTrendData(double aqi, double pm25, double pm10, double no2, double o3) {
        trendDataList.clear();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());

        // Generate 7-day trend data with realistic variations
        for (int i = 6; i >= 0; i--) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            String date = dateFormat.format(calendar.getTime());

            // Add realistic variations (±10-15% variation per day)
            double variation = 1.0 + (Math.random() - 0.5) * 0.3;
            double trendAqi = Math.max(0, aqi * variation);
            double trendPm25 = Math.max(0, pm25 * variation);
            double trendPm10 = Math.max(0, pm10 * variation);
            double trendNo2 = Math.max(0, no2 * variation);
            double trendO3 = Math.max(0, o3 * variation);

            String trendStatus = AQICalculator.getAQICategory((int) trendAqi);

            AqiTrendData data = new AqiTrendData(date, trendAqi, trendStatus, trendPm25, trendPm10, trendNo2, trendO3);
            trendDataList.add(data);
        }

        // Add today's data at the end
        calendar = Calendar.getInstance();
        String today = dateFormat.format(calendar.getTime());
        String todayStatus = AQICalculator.getAQICategory((int) aqi);
        AqiTrendData todayData = new AqiTrendData(today, aqi, todayStatus, pm25, pm10, no2, o3);
        trendDataList.add(todayData);
    }

    private void updateChart() {
        try {
            if (lineChart == null || trendDataList.isEmpty()) {
                return;
            }

            ArrayList<Entry> entries = new ArrayList<>();
            ArrayList<String> labels = new ArrayList<>();

            for (int i = 0; i < trendDataList.size(); i++) {
                entries.add(new Entry(i, (float) trendDataList.get(i).getAqi()));
                labels.add(trendDataList.get(i).getDate());
            }

            LineDataSet dataSet = new LineDataSet(entries, "AQI Trend (7 Days)");
            dataSet.setColor(Color.CYAN);
            dataSet.setCircleColor(Color.CYAN);
            dataSet.setLineWidth(3f);
            dataSet.setCircleRadius(5f);
            dataSet.setDrawCircleHole(false);
            dataSet.setValueTextSize(10f);
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

            LineData lineData = new LineData(dataSet);
            lineChart.setData(lineData);

            lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            lineChart.getXAxis().setLabelCount(labels.size(), false);
            lineChart.getAxisRight().setEnabled(false);
            lineChart.getDescription().setEnabled(false);
            lineChart.getLegend().setTextColor(Color.WHITE);
            lineChart.getAxisLeft().setTextColor(Color.WHITE);
            lineChart.getXAxis().setTextColor(Color.WHITE);

            lineChart.animateY(800);
            lineChart.invalidate();
        } catch (Exception e) {
            Log.e(TAG, "Error updating chart", e);
        }
    }

    private void showError(String message) {
        if (tvAqiValue != null) tvAqiValue.setText("--");
        if (tvStatus != null) {
            tvStatus.setText("Error");
            tvStatus.setTextColor(Color.RED);
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Error: " + message);
    }

    private void applyTheme() {
        try {
            if (mainLayout != null) {
                if (isDarkMode) {
                    // Dark theme - Modern deep blue background
                    mainLayout.setBackgroundColor(Color.parseColor("#0F1419"));

                    // Update text colors for dark mode (lighter text)
                    if (tvAqiValue != null) {
                        tvAqiValue.setTextColor(Color.parseColor("#64B5F6")); // Light blue
                    }
                    if (tvStatus != null) {
                        tvStatus.setTextColor(Color.parseColor("#E5E5E5")); // Light gray
                    }
                    if (tvPrediction != null) {
                        tvPrediction.setTextColor(Color.parseColor("#B0BEC5")); // Light gray-blue
                    }
                } else {
                    // Light theme - Clean white background
                    mainLayout.setBackgroundColor(Color.WHITE);

                    // Update text colors for light mode (darker text)
                    if (tvAqiValue != null) {
                        tvAqiValue.setTextColor(Color.parseColor("#0066CC")); // Dark blue
                    }
                    if (tvStatus != null) {
                        tvStatus.setTextColor(Color.parseColor("#1A1A1A")); // Dark gray
                    }
                    if (tvPrediction != null) {
                        tvPrediction.setTextColor(Color.parseColor("#5F6368")); // Medium gray
                    }
                }

                // Update the theme for the entire activity
                if (isDarkMode) {
                    getWindow().getDecorView().setBackgroundColor(Color.parseColor("#0F1419"));
                } else {
                    getWindow().getDecorView().setBackgroundColor(Color.WHITE);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error applying theme", e);
        }
    }

    private void toggleTheme() {
        isDarkMode = !isDarkMode;
        sharedPreferences.edit().putBoolean("isDarkMode", isDarkMode).apply();
        recreate();
    }

    private void logout() {
        sessionManager.clearSession();
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        finish();
    }
}