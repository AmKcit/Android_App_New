package com.example.android_app_new;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.components.XAxis;

import java.util.ArrayList;
import java.util.List;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class DashboardActivity extends AppCompatActivity {

    private Spinner citySpinner;
    private TextView tvAqiValue, tvStatus, tvPrediction;
    private LineChart lineChart;

    private ApiService service;

    String[] cities = {
            "Kathmandu",
            "Pokhara",
            "Lalitpur",
            "Biratnagar",
            "Bharatpur"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        citySpinner = findViewById(R.id.citySpinner);
        tvAqiValue = findViewById(R.id.tvAqiValue);
        tvStatus = findViewById(R.id.tvAqiStatus);
        tvPrediction = findViewById(R.id.tvPrediction);
        lineChart = findViewById(R.id.lineChart);

        // Initialize Retrofit ONCE
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openaq.org/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(ApiService.class);

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
                        fetchAqi(cities[position]);
                    }

                    @Override
                    public void onNothingSelected(android.widget.AdapterView<?> parent) {}
                });
    }

    private void fetchAqi(String selectedCity) {

        service.getLatestData("NP", 100).enqueue(new Callback<OpenAqResponse>() {

            @Override
            public void onResponse(Call<OpenAqResponse> call,
                                   Response<OpenAqResponse> response) {

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().getResults() != null
                        && !response.body().getResults().isEmpty()) {

                    double pm25 = -1;

                    for (OpenAqResponse.Result result :
                            response.body().getResults()) {

                        if (result.getCity() != null &&
                                result.getCity().equalsIgnoreCase(selectedCity)) {

                            for (OpenAqResponse.Measurement m :
                                    result.getMeasurements()) {

                                if ("pm25".equalsIgnoreCase(m.getParameter())) {
                                    pm25 = m.getValue();
                                    break;
                                }
                            }
                        }
                    }

                    if (pm25 != -1) {
                        updateUI(pm25);
                        updateChart(pm25);
                    } else {
                        showError("PM2.5 data not available for this city");
                    }

                } else {
                    showError("No data available");
                }
            }

            @Override
            public void onFailure(Call<OpenAqResponse> call, Throwable t) {
                showError("Network Error");
            }
        });
    }

    private void updateUI(double pm25) {

        tvAqiValue.setText(String.format("%.2f µg/m³", pm25));

        if (pm25 <= 12) {
            tvStatus.setText("Good");
            tvStatus.setTextColor(Color.parseColor("#2E7D32"));
        } else if (pm25 <= 35.4) {
            tvStatus.setText("Moderate");
            tvStatus.setTextColor(Color.parseColor("#F9A825"));
        } else if (pm25 <= 55.4) {
            tvStatus.setText("Unhealthy for Sensitive");
            tvStatus.setTextColor(Color.parseColor("#EF6C00"));
        } else {
            tvStatus.setText("Unhealthy");
            tvStatus.setTextColor(Color.parseColor("#C62828"));
        }

        tvPrediction.setText("Next Hour Prediction: Stable Trend");
    }

    private void updateChart(double value) {

        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(1f, (float)(value - 5)));
        entries.add(new Entry(2f, (float)(value - 2)));
        entries.add(new Entry(3f, (float)value));
        entries.add(new Entry(4f, (float)(value + 3)));

        LineDataSet dataSet = new LineDataSet(entries, "PM2.5 Trend");
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.BLUE);
        dataSet.setLineWidth(3f);
        dataSet.setValueTextColor(Color.BLACK);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        lineChart.getDescription().setEnabled(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.animateY(1000);
        lineChart.invalidate();
    }

    private void showError(String message) {
        tvAqiValue.setText("--");
        tvStatus.setText("Error");
        tvStatus.setTextColor(Color.RED);
        tvPrediction.setText(message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}