package com.example.bletesting.Service;

import com.example.bletesting.Response.UVIndexResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UVIndexService {
    @GET("api/v1/uvi")
    Call<UVIndexResponse> getCurrentUvIndex
            (@Query("latitude") double latitude,
             @Query("longitude") double longitude);


}
