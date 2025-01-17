package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth; // 파이어베이스 인증처리
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스
    private EditText nsignID, nsignPW; // 로그인 입력필드
    private SessionManager sessionManager; // 세션 매니저

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Mutalk");

        nsignID = findViewById(R.id.editID);
        nsignPW = findViewById(R.id.ediPassword);
        sessionManager = new SessionManager(getApplicationContext());

        // 로그인 상태를 확인하여, 로그인 상태이면 메인 화면으로 이동
        if (sessionManager.isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, TabActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        Button signin = findViewById(R.id.signin);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), sign.class);
                startActivity(intent);
            }
        });

        Button loginButton = findViewById(R.id.loginbutton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 로그인 요청
                String strID = nsignID.getText().toString();
                String strPW = nsignPW.getText().toString();

                // 아이디와 비밀번호 입력 검사
                if (strID.isEmpty()) {
                    Toast.makeText(MainActivity.this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (strPW.isEmpty()) {
                    Toast.makeText(MainActivity.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                mFirebaseAuth.signInWithEmailAndPassword(strID, strPW).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 로그인 성공 시 사용자 ID와 이름을 가져와서 세션에 저장
                            String userId = mFirebaseAuth.getCurrentUser().getUid();
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Mutalk/UserAccount").child(userId);

                            userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DataSnapshot dataSnapshot = task.getResult();
                                        if (dataSnapshot.exists()) {
                                            UserAccount userAccount = dataSnapshot.getValue(UserAccount.class);
                                            if (userAccount != null) {
                                                String userName = userAccount.getName();
                                                sessionManager.createLoginSession(userId, userName);
                                                // TabActivity로 이동
                                                Intent intent = new Intent(MainActivity.this, TabActivity.class);
                                                startActivity(intent);
                                                finish(); // 현재 액티비티 종료
                                            } else {
                                                Toast.makeText(MainActivity.this, "UserAccount object is null", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(MainActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                                            Log.e("MainActivity", "DataSnapshot does not exist for userId: " + userId);
                                        }
                                    } else {
                                        Toast.makeText(MainActivity.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                                        Log.e("MainActivity", "Database error: " + task.getException().getMessage());
                                    }
                                }
                            });
                        } else {
                            // 로그인 실패 시 실패 화면으로 이동
                            Intent intent = new Intent(MainActivity.this, loginfailed.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        });


    }
}
