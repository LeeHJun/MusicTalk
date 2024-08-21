package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class WritelistActivity extends AppCompatActivity {

    private static final int SPOTIFY_REQUEST_CODE = 1337;
    private static final int REQUEST_CODE = 101; // 권한 요청 코드
    private static final int SEARCH_REQUEST_CODE = 200; // 검색 요청 코드

    private ImageView albumImage;
    private TextView musicTitle, musicArtist;
    private EditText postContent;

    private SpotifyAuthManager spotifyAuthManager;
    private String selectedTrackId;
    private String accessToken;

    // Firebase Database reference
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.writelist);

        albumImage = findViewById(R.id.album_image);
        musicTitle = findViewById(R.id.music_title);
        musicArtist = findViewById(R.id.music_artist);
        postContent = findViewById(R.id.post_content);

        spotifyAuthManager = new SpotifyAuthManager(this);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("spotyfi_post");

        // 디렉토리 생성
        createDirectory();

        // 권한 요청
        checkPermissions();
    }

    public void onSearchTrack(View view) {
        if (accessToken == null || accessToken.isEmpty()) {
            Toast.makeText(this, "Access token is missing. Please log in again.", Toast.LENGTH_SHORT).show();
            startSpotifyAuth(view);
            return;
        }

        String query = postContent.getText().toString().trim();
        if (query.isEmpty()) {
            Toast.makeText(this, "Please enter a track name.", Toast.LENGTH_SHORT).show();
            return;
        }

        searchSpotify(query);
    }

    public void searchSpotify(String query) {
        // 예시: Spotify 검색 API 호출 - 실제 API 호출 코드 추가 필요
        // SpotifyAPI.searchTracks(query, accessToken, new Callback<TrackResponse>() { ... });
        // 위 코드를 작성할 때, 실제 API 호출 방법에 맞게 수정해야 합니다.
        // For example:
        // SpotifyAPI.searchTracks(query, accessToken, new Callback<TrackResponse>() {
        //    @Override
        //    public void onResponse(Call<TrackResponse> call, Response<TrackResponse> response) {
        //        if (response.isSuccessful() && response.body() != null) {
        //            // handle successful response
        //            updateTrackListUI(response.body().getTracks());
        //        }
        //    }
        //    @Override
        //    public void onFailure(Call<TrackResponse> call, Throwable t) {
        //        // handle failure
        //    }
        // });
    }

    public void onPost(View view) {
        String content = postContent.getText().toString().trim();

        if (selectedTrackId == null || selectedTrackId.isEmpty()) {
            Toast.makeText(this, "Please select a track.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (content.isEmpty()) {
            Toast.makeText(this, "Please enter post content.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase Database에 게시물 데이터 저장
        String postId = databaseReference.push().getKey();
        Post1 post = new Post1(selectedTrackId, content);
        if (postId != null) {
            databaseReference.child(postId).setValue(post)
                    .addOnSuccessListener(aVoid -> Toast.makeText(WritelistActivity.this, "Post created successfully.", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(WritelistActivity.this, "Failed to create post.", Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPOTIFY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null && uri.toString().startsWith(SpotifyAuthManager.REDIRECT_URI)) {
                String code = uri.getQueryParameter("code");
                if (code != null) {
                    SpotifyOAuthManager.getInstance().requestAccessToken(code);
                    accessToken = SpotifyOAuthManager.getInstance().getAccessToken(); // 액세스 토큰 저장
                }
            }
        } else if (requestCode == SEARCH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedTrackId = data.getStringExtra("trackId");
            String trackName = data.getStringExtra("trackName");
            String artistName = data.getStringExtra("artistName");
            String imageUrl = data.getStringExtra("imageUrl");

            if (selectedTrackId != null && !selectedTrackId.isEmpty() &&
                    trackName != null && !trackName.isEmpty() &&
                    artistName != null && !artistName.isEmpty() &&
                    imageUrl != null && !imageUrl.isEmpty()) {
                musicTitle.setText(trackName);
                musicArtist.setText(artistName);
                Picasso.get().load(imageUrl).into(albumImage);
            } else {
                Toast.makeText(this, "Failed to retrieve track information.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createDirectory() {
        File directory = new File(getExternalFilesDir(null), "myAppDirectory");
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용되었으므로 필요한 작업 수행
            } else {
                // 권한이 거부된 경우, 사용자가 수동으로 설정을 변경하도록 안내
                Toast.makeText(this, "Permission denied. You can enable it in settings.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }
    }

    private void openSpotifyTrack() {
        if (selectedTrackId != null && !selectedTrackId.isEmpty()) {
            String spotifyUri = "spotify:track:" + selectedTrackId;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(spotifyUri));
            startActivity(intent);
        } else {
            Toast.makeText(this, "No track selected.", Toast.LENGTH_SHORT).show();
        }
    }

    // Spotify 인증을 시작하는 메서드
    public void startSpotifyAuth(View view) {
        String authUrl = spotifyAuthManager.createAuthUrl();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
        startActivityForResult(intent, SPOTIFY_REQUEST_CODE);
    }

    // 트랙 목록 UI를 업데이트하는 메서드
    public void updateTrackListUI(List<Track> trackList) {
        RecyclerView recyclerView = findViewById(R.id.results_list); // RecyclerView를 XML에서 찾습니다.
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        TrackAdapter adapter = new TrackAdapter(this, trackList);
        recyclerView.setAdapter(adapter);
    }
}
