package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class WritelistActivity extends AppCompatActivity {

    private static final int SEARCH_REQUEST_CODE = 200; // REQUEST_CODE 정의

    private ImageView albumImage;
    private TextView musicTitle, musicArtist;
    private EditText postContent;
    private Button searchButton, postButton;

    private String selectedTrackId;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.writelist);

        initializeUI();
        databaseReference = FirebaseDatabase.getInstance().getReference("spotify_post"); // Firebase Database 참조

        searchButton.setOnClickListener(v -> openSearchActivity());
        postButton.setOnClickListener(v -> onPost(v));
    }

    private void initializeUI() {
        albumImage = findViewById(R.id.album_image);
        musicTitle = findViewById(R.id.music_title);
        musicArtist = findViewById(R.id.music_artist);
        postContent = findViewById(R.id.post_content);
        searchButton = findViewById(R.id.search_button);
        postButton = findViewById(R.id.post_button);
    }

    private void openSearchActivity() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivityForResult(intent, SEARCH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            handleSearchResult(data);
        }
    }

    private void handleSearchResult(Intent data) {
        selectedTrackId = data.getStringExtra("trackId");
        String trackName = data.getStringExtra("trackName");
        String artistName = data.getStringExtra("artistName");
        // String imageUrl = data.getStringExtra("imageUrl");

        if (isTrackInfoValid(trackName, artistName)) {
            updateTrackInfoUI(trackName, artistName);
        } else {
            showToast("Failed to retrieve track information.");
        }
    }

    private boolean isTrackInfoValid(String trackName, String artistName) {
        return selectedTrackId != null && !selectedTrackId.isEmpty()
                && trackName != null && !trackName.isEmpty()
                && artistName != null && !artistName.isEmpty();
    }

    private void updateTrackInfoUI(String trackName, String artistName) {
        musicTitle.setText(trackName);
        musicArtist.setText(artistName);
        // 고정된 이미지 리소스를 사용하여 앨범 이미지를 설정합니다.
        albumImage.setImageResource(R.drawable.albume);
    }

    public void onPost(View view) {
        String content = postContent.getText().toString().trim();
        if (isPostValid(content)) {
            String postId = databaseReference.push().getKey();
            if (postId != null) {
                Post1 post = new Post1(selectedTrackId, content);
                savePostToFirebase(postId, post);
            }
        }
    }

    private boolean isPostValid(String content) {
        if (selectedTrackId == null || selectedTrackId.isEmpty()) {
            showToast("Please select a track.");
            return false;
        }
        if (content.isEmpty()) {
            showToast("Please enter post content.");
            return false;
        }
        return true;
    }

    private void savePostToFirebase(String postId, Post1 post) {
        databaseReference.child(postId).setValue(post)
                .addOnSuccessListener(aVoid -> showToast("Post created successfully."))
                .addOnFailureListener(e -> showToast("Failed to create post."));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
