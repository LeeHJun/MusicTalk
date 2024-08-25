package com.example.myapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.Track1;
import com.example.myapplication.TrackAdapter1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Frag4 extends Fragment {

    private RecyclerView recyclerView;
    private TrackAdapter1 trackAdapter;
    private OkHttpClient httpClient;
    private EditText searchInput;
    private Button searchButton;
    private static final String LAST_FM_API_KEY = "9c9a029f67b2d59fec887a788eda9ef2"; // 여기에 Last.fm API 키를 입력하세요
    private static final String LAST_FM_API_URL = "http://ws.audioscrobbler.com/2.0/";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_search, container, false);

        recyclerView = view.findViewById(R.id.results_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        trackAdapter = new TrackAdapter1(getContext(), new ArrayList<>());
        recyclerView.setAdapter(trackAdapter);

        searchInput = view.findViewById(R.id.search_input);
        searchButton = view.findViewById(R.id.search_button);

        httpClient = new OkHttpClient();

        searchButton.setOnClickListener(v -> performSearch());

        return view;
    }

    private void performSearch() {
        String query = searchInput.getText().toString().trim();
        if (query.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a search query.", Toast.LENGTH_SHORT).show();
            return;
        }

        searchTracks(query);
    }

    private void searchTracks(String query) {
        String url = LAST_FM_API_URL + "?method=track.search&track=" + query + "&api_key=" + LAST_FM_API_KEY + "&format=json";
        Request request = new Request.Builder()
                .url(url)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Search failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    getActivity().runOnUiThread(() -> updateTrackList(responseBody));
                } else {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Search failed with code: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void updateTrackList(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONObject resultsObject = jsonObject.getJSONObject("results");
            JSONArray trackArray = resultsObject.getJSONObject("trackmatches").getJSONArray("track");

            List<Track1> tracks = new ArrayList<>();
            for (int i = 0; i < trackArray.length(); i++) {
                JSONObject trackObject = trackArray.getJSONObject(i);
                String id = trackObject.getString("url"); // URL로 ID를 설정
                String name = trackObject.getString("name");
                String artist = trackObject.getString("artist");

                String imageUrl = getImageUrl(trackObject.getJSONArray("image"));

                tracks.add(new Track1(id, name, artist, imageUrl));
            }

            trackAdapter.trackList.clear();
            trackAdapter.trackList.addAll(tracks);
            trackAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Failed to parse search results: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } catch (Exception e) {
            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "An unexpected error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private String getImageUrl(JSONArray imageArray) {
        for (int j = imageArray.length() - 1; j >= 0; j--) {
            try {
                String imageUrl = imageArray.getJSONObject(j).getString("#text");
                if (!imageUrl.isEmpty()) {
                    return imageUrl; // 첫 번째로 유효한 이미지 URL을 반환
                }
            } catch (JSONException e) {
                // 이미지 URL 파싱 중 예외 발생 시 무시
            }
        }
        return ""; // 유효한 이미지 URL이 없는 경우 빈 문자열 반환
    }
}
