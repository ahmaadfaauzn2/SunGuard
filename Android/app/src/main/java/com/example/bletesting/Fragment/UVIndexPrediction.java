package com.example.bletesting.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bletesting.ApiClient.ApiClient;
import com.example.bletesting.Adapter.UVIndexPredictionAdapter;
import com.example.bletesting.R;
import com.example.bletesting.Response.PredictionRequest;
import com.example.bletesting.Response.PredictionResponse;
import com.example.bletesting.Service.ApiInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UVIndexPrediction extends Fragment {
    private RecyclerView recyclerView;
    private UVIndexPredictionAdapter adapter;

    public UVIndexPrediction() {
        // Required empty public constructor
    }

    public static UVIndexPrediction newInstance(String param1, String param2) {
        UVIndexPrediction fragment = new UVIndexPrediction();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Handle any arguments if needed
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_uv_index_prediction, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.rvPrediction);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Fetch predictions from API
        fetchARIMAPredictions();

        return view;
    }

    private void fetchARIMAPredictions() {
        ApiInterface service = ApiClient.getClient("https://bison-amused-minnow.ngrok-free.app/arima/prediction/").create(ApiInterface.class);

        // Example parameters, replace with actual values if needed
        PredictionRequest request = new PredictionRequest(4, 8);

        service.getPredictions(request).enqueue(new Callback<PredictionResponse>() {
            @Override
            public void onResponse(Call<PredictionResponse> call, Response<PredictionResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PredictionResponse predictionResponse = response.body();
                    List<PredictionResponse.Prediction> predictions = predictionResponse.getPredictions();
                    adapter = new UVIndexPredictionAdapter(predictions);
                    recyclerView.setAdapter(adapter);
                } else {
                    Log.e("API Error", "Response Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<PredictionResponse> call, Throwable t) {
                Log.e("API Error", "Network Failure: ", t);
            }
        });
    }
}
