package com.example.myapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.Post;
import com.example.myapplication.R;
import com.example.myapplication.UserAccount;
import com.example.myapplication.WritelistActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class Frag3 extends Fragment {

    private View view;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private CircleImageView profileImageView;
    private TextView usernameTextView, postContentTextView, viewComments;
    private ImageView heart1, heart2, comment, writeImageView;
    private boolean isLiked = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // nolist.xml 레이아웃을 기본으로 설정
        view = inflater.inflate(R.layout.nolist, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Mutalk");

        // 게시물이 있는지 확인하는 함수 호출
        checkForPosts();

        return view;
    }

    private void checkForPosts() {
        String userId = firebaseAuth.getCurrentUser().getUid();
        databaseReference.child("Posts").orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // 게시물이 존재할 경우 frag3.xml 레이아웃을 인플레이트
                    View frag3View = LayoutInflater.from(getActivity()).inflate(R.layout.frag3, (ViewGroup) getView(), false);
                    setupViewElements(frag3View);
                    loadUserProfile();
                    loadPostContent(dataSnapshot);

                    // 새롭게 인플레이트된 레이아웃을 화면에 설정
                    FrameLayout container = (FrameLayout) frag3View.findViewById(R.id.fragment_container);
                    if (container != null) {
                        container.removeAllViews();
                        container.addView(frag3View);
                    }
                } else {
                    // 게시물이 없을 경우 NoListFragment로 처리
                    showNoListFragment();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to load posts.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupViewElements(View rootView) {
        // frag3.xml에서 사용하는 뷰를 초기화
        profileImageView = rootView.findViewById(R.id.profile_image);
        usernameTextView = rootView.findViewById(R.id.username);
        postContentTextView = rootView.findViewById(R.id.post_content);
        viewComments = rootView.findViewById(R.id.view_comments);
        heart1 = rootView.findViewById(R.id.heart1);
        heart2 = rootView.findViewById(R.id.heart2);
        comment = rootView.findViewById(R.id.comment);
        writeImageView = rootView.findViewById(R.id.write);

        // 버튼 리스너 등 필요한 초기화 작업
        heart1.setOnClickListener(v -> toggleLike());
        writeImageView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WritelistActivity.class);
            startActivity(intent);
        });
    }


    private void loadUserProfile() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getActivity(), "User not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        databaseReference.child("UserAccount").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserAccount userAccount = dataSnapshot.getValue(UserAccount.class);
                    if (userAccount != null) {
                        usernameTextView.setText(userAccount.getName());
                        // 프로필 이미지 설정 (필요시)
                        // Glide.with(getActivity()).load(userAccount.getProfileImageUrl()).into(profileImageView);
                    }
                } else {
                    Toast.makeText(getActivity(), "User profile not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to load user profile.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPostContent(DataSnapshot dataSnapshot) {
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            Post post = snapshot.getValue(Post.class);
            if (post != null) {
                postContentTextView.setText(post.getContent());
                viewComments.setText(post.getCommentCount() + " comments");
            }
        }
    }

    private void toggleLike() {
        if (isLiked) {
            heart1.setVisibility(View.VISIBLE);
            heart2.setVisibility(View.GONE);
        } else {
            heart1.setVisibility(View.GONE);
            heart2.setVisibility(View.VISIBLE);
        }
        isLiked = !isLiked;
    }

    private void showNoListFragment() {
        Fragment noListFragment = new NoListFragment();

        // 기존의 FragmentContainer를 가진 레이아웃에서 Fragment를 교체합니다.
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, noListFragment)
                    .commit();
        } else {
            Toast.makeText(getActivity(), "Fragment manager is null.", Toast.LENGTH_SHORT).show();
        }
    }


}
