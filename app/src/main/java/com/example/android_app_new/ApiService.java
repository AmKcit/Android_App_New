package com.example.android_app_new;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiService {

    // Used by DashboardActivity (fetches by token only)
    @GET("feed/here/")
    Call<AqiResponse> getLocalAqi(@Query("token") String token);

    // Used by MainActivity (fetches by a dynamic URL)
    @GET
    Call<AqiResponse> getAqiByUrl(@Url String url);
}