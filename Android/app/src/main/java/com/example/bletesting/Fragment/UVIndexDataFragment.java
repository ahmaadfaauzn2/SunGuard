package com.example.bletesting.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bletesting.Adapter.UVIndexAdapter;
import com.example.bletesting.ApiClient.ApiClient;
import com.example.bletesting.R;
import com.example.bletesting.Response.UVIndexData;
import com.example.bletesting.Service.UVDataListService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UVIndexDataFragment extends Fragment {
    private RecyclerView recyclerView;
    private UVIndexAdapter adapter;

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_uv_index_data, container, false);
        recyclerView = view.findViewById(R.id.UVIndexDataRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fetchUVIndexData();
        return view;
    }

    private void fetchUVIndexData() {
        UVDataListService service = ApiClient.getClient("https://bison-amused-minnow.ngrok-free.app/api/sensordata/").create(UVDataListService.class);
        service.getUVIndexData().enqueue(new Callback<List<UVIndexData>>() {
            @Override
            public void onResponse(Call<List<UVIndexData>> call, Response<List<UVIndexData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API Response", "Data received: " + response.body().toString());
                    adapter = new UVIndexAdapter(response.body());
                    recyclerView.setAdapter(adapter);
                } else {
                    Log.e("API Error", "Response Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<UVIndexData>> call, Throwable t) {
                Log.e("API Error", "Network Failure: ", t);
            }
        });
    }
}



 // https://b352-180-244-128-185.ngrok-free.app/api/sensordata/