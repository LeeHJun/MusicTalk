package com.example.myapplication;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

public class SpotifyAuthManager {
    private SpotifyAppRemote mSpotifyAppRemote;
    private static final String CLIENT_ID = "3868f7495e7f40058d0491a4c88c5936";
    public static final String REDIRECT_URI = "myapp://callback";
    private static final String SCOPES = "user-read-private user-read-email";

    public SpotifyAuthManager(Context context) {
        connectToSpotifyRemote(context);
    }

    public void connectToSpotifyRemote(Context context) {
        ConnectionParams connectionParams = new ConnectionParams.Builder(CLIENT_ID)
                .setRedirectUri(REDIRECT_URI)
                .showAuthView(true)
                .build();

        SpotifyAppRemote.connect(context, connectionParams, new Connector.ConnectionListener() {
            @Override
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                mSpotifyAppRemote = spotifyAppRemote;
                Log.d("SpotifyAuthManager", "Spotify connected successfully");
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e("SpotifyAuthManager", "Failed to connect to Spotify", throwable);
            }
        });
    }

    public void disconnect() {
        if (mSpotifyAppRemote != null) {
            SpotifyAppRemote.disconnect(mSpotifyAppRemote);
        }
    }

    public SpotifyAppRemote getSpotifyAppRemote() {
        return mSpotifyAppRemote;
    }

    public String createAuthUrl() {
        return "https://accounts.spotify.com/authorize" +
                "?response_type=code" +
                "&client_id=" + CLIENT_ID +
                "&redirect_uri=" + Uri.encode(REDIRECT_URI) +
                "&scope=" + Uri.encode(SCOPES);
    }
}
