package com.example.bletesting.Response;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class UVIndexData {

    @SerializedName("timestamp")
    private Date timestamp;

    @SerializedName("uv_index")
    private Float uvIndex;  // Use Float if it can be null, otherwise use float

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Float getUVIndex() {
        return uvIndex;
    }

    public void setUVIndex(Float uvIndex) {
        this.uvIndex = uvIndex;
    }

}
