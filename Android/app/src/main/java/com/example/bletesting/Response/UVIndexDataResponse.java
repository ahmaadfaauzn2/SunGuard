package com.example.bletesting.Response;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UVIndexDataResponse {
    @SerializedName("data")
    private List<UVIndexData> dataList;

    public List<UVIndexData> getDataList() {
        return dataList;
    }

    public void setDataList(List<UVIndexData> dataList) {
        this.dataList = dataList;
    }
}
