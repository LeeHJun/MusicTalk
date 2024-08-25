package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class register extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private String boardName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // BOARD_NAME 값을 인텐트로부터 받아옵니다.
        boardName = getIntent().getStringExtra("BOARD_NAME");

        if (boardName == null || boardName.isEmpty()) {
            Log.e("RegisterActivity", "BOARD_NAME is null or empty");
            Toast.makeText(this, "Invalid board name", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initFirebase();
        initView();
    }

    private void initFirebase() {
        // Firebase 데이터베이스 초기화
        database = FirebaseDatabase.getInstance();
        // 'notice board' 하위의 특정 게시판 이름 경로로 레퍼런스 설정
        databaseReference = database.getReference("notice board").child(boardName);
    }

    private void initView() {
        Button button = findViewById(R.id.reg_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    private void register() {
        // 입력된 제목과 내용을 가져옵니다.
        String name = ((EditText) findViewById(R.id.title_et)).getText().toString();
        String mobile = ((EditText) findViewById(R.id.content_et)).getText().toString();
        int resId = -1; // 기본 값으로 설정, 필요에 따라 변경 가능

        if (name.isEmpty() || mobile.isEmpty()) {
            Toast.makeText(this, "Title and content are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 현재 사용자 UID를 가져옵니다.
        String userId = getUserId();
        if (userId == null) {
            Toast.makeText(this, "Failed to get user ID.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 새 게시물 ID를 생성합니다.
        DatabaseReference newPostRef = databaseReference.push();
        String postId = newPostRef.getKey();

        if (postId == null) {
            Toast.makeText(this, "Failed to generate post ID.", Toast.LENGTH_SHORT).show();
            return;
        }

        // SingerItem2 객체 생성시 boardName을 포함하여 생성합니다.
        SingerItem2 newItem = new SingerItem2(name, mobile, resId, 0, 0, userId, postId, boardName);
        newPostRef.setValue(newItem).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(register.this, "Post successfully registered.", Toast.LENGTH_SHORT).show();
                finish(); // 등록 후 현재 액티비티 종료
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("RegisterActivity", "Failed to register post: " + e.getMessage());
                Toast.makeText(register.this, "Failed to register post.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null ? user.getUid() : null;
    }
}
