package com.example.myapplication;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

public class SpotifyAuthManager {

    private static final String CLIENT_ID = "a89f11f6968c49cb9d813d9bb2126719";
    public static final String REDIRECT_URI = "myapp://callback";
    private static final String SCOPES = "user-read-private user-read-email";
    private static final String TAG = "SpotifyAuthManager";

    private SpotifyAppRemote mSpotifyAppRemote;

    public SpotifyAuthManager(Context context) {
        connectToSpotifyRemote(context);
    }

    private void connectToSpotifyRemote(Context context) {
        ConnectionParams connectionParams = new ConnectionParams.Builder(CLIENT_ID)
                .setRedirectUri(REDIRECT_URI)
                .showAuthView(true)
                .build();

        SpotifyAppRemote.connect(context, connectionParams, new Connector.ConnectionListener() {
            @Override
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                mSpotifyAppRemote = spotifyAppRemote;
                Log.d(TAG, "Spotify connected successfully");
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "Failed to connect to Spotify", throwable);
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
        return new Uri.Builder()
                .scheme("https")
                .authority("accounts.spotify.com")
                .path("authorize")
                .appendQueryParameter("response_type", "code")
                .appendQueryParameter("client_id", CLIENT_ID)
                .appendQueryParameter("redirect_uri", REDIRECT_URI)
                .appendQueryParameter("scope", SCOPES)
                .build()
                .toString();
    }
}
