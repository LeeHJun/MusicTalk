package com.example.myapplication;

import android.net.Uri;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SpotifyTrackSearcher {
    private static final String SPOTIFY_API_URL = "https://api.spotify.com/v1/search";
    private static final String SEARCH_TYPE = "track";

    private OkHttpClient client = new OkHttpClient();

    public void searchTrack(String query, String accessToken) {
        String url = SPOTIFY_API_URL + "?q=" + Uri.encode(query) + "&type=" + SEARCH_TYPE;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // Handle failure
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    // Parse the response data to extract track info
                    // Example: use a JSON library to parse the response
                } else {
                    // Handle unsuccessful response
                }
            }
        });
    }
}
