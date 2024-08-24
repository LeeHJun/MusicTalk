package com.example.myapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Frag3 extends Fragment {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference postsReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag3, container, false);

        initializeFirebase();
        checkForPosts();

        return view;
    }

    private void initializeFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        postsReference = FirebaseDatabase.getInstance().getReference("spotify_post");
    }

    private void checkForPosts() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getActivity(), "User not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        postsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    // 게시물이 존재할 경우 Frag3LayoutManager로 전환
                    Fragment frag3LayoutManager = new Frag3LayoutManager();
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, frag3LayoutManager)
                            .addToBackStack(null)
                            .commit();
                } else {
                    // 게시물이 없을 경우 다른 Fragment로 전환
                    Fragment noListFragment = new NoListFragment();
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, noListFragment)
                            .addToBackStack(null)
                            .commit();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to load posts.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
