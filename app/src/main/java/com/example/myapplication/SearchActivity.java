package com.example.myapplication;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TrackAdapter trackAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recyclerView = findViewById(R.id.results_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        trackAdapter = new TrackAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(trackAdapter);

        // Intent로부터 쿼리 문자열 가져오기
        String query = getIntent().getStringExtra("query");
        if (query != null) {
            searchTracks(query);
        } else {
            Toast.makeText(this, "No search query provided.", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchTracks(String query) {
        // WritelistActivity의 searchSpotify 메서드를 호출하는 부분을 구현합니다.
        // 여기에 API 호출 코드를 추가해야 합니다.
    }

    public void updateTrackList(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray tracksArray = jsonObject.getJSONObject("tracks").getJSONArray("items");

            List<Track> tracks = new ArrayList<>();
            for (int i = 0; i < tracksArray.length(); i++) {
                JSONObject trackObject = tracksArray.getJSONObject(i);
                String id = trackObject.getString("id");
                String name = trackObject.getString("name");
                String artist = trackObject.getJSONArray("artists").getJSONObject(0).getString("name");
                String imageUrl = trackObject.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");

                tracks.add(new Track(id, name, artist, imageUrl));
            }

            trackAdapter.trackList.clear();
            trackAdapter.trackList.addAll(tracks);
            trackAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to parse search results.", Toast.LENGTH_SHORT).show();
        }
    }
}
