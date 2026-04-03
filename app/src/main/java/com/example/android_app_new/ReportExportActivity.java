package com.example.android_app_new;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.util.ArrayList;

public class ReportExportActivity extends AppCompatActivity {

    private TextView tvHealthRecommendations;
    private Button btnExportCSV, btnExportTXT, btnExportJSON, btnShare, btnBack;
    private ReportExporter reportExporter;

    private String city;
    private ArrayList<AqiTrendData> trendData;
    private int currentAqi;
    private double pm25, pm10, no2, o3;
    private HealthRecommendationEngine.HealthRecommendation healthRec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_export);

        // Get data from intent
        Intent intent = getIntent();
        city = intent.getStringExtra("city");
        currentAqi = intent.getIntExtra("aqi", 0);
        pm25 = intent.getDoubleExtra("pm25", 0);
        pm10 = intent.getDoubleExtra("pm10", 0);
        no2 = intent.getDoubleExtra("no2", 0);
        o3 = intent.getDoubleExtra("o3", 0);

        // Get trend data
        trendData = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            int dayAqi = currentAqi - (i * 5);
            String dayName = "Day " + (7 - i);
            String status = "Moderate";
            double dayPm25 = pm25 - i;
            double dayPm10 = pm10 - i;
            double dayNo2 = no2 - i;
            double dayO3 = o3 - i;

            AqiTrendData data = new AqiTrendData(dayName, dayAqi, status, dayPm25, dayPm10, dayNo2, dayO3);
            trendData.add(data);
        }

        // Initialize views
        tvHealthRecommendations = findViewById(R.id.tvHealthRecommendations);
        btnExportCSV = findViewById(R.id.btnExportCSV);
        btnExportTXT = findViewById(R.id.btnExportTXT);
        btnExportJSON = findViewById(R.id.btnExportJSON);
        btnShare = findViewById(R.id.btnShare);
        btnBack = findViewById(R.id.btnBack);

        reportExporter = new ReportExporter(this);

        // Get health recommendations
        healthRec = HealthRecommendationEngine.getRecommendations(currentAqi, pm25, pm10, no2, o3);

        // Display recommendations
        displayHealthRecommendations();

        // Setup button listeners
        setupButtonListeners();
    }

    private void displayHealthRecommendations() {
        StringBuilder display = new StringBuilder();
        display.append("═══════════════════════════════════════\n");
        display.append("AQI: ").append(currentAqi).append(" (").append(healthRec.severity).append(")\n");
        display.append("═══════════════════════════════════════\n\n");

        display.append("MAIN ADVICE:\n");
        display.append(healthRec.mainAdvice).append("\n\n");

        display.append("POLLUTANT LEVELS:\n");
        display.append(healthRec.pollutantAdvisories).append("\n");

        if (healthRec.riskGroups.length() > 0) {
            display.append("VULNERABLE GROUPS:\n");
            display.append(healthRec.riskGroups).append("\n");
        }

        display.append("PROTECTIVE MEASURES:\n");
        display.append(healthRec.protectiveMeasures).append("\n");

        display.append("ACTIVITY RECOMMENDATIONS:\n");
        display.append(healthRec.activityRecommendations);

        tvHealthRecommendations.setText(display.toString());
    }

    private void setupButtonListeners() {
        btnExportCSV.setOnClickListener(v -> exportAsCSV());
        btnExportTXT.setOnClickListener(v -> exportAsText());
        btnExportJSON.setOnClickListener(v -> exportAsJSON());
        btnShare.setOnClickListener(v -> shareReport());
        btnBack.setOnClickListener(v -> finish());
    }

    private void exportAsCSV() {
        try {
            File csvFile = reportExporter.exportToCSV(city, trendData, currentAqi, pm25, pm10, no2, o3);
            if (csvFile != null) {
                Toast.makeText(this, "CSV exported: " + csvFile.getName(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to export CSV", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void exportAsText() {
        try {
            File txtFile = reportExporter.exportToTextReport(city, trendData, currentAqi, pm25, pm10, no2, o3, healthRec);
            if (txtFile != null) {
                Toast.makeText(this, "Text report exported: " + txtFile.getName(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to export text report", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void exportAsJSON() {
        try {
            File jsonFile = reportExporter.exportToJSON(city, trendData, currentAqi, pm25, pm10, no2, o3);
            if (jsonFile != null) {
                Toast.makeText(this, "JSON exported: " + jsonFile.getName(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to export JSON", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void shareReport() {
        try {
            // Export as text first
            File txtFile = reportExporter.exportToTextReport(city, trendData, currentAqi, pm25, pm10, no2, o3, healthRec);

            if (txtFile != null) {
                Uri fileUri = FileProvider.getUriForFile(
                    this,
                    getApplicationContext().getPackageName() + ".fileprovider",
                    txtFile
                );

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Air Quality Report - " + city);
                shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(Intent.createChooser(shareIntent, "Share Report"));
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error sharing report: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}


