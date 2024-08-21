package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class sign extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth; // 파이어베이스 인증처리
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스
    private EditText nsignname, nsignID, nsignBirth, nsignBirth2, // 회원가입 입력필드
            nsignBirth3, nsignPW, PWok;
    private Button nsignupbutton, checkIDButton, checkPasswordButton;  // 회원가입 버튼
    private ImageButton togglePasswordVisibility; // 비밀번호 보기/숨기기 버튼
    private TextView passwordError, passwordMatchError; // 오류 메시지 텍스트뷰
    private boolean isIDChecked = false;  // ID 중복 확인 여부
    private boolean isPasswordValid = false;  // 비밀번호 조건 충족 여부
    private boolean isPasswordMatching = false;  // 비밀번호 일치 여부
    private boolean isPasswordVisible = false;  // 비밀번호 보기 상태

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Mutalk");

        nsignname = findViewById(R.id.signName);
        nsignID = findViewById(R.id.signID);
        nsignBirth = findViewById(R.id.signBirth);
        nsignBirth2 = findViewById(R.id.signBirth2);
        nsignBirth3 = findViewById(R.id.signBirth3);
        nsignPW = findViewById(R.id.signPW);
        PWok = findViewById(R.id.signPW2);
        nsignupbutton = findViewById(R.id.signupbutton);
        checkIDButton = findViewById(R.id.checkIDButton);
        checkPasswordButton = findViewById(R.id.checkPasswordButton);
        togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility);
        passwordError = findViewById(R.id.passwordError);
        passwordMatchError = findViewById(R.id.passwordMatchError);

        // 로그인 텍스트뷰에 클릭 리스너 추가
        TextView loginTextView = findViewById(R.id.login12);
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(sign.this, MainActivity.class);
                startActivity(intent);
            }
        });

        checkIDButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strID = nsignID.getText().toString();
                if (strID.isEmpty()) {
                    Toast.makeText(sign.this, "아이디를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                checkIDAvailability(strID);
            }
        });

        togglePasswordVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPasswordVisible) {
                    nsignPW.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    togglePasswordVisibility.setImageResource(R.drawable.ic_visibility_off);
                } else {
                    nsignPW.setInputType(InputType.TYPE_CLASS_TEXT);
                    togglePasswordVisibility.setImageResource(R.drawable.ic_visibility);
                }
                isPasswordVisible = !isPasswordVisible;
                nsignPW.setSelection(nsignPW.length());
            }
        });

        checkPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strPW = nsignPW.getText().toString();
                String strPW2 = PWok.getText().toString();
                if (strPW.equals(strPW2)) {
                    passwordMatchError.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    passwordMatchError.setText("비밀번호가 일치합니다.");
                    isPasswordMatching = true;
                } else {
                    passwordMatchError.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    passwordMatchError.setText("비밀번호가 다릅니다.");
                    isPasswordMatching = false;
                }
                passwordMatchError.setVisibility(View.VISIBLE);
            }
        });

        nsignupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strID = nsignID.getText().toString();
                if (!isIDChecked) {
                    Toast.makeText(sign.this, "아이디 중복 확인을 해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isEmailValid(strID)) {
                    Toast.makeText(sign.this, "올바른 이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String strPW = nsignPW.getText().toString();
                if (!isPasswordValid(strPW)) {
                    passwordError.setVisibility(View.VISIBLE);
                    return;
                }

                String strPW2 = PWok.getText().toString();
                if (!strPW.equals(strPW2)) {
                    Toast.makeText(sign.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (isIDChecked && isPasswordValid && isPasswordMatching) {
                    String strname = nsignname.getText().toString();
                    String strBirth = nsignBirth.getText().toString();
                    String strBirth2 = nsignBirth2.getText().toString();
                    String strBirth3 = nsignBirth3.getText().toString();

                    mFirebaseAuth.createUserWithEmailAndPassword(strID, strPW).addOnCompleteListener
                            (sign.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser FirebaseUser = mFirebaseAuth.getCurrentUser();
                                        UserAccount account = new UserAccount();
                                        account.setIdToken(FirebaseUser.getUid());
                                        account.setEmailId(strID);
                                        account.setPassword(strPW);
                                        account.setName(strname);
                                        account.setBirth(strBirth);
                                        account.setBirth2(strBirth2);
                                        account.setBirth3(strBirth3);

                                        mDatabaseRef.child("UserAccount").child(FirebaseUser.getUid()).setValue(account);

                                        Toast.makeText(sign.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Exception e = task.getException();
                                        if (e != null) {
                                            Log.e("SignUpError", e.getMessage());
                                        }
                                        Toast.makeText(sign.this, "회원가입에 실패하셨습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(sign.this, "회원가입 조건을 충족하지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void checkIDAvailability(final String strID) {
        mDatabaseRef.child("UserAccount").orderByChild("emailId").equalTo(strID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(sign.this, "아이디가 이미 존재합니다.", Toast.LENGTH_SHORT).show();
                            isIDChecked = false;
                        } else {
                            Toast.makeText(sign.this, "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show();
                            isIDChecked = true;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(sign.this, "아이디 확인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isPasswordValid(String password) {
        Pattern pattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$");
        boolean isValid = pattern.matcher(password).matches();
        if (!isValid) {
            passwordError.setText("최소 8자 이상, 영문자, 숫자, 특수문자를 각각 1개 이상 포함해야 합니다.");
            passwordError.setVisibility(View.VISIBLE);
        } else {
            passwordError.setVisibility(View.GONE);
            isPasswordValid = true;
        }
        return isValid;
    }

    private boolean isEmailValid(String email) {
        Pattern pattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
        return pattern.matcher(email).matches();
    }
}
