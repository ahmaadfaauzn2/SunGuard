package com.example.bletesting.Service;

import com.example.bletesting.Response.UVIndexData;
import com.example.bletesting.Response.UVIndexDataResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface UVDataListService {

    @GET("/api/sensordata")
    Call<List<UVIndexData>> getUVIndexData();  // Adjust to return a List of UVIndexData
}
