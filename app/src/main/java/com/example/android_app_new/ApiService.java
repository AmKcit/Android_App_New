package com.example.android_app_new;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("feed/{city}/")
    Call<AqiResponse> getCityAqi(
            @Path("city") String city,
            @Query("token") String token
    );
}