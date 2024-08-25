package com.example.myapplication.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.Post1;
import com.example.myapplication.R;
import com.example.myapplication.SessionManager;
import com.example.myapplication.SimplePostAdapter;
import com.example.myapplication.UserAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Frag2 extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private CircleImageView profileImageView;
    private TextView userNameTextView, userIdTextView, userDescriptionTextView;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private SessionManager sessionManager;
    private RecyclerView recyclerViewWorks;
    private SimplePostAdapter postAdapter;
    private List<Post1> postList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag2, container, false);

        profileImageView = view.findViewById(R.id.profile_image);
        userNameTextView = view.findViewById(R.id.user_name);
        userIdTextView = view.findViewById(R.id.user_id);
        userDescriptionTextView = view.findViewById(R.id.user_description);
        recyclerViewWorks = view.findViewById(R.id.recycler_view_works);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Mutalk");
        sessionManager = new SessionManager(getActivity().getApplicationContext());

        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(getActivity(), "로그인 후 사용해 주세요.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
            return view;
        }

        profileImageView.setOnClickListener(v -> openGallery());
        loadUserInfo();
        view.findViewById(R.id.button_edit).setOnClickListener(v -> logout());
        loadUserPosts();

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
            Toast.makeText(getActivity(), "사용자가 로그인되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();

        databaseReference.child("UserAccount").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserAccount userAccount = dataSnapshot.getValue(UserAccount.class);

                    if (userAccount != null) {
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

    private void loadUserPosts() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            Log.e("Frag2", "사용자가 로그인되어 있지 않습니다.");
            return;
        }

        // Firebase에서 현재 로그인한 사용자와 관련된 게시물 로드
        databaseReference.child("spotify_post").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                Log.d("Frag2", "데이터 변경 감지됨");

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post1 post = snapshot.getValue(Post1.class);
                    if (post != null) {
                        Log.d("Frag2", "게시물 데이터: " + post.toString());
                        postList.add(post);
                    } else {
                        Log.d("Frag2", "게시물이 null입니다.");
                    }
                }

                if (postList.isEmpty()) {
                    Log.d("Frag2", "게시물이 없습니다.");
                } else {
                    Log.d("Frag2", "게시물 로드 완료, 총 게시물 수: " + postList.size());
                }

                // RecyclerView와 Adapter 설정
                if (postAdapter == null) {
                    postAdapter = new SimplePostAdapter(postList, getActivity());
                    recyclerViewWorks.setAdapter(postAdapter);
                    recyclerViewWorks.setLayoutManager(new LinearLayoutManager(getActivity()));
                } else {
                    postAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Frag2", "게시물 로드 실패: " + databaseError.getMessage());
                Toast.makeText(getActivity(), "게시물 로드 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        sessionManager.logoutUser();

        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
