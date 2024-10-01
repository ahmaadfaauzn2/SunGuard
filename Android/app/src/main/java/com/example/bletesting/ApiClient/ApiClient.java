package com.example.bletesting.ApiClient;



import com.example.bletesting.DateDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {
//    private static Retrofit retrofit = null;
//
//    // General method to get client for any URL
//    public static Retrofit getClient(String baseUrl) {
//        if (retrofit == null || !retrofit.baseUrl().toString().equals(baseUrl)) {
//            retrofit = new Retrofit.Builder()
//                    .baseUrl(baseUrl)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//        }
//        return retrofit;
//    }
private static Retrofit retrofit = null;

    // General method to get client for any URL
    public static Retrofit getClient(String baseUrl) {
        if (retrofit == null || !retrofit.baseUrl().toString().equals(baseUrl)) {
            // Set up Gson with the custom DateDeserializer
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, new DateDeserializer()) // Use the custom DateDeserializer
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson)) // Pass the custom Gson instance
                    .build();
        }
        return retrofit;
    }
}
//}