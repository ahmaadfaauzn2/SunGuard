package com.example.bletesting.Response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PredictionResponse {
    @SerializedName("status")
    private String status;
    @SerializedName("predictions")
    private List<Prediction> predictions;
    @SerializedName("message")
    private String message;

    public class Prediction {
        @SerializedName("datetime")
        private String datetime;
        @SerializedName("value")
        private double value;

        // Getters
        public String getDatetime() { return datetime; }
        public double getValue() { return value; }
    }

    // Getters
    public String getStatus() { return status; }
    public List<Prediction> getPredictions() { return predictions; }
    public String getMessage() { return message; }
}
