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
import com.example.myapplication.SimplePostAdapter2;
import com.example.myapplication.SingerItem2;
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
    private RecyclerView recyclerViewLikes;
    private SimplePostAdapter postAdapter;
    private SimplePostAdapter2 boardPostAdapter; // 추가된 부분
    private List<SingerItem2> boardPostList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag2, container, false);

        profileImageView = view.findViewById(R.id.profile_image);
        userNameTextView = view.findViewById(R.id.user_name);
        userIdTextView = view.findViewById(R.id.user_id);
        userDescriptionTextView = view.findViewById(R.id.user_description);
        recyclerViewWorks = view.findViewById(R.id.recycler_view_works);
        recyclerViewLikes = view.findViewById(R.id.recycler_view_likes);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
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
        loadBoardPosts(); // 수정된 부분

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
                Log.d("Frag2", "프로필 이미지 변경됨: " + imageUri.toString());
            } catch (IOException e) {
                Log.e("Frag2", "이미지 로드 실패", e);
            }
        }
    }

    private void loadUserInfo() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(getActivity(), "사용자가 로그인되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
            Log.e("Frag2", "사용자가 로그인되어 있지 않습니다.");
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
                        Log.d("Frag2", "사용자 정보 로드 완료: " + userAccount.toString());
                    } else {
                        Toast.makeText(getActivity(), "사용자 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                        Log.w("Frag2", "사용자 정보가 null입니다.");
                    }
                } else {
                    Log.w("Frag2", "데이터베이스에 사용자 정보가 존재하지 않습니다.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "사용자 정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                Log.e("Frag2", "사용자 정보 로드 실패: " + databaseError.getMessage());
            }
        });
    }

    private void loadUserPosts() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            Log.e("Frag2", "사용자가 로그인되어 있지 않습니다.");
            return;
        }

        String currentUserId = currentUser.getUid();
        Log.d("Frag2", "현재 로그인된 사용자 UID: " + currentUserId);

        databaseReference.child("spotify_post").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Post1> postList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post1 post = snapshot.getValue(Post1.class);
                    if (post != null) {
                        if (post.getUserId().equals(currentUserId)) {
                            postList.add(post);
                        }
                    }
                }
                Log.d("Frag2", "게시물 로드 완료, 총 게시물 수: " + postList.size());
                postAdapter = new SimplePostAdapter(postList, getActivity());
                recyclerViewWorks.setAdapter(postAdapter);
                recyclerViewWorks.setLayoutManager(new LinearLayoutManager(getActivity()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Frag2", "게시물 로드 실패: " + databaseError.getMessage());
                Toast.makeText(getActivity(), "게시물 로드 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBoardPosts() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            Log.e("Frag2", "사용자가 로그인되어 있지 않습니다.");
            return;
        }

        String currentUserId = currentUser.getUid();
        Log.d("Frag2", "현재 로그인된 사용자 UID: " + currentUserId);

        databaseReference.child("notice board").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boardPostList.clear();
                Log.d("Frag2", "게시판 게시글 데이터 변경 감지됨");

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // "자유 게시판" 등의 게시판 이름이 추가로 있을 수 있으므로 하위 노드를 읽어야 합니다.
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        SingerItem2 item = postSnapshot.getValue(SingerItem2.class);

                        if (item != null) {
                            String itemUserId = item.getUserId();
                            Log.d("Frag2", "불러온 게시글의 userId: " + itemUserId);

                            // 로그에 현재 로그인된 사용자 ID도 출력
                            Log.d("Frag2", "현재 로그인된 사용자 UID: " + currentUserId);

                            if (itemUserId != null && itemUserId.equals(currentUserId)) {
                                Log.d("Frag2", "게시판 게시글 데이터 (현재 로그인 사용자와 일치): " + item.toString());
                                boardPostList.add(item);
                            } else {
                                Log.d("Frag2", "게시판 게시글의 userId와 로그인된 사용자 UID가 일치하지 않거나 userId가 null입니다.");
                            }
                        } else {
                            Log.d("Frag2", "게시판 게시글이 null입니다.");
                        }
                    }
                }

                if (boardPostList.isEmpty()) {
                    Log.d("Frag2", "게시판 게시글이 없습니다.");
                }

                boardPostAdapter = new SimplePostAdapter2(boardPostList, getActivity());
                recyclerViewLikes.setAdapter(boardPostAdapter);
                recyclerViewLikes.setLayoutManager(new LinearLayoutManager(getActivity()));
                Log.d("Frag2", "게시판 게시글 로드 완료, 총 게시글 수: " + boardPostList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Frag2", "게시판 게시글 로드 실패: " + databaseError.getMessage());
                Toast.makeText(getActivity(), "게시판 게시글 로드 실패", Toast.LENGTH_SHORT).show();
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
