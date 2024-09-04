package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class SearchActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TrackAdapter trackAdapter;
    private OkHttpClient httpClient;
    private EditText searchInput;
    private Button searchButton;
    private static final String LAST_FM_API_KEY = "9c9a029f67b2d59fec887a788eda9ef2"; // 여기에 Last.fm API 키를 입력하세요
    private static final String LAST_FM_API_URL = "http://ws.audioscrobbler.com/2.0/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recyclerView = findViewById(R.id.results_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        trackAdapter = new TrackAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(trackAdapter);

        searchInput = findViewById(R.id.search_input);
        searchButton = findViewById(R.id.search_button);

        httpClient = new OkHttpClient();

        searchButton.setOnClickListener(v -> performSearch());
    }

    private void performSearch() {
        String query = searchInput.getText().toString().trim();
        if (query.isEmpty()) {
            Toast.makeText(this, "Please enter a search query.", Toast.LENGTH_SHORT).show();
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
                runOnUiThread(() -> Toast.makeText(SearchActivity.this, "Search failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    runOnUiThread(() -> updateTrackList(responseBody));
                } else {
                    runOnUiThread(() -> Toast.makeText(SearchActivity.this, "Search failed with code: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void updateTrackList(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONObject resultsObject = jsonObject.getJSONObject("results");
            JSONArray trackArray = resultsObject.getJSONObject("trackmatches").getJSONArray("track");

            List<Track> tracks = new ArrayList<>();
            for (int i = 0; i < trackArray.length(); i++) {
                JSONObject trackObject = trackArray.getJSONObject(i);
                String id = trackObject.getString("url"); // ID는 URL로 대체
                String name = trackObject.getString("name");
                String artist = trackObject.getString("artist");

                // 이미지가 없는 경우를 대비해 이미지의 크기를 큰 것부터 작은 순으로 가져옵니다.
                String imageUrl = getImageUrl(trackObject.getJSONArray("image"));

                tracks.add(new Track(id, name, artist, imageUrl));
            }

            runOnUiThread(() -> {
                trackAdapter.trackList.clear();
                trackAdapter.trackList.addAll(tracks);
                trackAdapter.notifyDataSetChanged();
            });
        } catch (JSONException e) {
            runOnUiThread(() -> Toast.makeText(this, "Failed to parse search results: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } catch (Exception e) {
            runOnUiThread(() -> Toast.makeText(this, "An unexpected error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private String getImageUrl(JSONArray imageArray) {
        for (int j = imageArray.length() - 1; j >= 0; j--) {
            try {
                String imageUrl = imageArray.getJSONObject(j).getString("#text");
                if (!imageUrl.isEmpty()) {
                    return imageUrl;
                }
            } catch (JSONException e) {
                // 이미지 URL 파싱 중 예외 발생 시 무시
            }
        }
        return "";
    }
}
