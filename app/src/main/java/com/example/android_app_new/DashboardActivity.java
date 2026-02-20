package com.example.android_app_new;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvAqiValue, tvStatus, tvPrediction, tvPollutants;
    private Spinner citySpinner;

    private final String API_TOKEN = "87c3349785b993ad86d4b01fa941e94ebaf8f224";

    private int lastAqi = -1;

    String[] cities = {
            "kathmandu",
            "pokhara",
            "lalitpur",
            "biratnagar",
            "bharatpur"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        tvAqiValue = findViewById(R.id.tvAqiValue);
        tvStatus = findViewById(R.id.tvAqiStatus);
        tvPrediction = findViewById(R.id.tvPrediction);
        tvPollutants = findViewById(R.id.tvPollutants);
        citySpinner = findViewById(R.id.citySpinner);

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

    private void fetchAqi(String city) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.waqi.info/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);

        service.getCityAqi(city, API_TOKEN).enqueue(new Callback<AqiResponse>() {

            @Override
            public void onResponse(Call<AqiResponse> call,
                                   Response<AqiResponse> response) {

                if (response.isSuccessful()
                        && response.body() != null
                        && "ok".equals(response.body().getStatus())
                        && response.body().getData() != null) {

                    updateUI(response.body().getData());

                } else {
                    Toast.makeText(DashboardActivity.this,
                            "Failed to fetch data",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AqiResponse> call, Throwable t) {
                tvAqiValue.setText("Error: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void updateUI(AqiResponse.Data data) {

        int currentAqi = data.getAqi();
        tvAqiValue.setText("AQI: " + currentAqi);

        if (currentAqi <= 50) {
            tvStatus.setText("GOOD");
            tvStatus.setTextColor(Color.GREEN);
        } else if (currentAqi <= 100) {
            tvStatus.setText("MODERATE");
            tvStatus.setTextColor(Color.YELLOW);
        } else {
            tvStatus.setText("POOR");
            tvStatus.setTextColor(Color.RED);
        }

        AqiResponse.Iaqi p = data.getIaqi();

        if (p != null) {

            String pollutants =
                    "PM2.5: " + (p.getPm25() != null ? p.getPm25().getV() : "N/A") +
                            "\nPM10: " + (p.getPm10() != null ? p.getPm10().getV() : "N/A") +
                            "\nNO2: " + (p.getNo2() != null ? p.getNo2().getV() : "N/A");

            tvPollutants.setText(pollutants);
        }

        predictHourly(currentAqi);
        lastAqi = currentAqi;
    }

    private void predictHourly(int current) {

        if (lastAqi == -1) {
            tvPrediction.setText("Prediction: Gathering data...");
            return;
        }

        int predicted;

        if (current > lastAqi) {
            predicted = current + 5;
        } else if (current < lastAqi) {
            predicted = current - 5;
        } else {
            predicted = current;
        }

        tvPrediction.setText("Next Hour Predicted AQI: " + predicted);
    }
}