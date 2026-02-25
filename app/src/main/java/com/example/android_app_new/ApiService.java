package com.example.android_app_new;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ApiService {

    @GET("latest")
    Call<OpenAqResponse> getLatestData(
            @Header("X-API-Key") String apiKey,
            @Query("country") String country,
            @Query("city") String city,
            @Query("limit") int limit
    );
}