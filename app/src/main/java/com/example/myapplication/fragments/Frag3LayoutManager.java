package com.example.myapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Post1;
import com.example.myapplication.PostAdapter;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Frag3LayoutManager extends Fragment {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference postsReference;
    private RecyclerView postsRecyclerView;
    private PostAdapter postAdapter;
    private List<Post1> postList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag3, container, false);

        initializeFirebase();
        setupViewElements(view);
        loadPosts();

        return view;
    }

    private void initializeFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        postsReference = FirebaseDatabase.getInstance().getReference("spotify_post");
    }

    private void setupViewElements(View rootView) {
        postsRecyclerView = rootView.findViewById(R.id.posts_recycler_view);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Context를 전달하여 PostAdapter 생성
        postAdapter = new PostAdapter(postList, getActivity());
        postsRecyclerView.setAdapter(postAdapter);
    }

    private void loadPosts() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getActivity(), "사용자가 로그인되지 않았습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserId = currentUser.getUid();
        postsReference.orderByChild("userId").equalTo(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        postList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Post1 post = snapshot.getValue(Post1.class);
                            if (post != null) {
                                postList.add(post);
                            }
                        }
                        postAdapter.notifyDataSetChanged();
                        loadCommentsForAllPosts();  // Load comments for all posts after posts are loaded
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getActivity(), "게시물을 로드하는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadCommentsForAllPosts() {
        for (Post1 post : postList) {
            loadCommentsForPost(post.getPostId());
        }
    }

    private void loadCommentsForPost(String postId) {
        if (postId != null) {
            DatabaseReference commentsRef = postsReference.child(postId).child("comments");
            commentsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<String> comments = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String comment = snapshot.getValue(String.class);
                        if (comment != null) {
                            comments.add(comment);
                        }
                    }
                    postAdapter.updatePostComments(postId, comments);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(), "댓글 로딩 실패.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
