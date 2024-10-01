package com.example.bletesting.Service;

import com.example.bletesting.Response.PredictionRequest;
import com.example.bletesting.Response.PredictionResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {
    @POST("/api/arima/predict")
    Call<PredictionResponse> getPredictions(@Body PredictionRequest request);
}
