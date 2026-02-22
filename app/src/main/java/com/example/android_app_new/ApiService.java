package com.example.android_app_new;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("latest")
    Call<OpenAqResponse> getLatestData(
            @Query("country") String country,
            @Query("limit") int limit
    );
}