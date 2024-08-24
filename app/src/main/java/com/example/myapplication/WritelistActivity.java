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

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class WritelistActivity extends AppCompatActivity {

    private static final int SEARCH_REQUEST_CODE = 200;

    private ImageView albumImage;
    private TextView musicTitle, musicArtist;
    private EditText postContent, userNameInput;
    private Button searchButton, postButton;

    private String selectedTrackId;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.writelist);

        initializeUI();

        // Firebase 초기화
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("spotify_post");

        // ImageView에 기본 이미지 설정 (URL 사용 예제)
        String imageUrl = "https://example.com/default_album_image.jpg"; // URL로 교체
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.albume) // 로딩 중에 표시할 기본 이미지
                .into(albumImage);

        searchButton.setOnClickListener(v -> openSearchActivity());
        postButton.setOnClickListener(this::onPost);
    }

    private void initializeUI() {
        albumImage = findViewById(R.id.album_image);
        musicTitle = findViewById(R.id.music_title);
        musicArtist = findViewById(R.id.music_artist);
        postContent = findViewById(R.id.post_content);
        userNameInput = findViewById(R.id.user_name_input);
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
    }

    private void onPost(View view) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            showToast("사용자가 로그인되지 않았습니다.");
            return;
        }

        String userId = currentUser.getUid();
        String userName = userNameInput.getText().toString();
        String postId = databaseReference.push().getKey();
        String content = postContent.getText().toString();
        String trackName = musicTitle.getText().toString();
        String artistName = musicArtist.getText().toString();

        Post1 post = new Post1(userId, userName, postId, content, trackName, artistName, selectedTrackId, 0, 0);

        if (postId != null) {
            savePostToDatabase(postId, post);
        }
    }

    private void savePostToDatabase(String postId, Post1 post) {
        databaseReference.child(postId).setValue(post)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showToast("게시물이 업로드되었습니다.");
                        finish();
                    } else {
                        showToast("게시물 업로드에 실패하였습니다.");
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}