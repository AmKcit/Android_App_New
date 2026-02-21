package com.example.android_app_new;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    private Spinner citySpinner;
    private WebView webView;

    private final String API_TOKEN = "87c3349785b993ad86d4b01fa941e94ebaf8f224";

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

        citySpinner = findViewById(R.id.citySpinner);
        webView = findViewById(R.id.webViewAqi);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item,
                        cities);

        citySpinner.setAdapter(adapter);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        citySpinner.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(android.widget.AdapterView<?> parent,
                                               android.view.View view,
                                               int position,
                                               long id) {

                        loadWidget(cities[position]);
                    }

                    @Override
                    public void onNothingSelected(android.widget.AdapterView<?> parent) {}
                });
    }

    private void loadWidget(String city) {

        String url = "https://widget.waqi.info/widget/?token="
                + API_TOKEN
                + "&city="
                + city;

        webView.loadUrl(url);
    }
}