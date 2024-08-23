package com.example.myapplication;

import android.content.Context;
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

    private static final String CLIENT_ID = "a89f11f6968c49cb9d813d9bb2126719";
    private static final String CLIENT_SECRET = "f71b0f0ab7b24ff1a88907ec957b5d0b";
    private static final String REDIRECT_URI = "myapp://callback";
    private static final String TOKEN_URL = "https://accounts.spotify.com/api/token";
    private static final String AUTH_URL = "https://accounts.spotify.com/authorize";
    private static final String TAG = "SpotifyOAuthManager";

    private static SpotifyOAuthManager instance;
    private Context context;
    private String accessToken;

    private SpotifyOAuthManager(Context context) {
        this.context = context.getApplicationContext(); // ApplicationContext를 사용
    }

    public static synchronized SpotifyOAuthManager getInstance(Context context) {
        if (instance == null) {
            instance = new SpotifyOAuthManager(context);
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
                .url(TOKEN_URL)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to request access token", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    handleTokenResponse(response.body().string());
                } else {
                    Log.e(TAG, "Failed to obtain access token, response code: " + response.code());
                }
            }
        });
    }

    private void handleTokenResponse(String responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody);
            accessToken = jsonObject.getString("access_token");
            Log.d(TAG, "Access token obtained successfully");
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse access token response", e);
        }
    }

    public String createAuthUrl() {
        // Spotify Authorization URL 생성
        return AUTH_URL + "?client_id=" + CLIENT_ID +
                "&response_type=code" +
                "&redirect_uri=" + REDIRECT_URI +
                "&scope=user-read-private user-read-email";
    }
}
