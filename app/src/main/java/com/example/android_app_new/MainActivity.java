package com.example.android_app_new;

import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private EditText etCityName;
    private Button btnFetchAqi;
    private TextView tvAqiDisplay;
    private DatabaseHelper dbHelper;

    // ✅ WAQI Base URL
    private static final String BASE_URL = "https://api.waqi.info/";

    // ✅ WAQI API TOKEN
    private static final String API_TOKEN = "87c3349785b993ad86d4b01fa941e94ebaf8f224";

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
                Toast.makeText(this, "Enter city name (e.g., London, Delhi, Mumbai)", Toast.LENGTH_SHORT).show();
                return;
            }

            // Convert city name to lowercase for API
            String cityForApi = city.toLowerCase().replaceAll("\\s+", "");

            if (isNetworkAvailable()) {
                tvAqiDisplay.setText("Fetching data...");
                fetchDataFromApi(cityForApi);
            } else {
                fetchDataFromSqlite(city);
            }

        });
    }

    private void fetchDataFromApi(String city) {

        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiService service = retrofit.create(ApiService.class);

        service.getCityData(city, API_TOKEN).enqueue(new Callback<WaqiResponse>() {

            @Override
            public void onResponse(Call<WaqiResponse> call,
                                   Response<WaqiResponse> response) {

                Log.d("MainActivity", "Response Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {

                    WaqiResponse.Data data = response.body().getData();

                    if (data != null) {

                        int aqi = data.getAqi();
                        String cityName = data.getCity();

                        String info = "City: " + cityName +
                                "\nAQI: " + aqi +
                                "\nPM2.5: " + String.format("%.2f", data.getPm25()) +
                                "\nPM10: " + String.format("%.2f", data.getPm10());

                        tvAqiDisplay.setText(info);

                        dbHelper.insertData(cityName,
                                String.valueOf(aqi));

                    } else {
                        tvAqiDisplay.setText("City not found.");
                    }

                } else {
                    String errorMsg = "API Error: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            errorMsg += "\n" + response.errorBody().string();
                        } catch (Exception e) {
                            Log.e("MainActivity", "Error reading error body", e);
                        }
                    }
                    tvAqiDisplay.setText(errorMsg);
                    Log.e("MainActivity", "API Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<WaqiResponse> call, Throwable t) {
                Log.e("MainActivity", "Network Error: " + t.getMessage(), t);
                tvAqiDisplay.setText("Network Error: " + t.getMessage() +
                    "\n\nMake sure you have internet connection and the API token is valid.");
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
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
                        .append("\nAQI: ")
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