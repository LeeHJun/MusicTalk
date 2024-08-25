package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private boolean isLiked = false; // 좋아요 상태를 추적하는 변수

    // UI 요소 선언
    private TextView nameTextView;
    private TextView mobileTextView;
    private TextView likesCountTextView;
    private TextView viewCommentsTextView;
    private EditText commentInput;
    private Button postCommentButton;
    private ImageView profileImageView;
    private ImageView backButton;
    private ImageView heart1;
    private ImageView heart2;
    private RecyclerView commentsRecyclerView;
    private NewCommentAdapter commentAdapter;
    private List<String> commentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // 전달된 데이터 가져오기
        boardName = getIntent().getStringExtra("BOARD_NAME");
        postId = getIntent().getStringExtra("POST_ID");

        // 디버깅을 위한 로그 출력
        Log.d("PostDetailActivity", "Received BOARD_NAME: " + boardName);
        Log.d("PostDetailActivity", "Received POST_ID: " + postId);

        if (boardName == null || postId == null) {
            Log.e("PostDetailActivity", "유효하지 않은 BOARD_NAME 또는 POST_ID");
            finish();
            return;
        }

        // UI 요소 초기화
        nameTextView = findViewById(R.id.name);
        mobileTextView = findViewById(R.id.mobile);
        likesCountTextView = findViewById(R.id.likes_count);
        viewCommentsTextView = findViewById(R.id.view_comments);
        commentInput = findViewById(R.id.comment_input);
        postCommentButton = findViewById(R.id.post_comment_button);
        profileImageView = findViewById(R.id.profile_image2);
        backButton = findViewById(R.id.back);
        heart1 = findViewById(R.id.heart1);
        heart2 = findViewById(R.id.heart2);

        commentsRecyclerView = findViewById(R.id.comments);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new NewCommentAdapter(commentList);
        commentsRecyclerView.setAdapter(commentAdapter);

        initFirebase();
        loadPostDetails();

        // 뒤로 가기 버튼 클릭 리스너 설정
        backButton.setOnClickListener(v -> finish());

        // 댓글 작성 버튼 클릭 리스너 설정
        postCommentButton.setOnClickListener(v -> postComment());

        // 좋아요 버튼 클릭 리스너 설정
        heart1.setOnClickListener(v -> toggleLike());
        heart2.setOnClickListener(v -> toggleLike());
    }

    private void initFirebase() {
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("notice board").child(boardName).child(postId);
        Log.d("PostDetailActivity", "Database Reference: " + databaseReference.toString());
    }

    private void loadPostDetails() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("PostDetailActivity", "DataSnapshot: " + dataSnapshot.toString());

                if (dataSnapshot.exists()) {
                    SingerItem2 post = dataSnapshot.getValue(SingerItem2.class);
                    if (post != null) {
                        Log.d("PostDetailActivity", "Name: " + post.getName());
                        Log.d("PostDetailActivity", "Mobile: " + post.getMobile());
                        Log.d("PostDetailActivity", "Like Count: " + post.getLikeCount());
                        Log.d("PostDetailActivity", "Comment Count: " + post.getCommentCount());

                        nameTextView.setText(post.getName());
                        mobileTextView.setText(post.getMobile());
                        likesCountTextView.setText(post.getLikeCount() + " 좋아요");

                        // 댓글 수에 따라 viewCommentsTextView 업데이트
                        viewCommentsTextView.setText("전체 " + post.getCommentCount() + "개의 댓글 보기");

                        // 초기 좋아요 상태 설정
                        isLiked = false; // 기본값은 좋아요 안함
                        heart1.setVisibility(View.VISIBLE);
                        heart2.setVisibility(View.INVISIBLE);

                        loadComments(); // 댓글 로딩
                    } else {
                        Log.e("PostDetailActivity", "Post 객체가 null입니다.");
                    }
                } else {
                    Log.e("PostDetailActivity", "게시물이 존재하지 않습니다.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PostDetailActivity", "게시물 로드 오류: " + databaseError.getMessage());
            }
        });
    }

    private void loadComments() {
        databaseReference.child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear(); // 기존 댓글 리스트 클리어
                int commentCount = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment1 comment = snapshot.getValue(Comment1.class);
                    if (comment != null) {
                        commentList.add(comment.getText());
                        commentCount++;
                    }
                }
                commentAdapter.notifyDataSetChanged(); // 어댑터에 변경 사항 알리기

                // 댓글 수 업데이트
                viewCommentsTextView.setText("전체 " + commentCount + "개의 댓글 보기");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PostDetailActivity", "댓글 로드 오류: " + databaseError.getMessage());
            }
        });
    }

    private void toggleLike() {
        DatabaseReference likeCountRef = databaseReference.child("likeCount");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    SingerItem2 post = dataSnapshot.getValue(SingerItem2.class);
                    if (post != null) {
                        int currentLikeCount = post.getLikeCount();
                        if (isLiked) {
                            // 좋아요 취소
                            currentLikeCount--;
                            heart1.setVisibility(View.VISIBLE);
                            heart2.setVisibility(View.INVISIBLE);
                        } else {
                            // 좋아요 추가
                            currentLikeCount++;
                            heart1.setVisibility(View.INVISIBLE);
                            heart2.setVisibility(View.VISIBLE);
                        }
                        // 좋아요 상태 업데이트
                        isLiked = !isLiked;
                        likesCountTextView.setText(currentLikeCount + " 좋아요");
                        databaseReference.child("likeCount").setValue(currentLikeCount);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PostDetailActivity", "좋아요 업데이트 오류: " + databaseError.getMessage());
            }
        });
    }

    private void postComment() {
        String commentText = commentInput.getText().toString().trim();
        if (!commentText.isEmpty()) {
            DatabaseReference commentsRef = databaseReference.child("comments");
            String commentId = commentsRef.push().getKey();
            Log.d("PostDetailActivity", "댓글 ID: " + commentId);
            Comment1 comment = new Comment1(commentId, commentText);
            commentsRef.child(commentId).setValue(comment).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("PostDetailActivity", "댓글 작성 성공");
                    commentInput.setText(""); // 입력 필드 비우기
                    loadComments(); // 댓글 새로고침
                } else {
                    Log.e("PostDetailActivity", "댓글 작성 실패: " + task.getException().getMessage());
                }
            });
        } else {
            Log.d("PostDetailActivity", "댓글 내용이 비어 있습니다.");
        }
    }
}
