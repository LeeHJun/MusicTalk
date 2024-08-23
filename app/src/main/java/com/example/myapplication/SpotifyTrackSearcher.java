package com.example.myapplication;

import android.util.Log;

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

public class SpotifyTrackSearcher {

    private static final String TAG = "SpotifyTrackSearcher";
    private static final String SEARCH_URL = "https://api.spotify.com/v1/search?type=track&q=";

    public static void searchTrack(String accessToken, String query, TrackSearchCallback callback) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(SEARCH_URL + query)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Track search request failed", e);
                callback.onFailure("Network error");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBody = response.body().string();
                        List<Track> trackList = parseTracksFromResponse(responseBody);
                        callback.onTracksFound(trackList);
                    } catch (JSONException e) {
                        Log.e(TAG, "Failed to parse track search response", e);
                        callback.onFailure("Parsing error");
                    }
                } else {
                    Log.e(TAG, "Track search failed, response code: " + response.code());
                    callback.onFailure("Search failed");
                }
            }
        });
    }

    private static List<Track> parseTracksFromResponse(String responseBody) throws JSONException {
        List<Track> trackList = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(responseBody);
        JSONArray tracksArray = jsonObject.getJSONObject("tracks").getJSONArray("items");

        for (int i = 0; i < tracksArray.length(); i++) {
            JSONObject trackObject = tracksArray.getJSONObject(i);
            String trackId = trackObject.getString("id");
            String trackName = trackObject.getString("name");
            String artistName = trackObject.getJSONArray("artists").getJSONObject(0).getString("name");
            String imageUrl = trackObject.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");

            Track track = new Track(trackId, trackName, artistName, imageUrl);
            trackList.add(track);
        }

        return trackList;
    }

    public interface TrackSearchCallback {
        void onTracksFound(List<Track> trackList);
        void onFailure(String errorMessage);
    }
}
