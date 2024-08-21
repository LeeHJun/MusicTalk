package com.example.myapplication.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.SessionManager;
import com.example.myapplication.UserAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class Frag2 extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private CircleImageView profileImageView;
    private TextView userNameTextView, userIdTextView, userDescriptionTextView;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frag2, container, false);

        profileImageView = view.findViewById(R.id.profile_image);
        userNameTextView = view.findViewById(R.id.user_name);
        userIdTextView = view.findViewById(R.id.user_id);
        userDescriptionTextView = view.findViewById(R.id.user_description);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Mutalk");
        sessionManager = new SessionManager(getActivity().getApplicationContext());

        if (!sessionManager.isLoggedIn()) {
            // 사용자가 로그인되어 있지 않은 경우 MainActivity로 돌아감
            Toast.makeText(getActivity(), "로그인 후 사용해 주세요.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
            return view;
        }

        // 1. 프로필 이미지 변경 기능
        profileImageView.setOnClickListener(v -> openGallery());

        // 2. 회원가입 시 입력한 사용자 정보 가져오기
        loadUserInfo();

        // 3. 로그아웃 버튼 클릭 시 로그아웃 기능 구현
        view.findViewById(R.id.button_edit).setOnClickListener(v -> logout());

        return view;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                profileImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadUserInfo() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            // 사용자가 로그인되어 있지 않은 경우 처리
            Toast.makeText(getActivity(), "사용자가 로그인되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();

        // 데이터베이스의 UserAccount 노드에서 현재 사용자 ID로 정보를 가져옴
        databaseReference.child("UserAccount").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserAccount userAccount = dataSnapshot.getValue(UserAccount.class);

                    if (userAccount != null) {
                        // 사용자 정보를 화면에 표시
                        userNameTextView.setText(userAccount.getName());
                        userIdTextView.setText(userAccount.getEmailId());
                        userDescriptionTextView.setText("안녕하세요 " + userAccount.getName() + "입니다. 잘 부탁드립니다.");
                    } else {
                        Toast.makeText(getActivity(), "사용자 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "사용자 정보가 데이터베이스에 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "사용자 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        // Firebase 인증 로그아웃
        FirebaseAuth.getInstance().signOut();

        // 세션 로그아웃
        sessionManager.logoutUser();

        // 로그인 화면으로 이동
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);

        // 현재 액티비티 종료
        getActivity().finish();
    }
}
