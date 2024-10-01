package com.example.bletesting.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.bletesting.R;
import com.example.bletesting.Response.PredictionResponse;

import java.util.List;

public class UVIndexPredictionAdapter extends RecyclerView.Adapter<UVIndexPredictionAdapter.ViewHolder> {
    private List<PredictionResponse.Prediction> predictionList;

    public UVIndexPredictionAdapter(List<PredictionResponse.Prediction> predictionList) {
        this.predictionList = predictionList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.uv_index_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PredictionResponse.Prediction prediction = predictionList.get(position);
        holder.predictionDateTime.setText(String.format("Tanggal dan Jam %s", prediction.getDatetime()));
        holder.predictedUVIndex.setText(String.format("Hasil Prediksi UV Index : %.2f", prediction.getValue()));
        // You can set confidence level if provided, here just an example:
//        holder.confidenceLevel.setText("Confidence Level: N/A"); // Replace with actual value if available
    }

    @Override
    public int getItemCount() {
        return predictionList != null ? predictionList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView predictionDateTime, predictedUVIndex, confidenceLevel;

        public ViewHolder(View itemView) {
            super(itemView);
            predictionDateTime = itemView.findViewById(R.id.predictionDateTime);
            predictedUVIndex = itemView.findViewById(R.id.predictedUVIndex);
//            confidenceLevel = itemView.findViewById(R.id.confidenceLevel);
        }
    }
}
