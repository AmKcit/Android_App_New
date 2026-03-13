package com.example.android_app_new;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AqiCalculatorActivity extends AppCompatActivity {

    private TextInputEditText etPm25, etPm10;
    private MaterialButton btnCalculate;
    private CardView resultCard;
    private LinearLayout resultBackground;

    private TextView tvCalculatedAqi;
    private TextView tvCalculatedStatus;
    private TextView tvAqiLabel; // "Overall AQI" label

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aqi_calculator);

        etPm25 = findViewById(R.id.etPm25);
        etPm10 = findViewById(R.id.etPm10);
        btnCalculate = findViewById(R.id.btnCalculate);

        resultCard = findViewById(R.id.resultCard);
        resultBackground = findViewById(R.id.resultBackground);

        tvCalculatedAqi = findViewById(R.id.tvCalculatedAqi);
        tvCalculatedStatus = findViewById(R.id.tvCalculatedStatus);

        // Add this TextView in XML if not present
        tvAqiLabel = findViewById(R.id.tvAqiLabel);

        btnCalculate.setOnClickListener(v -> calculateAndDisplay());
    }

    private void calculateAndDisplay() {

        String pm25Str = etPm25.getText().toString().trim();
        String pm10Str = etPm10.getText().toString().trim();

        if (pm25Str.isEmpty() || pm10Str.isEmpty()) {
            Toast.makeText(this, "Please enter both PM2.5 and PM10 values", Toast.LENGTH_SHORT).show();
            return;
        }

        double pm25, pm10;

        try {
            pm25 = Double.parseDouble(pm25Str);
            pm10 = Double.parseDouble(pm10Str);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
            return;
        }

        int aqi = AQICalculator.calculateOverallAQI(pm25, pm10);
        String status = AQICalculator.getAQICategory(aqi);

        tvCalculatedAqi.setText(String.valueOf(aqi));
        tvCalculatedStatus.setText(status);

        setResultColor(aqi);

        resultCard.setVisibility(View.VISIBLE);
    }

    private void setResultColor(int aqi) {

        int color;

        if (aqi <= 50)
            color = Color.parseColor("#4CAF50"); // Green
        else if (aqi <= 100)
            color = Color.parseColor("#FFEB3B"); // Yellow
        else if (aqi <= 150)
            color = Color.parseColor("#FF9800"); // Orange
        else if (aqi <= 200)
            color = Color.parseColor("#F44336"); // Red
        else if (aqi <= 300)
            color = Color.parseColor("#9C27B0"); // Purple
        else
            color = Color.parseColor("#800000"); // Maroon

        resultBackground.setBackgroundColor(color);

        int textColor = (aqi > 50 && aqi <= 100) ? Color.BLACK : Color.WHITE;

        tvCalculatedAqi.setTextColor(textColor);
        tvCalculatedStatus.setTextColor(textColor);

        if (tvAqiLabel != null)
            tvAqiLabel.setTextColor(textColor);
    }
}