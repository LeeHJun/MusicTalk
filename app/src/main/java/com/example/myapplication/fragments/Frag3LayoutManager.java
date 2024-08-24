package com.example.myapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.Post1;
import com.example.myapplication.R;
import com.example.myapplication.WritelistActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Frag3LayoutManager extends Fragment {

    private static final String TAG = "Frag3LayoutManager";

    private FirebaseAuth firebaseAuth;
    private DatabaseReference postsReference;
    private TextView postContentTextView, viewComments;
    private TextView musicTitleTextView, musicArtistTextView, userNameTextView, likesCountTextView;
    private ImageView heart1, heart2, writeImageView;
    private EditText commentInput;
    private Button postCommentButton;
    private boolean isLiked = false;
    private int likeCount = 0;
    private String currentPostId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag3, container, false);

        initializeFirebase();
        setupViewElements(view);
        loadPostContent();

        return view;
    }

    private void initializeFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        postsReference = FirebaseDatabase.getInstance().getReference("spotify_post");
    }

    private void setupViewElements(View rootView) {
        postContentTextView = rootView.findViewById(R.id.post_content);
        viewComments = rootView.findViewById(R.id.view_comments);
        heart1 = rootView.findViewById(R.id.heart1);
        heart2 = rootView.findViewById(R.id.heart2);
        writeImageView = rootView.findViewById(R.id.write);
        commentInput = rootView.findViewById(R.id.comment_input);
        postCommentButton = rootView.findViewById(R.id.post_comment_button);

        // 추가: song title과 artist name을 위한 TextView 초기화
        musicTitleTextView = rootView.findViewById(R.id.music_title);
        musicArtistTextView = rootView.findViewById(R.id.music_artist);
        userNameTextView = rootView.findViewById(R.id.username); // 추가된 TextView
        likesCountTextView = rootView.findViewById(R.id.likes_count); // 추가된 TextView

        heart1.setOnClickListener(v -> toggleLike());
        heart2.setOnClickListener(v -> toggleLike());
        writeImageView.setOnClickListener(v -> openWriteActivity());
        postCommentButton.setOnClickListener(v -> postComment());
    }

    private void loadPostContent() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getActivity(), "사용자가 로그인되지 않았습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserId = currentUser.getUid();
        postsReference.orderByChild("userId").equalTo(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean postFound = false;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Post1 post = snapshot.getValue(Post1.class);
                            if (post != null) {
                                postContentTextView.setText(post.getContent());
                                viewComments.setText(post.getCommentCount() + " 댓글");
                                likeCount = post.getLikeCount();
                                likesCountTextView.setText(likeCount + " 좋아요");

                                // 노래 제목과 아티스트 이름을 설정합니다.
                                String trackName = post.getTrackName();
                                String artistName = post.getArtistName();
                                if (trackName != null && artistName != null) {
                                    musicTitleTextView.setText(trackName);
                                    musicArtistTextView.setText(artistName);
                                }

                                // 사용자 이름을 설정합니다.
                                String userName = post.getUserName();
                                if (userName != null) {
                                    userNameTextView.setText(userName);
                                }

                                currentPostId = snapshot.getKey();
                                isLiked = likeCount > 0;
                                updateLikeUI();

                                postFound = true;
                                break;
                            }
                        }

                        if (!postFound) {
                            Toast.makeText(getActivity(), "게시물이 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getActivity(), "게시물 로딩 실패.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void toggleLike() {
        if (isLiked) {
            heart1.setVisibility(View.VISIBLE);
            heart2.setVisibility(View.GONE);
            likeCount--;
        } else {
            heart1.setVisibility(View.GONE);
            heart2.setVisibility(View.VISIBLE);
            likeCount++;
        }
        likesCountTextView.setText(likeCount + " 좋아요"); // 수정된 부분
        isLiked = !isLiked;

        updateLikeCountInFirebase();
    }


    private void updateLikeUI() {
        if (isLiked) {
            heart1.setVisibility(View.GONE);
            heart2.setVisibility(View.VISIBLE);
        } else {
            heart1.setVisibility(View.VISIBLE);
            heart2.setVisibility(View.GONE);
        }
    }

    private void updateLikeCountInFirebase() {
        if (currentPostId != null) {
            postsReference.child(currentPostId).child("likeCount").setValue(likeCount)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Like count updated successfully."))
                    .addOnFailureListener(e -> Log.d(TAG, "Failed to update like count: " + e.getMessage()));
        }
    }

    private void postComment() {
        String commentText = commentInput.getText().toString().trim();
        if (!commentText.isEmpty() && currentPostId != null) {
            DatabaseReference commentsRef = postsReference.child(currentPostId).child("comments");
            String commentId = commentsRef.push().getKey();
            commentsRef.child(commentId).setValue(commentText)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "댓글이 게시되었습니다.", Toast.LENGTH_SHORT).show();
                            commentInput.setText("");
                        } else {
                            Toast.makeText(getActivity(), "댓글 게시 실패.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "댓글을 입력하세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openWriteActivity() {
        Intent intent = new Intent(getActivity(), WritelistActivity.class);
        startActivity(intent);
    }
}
