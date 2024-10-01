package com.example.bletesting.Service;

import com.example.bletesting.SensorData;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


// Body API Diubah dari SensorData menjadi Map<String, String>
public interface SensorService {
    @POST("/api/sensordata")
//    Call<Void> sendSensorData(@Body SensorData sensorData);
    Call<Void> sendSensorData(@Body Map<String, String> data);



}
