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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private EditText etCityName;
    private Button btnFetchAqi;
    private TextView tvAqiDisplay;
    private DatabaseHelper dbHelper;

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
            if (!city.isEmpty()) {
                if (isNetworkAvailable()) {
                    fetchDataFromApi(city);
                } else {
                    fetchDataFromSqlite(city);
                }
            }
        });
    }

    private void fetchDataFromApi(String city) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.waqi.info/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        // Replace 'demo' with your real API token from aqicn.org
        String url = "feed/" + city + "/?token=demo";

        apiService.getAqiByUrl(url).enqueue(new Callback<AqiResponse>() {
            @Override
            public void onResponse(Call<AqiResponse> call, Response<AqiResponse> response) {
                if (response.isSuccessful() && response.body() != null && "ok".equals(response.body().status)) {
                    int aqi = response.body().data.aqi;
                    String info = "City: " + response.body().data.city.name + "\nAQI: " + aqi;
                    tvAqiDisplay.setText(info);

                    // Save to SQLite for offline use
                    dbHelper.insertData(response.body().data.city.name, String.valueOf(aqi));
                } else {
                    tvAqiDisplay.setText("City not found.");
                }
            }

            @Override
            public void onFailure(Call<AqiResponse> call, Throwable t) {
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
                data.append("City: ").append(cursor.getString(1)).append("\nAQI: ").append(cursor.getString(2)).append("\n\n");
                found = true;
            }
        }

        if (found) {
            tvAqiDisplay.setText(data.toString());
        } else {
            tvAqiDisplay.setText("No offline data for this city.");
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}