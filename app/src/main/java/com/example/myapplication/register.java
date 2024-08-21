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
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("notice board").child(boardName); // 게시판 이름을 경로에 사용
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
        String name = ((EditText) findViewById(R.id.title_et)).getText().toString();
        String mobile = ((EditText) findViewById(R.id.content_et)).getText().toString();
        int resId = -1; // 임시로 설정, 필요 시 다른 방법으로 아이콘 결정

        if (name.isEmpty() || mobile.isEmpty()) {
            Toast.makeText(this, "Title and content are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        SingerItem2 newItem = new SingerItem2(name, mobile, resId);
        databaseReference.push().setValue(newItem).addOnSuccessListener(new OnSuccessListener<Void>() {
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
}
