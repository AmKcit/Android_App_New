package com.example.android_app_new;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvAqiValue, tvPrediction, tvAqiStatus;
    // IMPORTANT: Put your actual token here from waqi.info email
    private final String API_TOKEN = "YOUR_ACTUAL_TOKEN_HERE";
    private int lastStoredAqi = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This line ensures you see the NEW screen design
        setContentView(R.layout.activity_dashboard);

        tvAqiValue = findViewById(R.id.tvAqiValue);
        tvPrediction = findViewById(R.id.tvPrediction);
        tvAqiStatus = findViewById(R.id.tvAqiStatus);

        fetchRealTimeAqi();
    }

    private void fetchRealTimeAqi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.waqi.info/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);

        service.getLocalAqi(API_TOKEN).enqueue(new Callback<AqiResponse>() {
            @Override
            public void onResponse(Call<AqiResponse> call, Response<AqiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int currentAqi = response.body().getData().getAqi();
                    updateUI(currentAqi);
                }
            }

            @Override
            public void onFailure(Call<AqiResponse> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(int currentAqi) {
        tvAqiValue.setText(String.valueOf(currentAqi));

        if (currentAqi <= 50) {
            tvAqiStatus.setText("EXCELLENT");
            tvAqiStatus.setTextColor(Color.GREEN);
        } else if (currentAqi <= 100) {
            tvAqiStatus.setText("MODERATE");
            tvAqiStatus.setTextColor(Color.parseColor("#FFD700")); // Solid Yellow/Gold
        } else {
            tvAqiStatus.setText("POOR");
            tvAqiStatus.setTextColor(Color.RED);
        }

        showPrediction(currentAqi, lastStoredAqi);
    }

    private void showPrediction(int current, int previous) {
        if (current < previous) {
            tvPrediction.setText("Trend: Improving! Air is getting cleaner.");
        } else if (current > previous) {
            tvPrediction.setText("Trend: Worsening. Expect higher pollution levels.");
        } else {
            tvPrediction.setText("Trend: Stable conditions expected.");
        }
    }
}