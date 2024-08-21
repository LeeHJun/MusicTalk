package com.example.myapplication;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SpotifyOAuthManager {
    private static final String CLIENT_ID = "3868f7495e7f40058d0491a4c88c5936";
    private static final String CLIENT_SECRET = "278f273fb3bd4f0c94b3832ee1f0578d";
    private static final String REDIRECT_URI = "myapp://callback";
    private static SpotifyOAuthManager instance;

    private String accessToken; // 액세스 토큰 저장 변수

    private SpotifyOAuthManager() {
        // Singleton 패턴
    }

    public static SpotifyOAuthManager getInstance() {
        if (instance == null) {
            instance = new SpotifyOAuthManager();
        }
        return instance;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void requestAccessToken(String code) {
        OkHttpClient client = new OkHttpClient();

        FormBody formBody = new FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("code", code)
                .add("redirect_uri", REDIRECT_URI)
                .add("client_id", CLIENT_ID)
                .add("client_secret", CLIENT_SECRET)
                .build();

        Request request = new Request.Builder()
                .url("https://accounts.spotify.com/api/token")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("SpotifyOAuthManager", "Failed to request access token: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        accessToken = jsonObject.getString("access_token");
                        Log.d("SpotifyOAuthManager", "Access token obtained successfully");
                    } catch (JSONException e) {
                        Log.e("SpotifyOAuthManager", "Failed to parse access token response: " + e.getMessage());
                    }
                } else {
                    Log.e("SpotifyOAuthManager", "Failed to obtain access token");
                }
            }
        });
    }
}
