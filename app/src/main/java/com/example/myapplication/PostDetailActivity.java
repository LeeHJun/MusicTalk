package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private String boardName;
    private String postId;

    private TextView titleTextView, contentTextView, recommendationCount;
    private Button recommendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        boardName = getIntent().getStringExtra("BOARD_NAME");
        postId = getIntent().getStringExtra("POST_ID");

        if (boardName == null || postId == null) {
            Log.e("PostDetailActivity", "Invalid BOARD_NAME or POST_ID");
            finish();
            return;
        }

        titleTextView = findViewById(R.id.post_title);
        contentTextView = findViewById(R.id.post_content);
        recommendationCount = findViewById(R.id.recommendationCount);
        recommendButton = findViewById(R.id.btn_recommend);

        initFirebase();
        loadPostDetails();
    }

    private void initFirebase() {
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("notice board").child(boardName).child(postId);
    }

    private void loadPostDetails() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SingerItem2 post = dataSnapshot.getValue(SingerItem2.class);
                if (post != null) {
                    titleTextView.setText(post.getName());
                    contentTextView.setText(post.getMobile());
                    // recommendationCount는 필요 시 post 클래스에 추가하여 사용
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PostDetailActivity", "Error loading post details: " + databaseError.getMessage());
            }
        });
    }

    private void recommendPost() {
        // 추천 로직 구현
    }
}
