package com.example.android_app_new;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminSettingsActivity extends AppCompatActivity {

    private Switch swNotifications, swMaintenanceMode;
    private TextView tvSettingsTitle;
    private Button btnSave, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_settings);

        // Initialize views
        tvSettingsTitle = findViewById(R.id.tvSettingsTitle);
        swNotifications = findViewById(R.id.swNotifications);
        swMaintenanceMode = findViewById(R.id.swMaintenanceMode);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        // Set up button listeners
        btnSave.setOnClickListener(v -> saveSettings());
        btnCancel.setOnClickListener(v -> finish());

        // Load current settings
        loadSettings();
    }

    private void loadSettings() {
        // TODO: Load settings from SharedPreferences or database
        swNotifications.setChecked(true);
        swMaintenanceMode.setChecked(false);
    }

    private void saveSettings() {
        // TODO: Save settings to SharedPreferences or database
        Toast.makeText(this, "Settings saved successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}

