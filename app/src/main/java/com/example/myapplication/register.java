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

        String name = ((EditText) findViewById(R.id.title_et)).getText().toString();
        String mobile = ((EditText) findViewById(R.id.content_et)).getText().toString();
        int resId = -1;
        if (name.isEmpty() || mobile.isEmpty()) {
            Toast.makeText(this, "Title and content are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = getUserId();
        if (userId == null) {
            Toast.makeText(this, "Failed to get user ID.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference newPostRef = databaseReference.push();
        String postId = newPostRef.getKey();

        if (postId == null) {
            Toast.makeText(this, "Failed to generate post ID.", Toast.LENGTH_SHORT).show();
            return;
        }

        SingerItem2 newItem = new SingerItem2(name, mobile, resId, 0, 0, userId, postId, boardName);
        newPostRef.setValue(newItem).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(register.this, "Post successfully registered.", Toast.LENGTH_SHORT).show();
                finish();
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
