package com.example.bletesting.Response;

import com.google.gson.annotations.SerializedName;

public class PredictionRequest {
    @SerializedName("start")
    private int start;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    @SerializedName("end")
    private int end;

    public PredictionRequest(int start, int end) {
        this.start = start;
        this.end = end;
    }

    // Getters and setters
}
