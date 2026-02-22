package com.example.android_app_new;

import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private EditText etCityName;
    private Button btnFetchAqi;
    private TextView tvAqiDisplay;
    private DatabaseHelper dbHelper;

    // ✅ OpenAQ Base URL
    private static final String BASE_URL = "https://api.openaq.org/v2/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCityName = findViewById(R.id.etCityName);
        btnFetchAqi = findViewById(R.id.btnFetchAqi);
        tvAqiDisplay = findViewById(R.id.tvAqiDisplay);
        dbHelper = new DatabaseHelper(this);

        btnFetchAqi.setOnClickListener(v -> {

            String city = etCityName.getText().toString().trim();

            if (city.isEmpty()) {
                Toast.makeText(this, "Enter city name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isNetworkAvailable()) {
                fetchDataFromApi(city);
            } else {
                fetchDataFromSqlite(city);
            }
        });
    }

    private void fetchDataFromApi(String city) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);

        // ✅ OpenAQ Call
        service.getLatestData(city, 1).enqueue(new Callback<OpenAqResponse>() {

            @Override
            public void onResponse(Call<OpenAqResponse> call,
                                   Response<OpenAqResponse> response) {

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().getResults() != null
                        && !response.body().getResults().isEmpty()) {

                    OpenAqResponse.Result result =
                            response.body().getResults().get(0);

                    String cityName = result.getCity();

                    double pm25Value = -1;

                    for (OpenAqResponse.Measurement m :
                            result.getMeasurements()) {

                        if (m.getParameter().equalsIgnoreCase("pm25")) {
                            pm25Value = m.getValue();
                            break;
                        }
                    }

                    if (pm25Value != -1) {

                        String info = "City: " + cityName +
                                "\nPM2.5: " + pm25Value + " µg/m³";

                        tvAqiDisplay.setText(info);

                        dbHelper.insertData(cityName,
                                String.valueOf(pm25Value));

                    } else {
                        tvAqiDisplay.setText("PM2.5 data not available.");
                    }

                } else {
                    tvAqiDisplay.setText("City not found.");
                }
            }

            @Override
            public void onFailure(Call<OpenAqResponse> call, Throwable t) {
                tvAqiDisplay.setText("Error: " + t.getMessage());
            }
        });
    }

    private void fetchDataFromSqlite(String city) {

        Toast.makeText(this, "Offline Mode", Toast.LENGTH_SHORT).show();

        Cursor cursor = dbHelper.getAllData();
        StringBuilder data = new StringBuilder("Last Cached Results:\n\n");
        boolean found = false;

        while (cursor.moveToNext()) {

            if (cursor.getString(1).toLowerCase().contains(city.toLowerCase())) {

                data.append("City: ")
                        .append(cursor.getString(1))
                        .append("\nPM2.5: ")
                        .append(cursor.getString(2))
                        .append("\n\n");

                found = true;
            }
        }

        if (found) {
            tvAqiDisplay.setText(data.toString());
        } else {
            tvAqiDisplay.setText("No offline data for this city.");
        }

        cursor.close();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}