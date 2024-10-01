package com.example.bletesting.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.bletesting.R;
import com.example.bletesting.Response.UVIndexData;

import java.util.List;



public class UVIndexAdapter extends RecyclerView.Adapter<UVIndexAdapter.ViewHolder> {
    private List<UVIndexData> uvDataList;

    public UVIndexAdapter(List<UVIndexData> uvDataList) {
        this.uvDataList = uvDataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.uv_index_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UVIndexData data = uvDataList.get(position);
        holder.uvIndex.setText(String.format("UV Index: %s", data.getUVIndex() != null ? data.getUVIndex() : "N/A"));
        holder.dateTime.setText(String.format("Date: %s", data.getTimestamp() != null ? data.getTimestamp().toString() : "N/A"));
    }

    @Override
    public int getItemCount() {
        return uvDataList != null ? uvDataList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView uvIndex, dateTime;

        public ViewHolder(View itemView) {
            super(itemView);
            uvIndex = itemView.findViewById(R.id.uvIndex);
            dateTime = itemView.findViewById(R.id.dateTime);
        }
    }
}


