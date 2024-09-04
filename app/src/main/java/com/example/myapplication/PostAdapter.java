package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post1> postList;
    private Map<String, List<String>> postCommentsMap = new HashMap<>();
    private Map<String, Boolean> postLikesMap = new HashMap<>();
    private Context context;

    public PostAdapter(List<Post1> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post1 post = postList.get(position);
        holder.usernameTextView.setText(post.getUserName());
        holder.postContentTextView.setText(post.getContent());
        holder.likesCountTextView.setText(post.getLikeCount() + " 좋아요");
        holder.musicTitleTextView.setText(post.getTrackName());
        holder.musicArtistTextView.setText(post.getArtistName());

        List<String> comments = postCommentsMap.get(post.getPostId());
        if (comments == null) {
            comments = new ArrayList<>();
        }
        holder.setCommentCount(comments.size());

        CommentAdapter commentAdapter = new CommentAdapter(comments);
        holder.commentsRecyclerView.setAdapter(commentAdapter);
        holder.commentsRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));

        holder.commentsRecyclerView.post(() -> {
            holder.commentsRecyclerView.scrollToPosition(commentAdapter.getItemCount() - 1);
        });

        // Set up heart button
        Boolean isLiked = postLikesMap.get(post.getPostId());
        if (isLiked == null) {
            isLiked = false;
        }
        if (isLiked) {
            holder.heartFilledImageView.setVisibility(View.VISIBLE);
            holder.heartOutlineImageView.setVisibility(View.INVISIBLE);
        } else {
            holder.heartFilledImageView.setVisibility(View.INVISIBLE);
            holder.heartOutlineImageView.setVisibility(View.VISIBLE);
        }

        holder.heartOutlineImageView.setOnClickListener(v -> {
            holder.heartFilledImageView.setVisibility(View.VISIBLE);
            holder.heartOutlineImageView.setVisibility(View.INVISIBLE);
            postLikesMap.put(post.getPostId(), true);
            updateLikesInFirebase(post.getPostId(), post.getLikeCount() + 1);
        });

        holder.heartFilledImageView.setOnClickListener(v -> {
            holder.heartFilledImageView.setVisibility(View.INVISIBLE);
            holder.heartOutlineImageView.setVisibility(View.VISIBLE);
            postLikesMap.put(post.getPostId(), false);
            updateLikesInFirebase(post.getPostId(), post.getLikeCount() - 1);
        });

        holder.postCommentButton.setOnClickListener(v -> {
            String newComment = holder.commentInput.getText().toString().trim();
            if (!newComment.isEmpty()) {
                addCommentToFirebase(post.getPostId(), newComment);
                holder.commentInput.setText("");
            }
        });

        holder.writeImageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, WritelistActivity.class);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void updatePostComments(String postId, List<String> comments) {
        postCommentsMap.put(postId, comments);
        notifyDataSetChanged();
    }

    private void addCommentToFirebase(String postId, String comment) {
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("spotify_post").child(postId).child("comments");
        String commentId = commentsRef.push().getKey();
        commentsRef.child(commentId).setValue(comment)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        refreshComments(postId);
                    }
                });
    }

    private void updateLikesInFirebase(String postId, int newLikeCount) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("spotify_post").child(postId);
        postRef.child("likeCount").setValue(newLikeCount)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        // 핸들러 에러코드
                    }
                });
    }

    private void refreshComments(String postId) {
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("spotify_post").child(postId).child("comments");
        commentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> updatedComments = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String updatedComment = snapshot.getValue(String.class);
                    if (updatedComment != null) {
                        updatedComments.add(updatedComment);
                    }
                }
                updatePostComments(postId, updatedComments);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 핸들러 possible 에러
            }
        });
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView postContentTextView;
        TextView likesCountTextView;
        TextView musicTitleTextView;
        TextView musicArtistTextView;
        RecyclerView commentsRecyclerView;
        EditText commentInput;
        Button postCommentButton;
        TextView viewCommentsTextView;
        ImageView heartOutlineImageView;
        ImageView heartFilledImageView;
        ImageView writeImageView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.username);
            postContentTextView = itemView.findViewById(R.id.post_content);
            likesCountTextView = itemView.findViewById(R.id.likes_count);
            musicTitleTextView = itemView.findViewById(R.id.music_title);
            musicArtistTextView = itemView.findViewById(R.id.music_artist);
            commentsRecyclerView = itemView.findViewById(R.id.comments);
            commentInput = itemView.findViewById(R.id.comment_input);
            postCommentButton = itemView.findViewById(R.id.post_comment_button);
            viewCommentsTextView = itemView.findViewById(R.id.view_comments);
            heartOutlineImageView = itemView.findViewById(R.id.heart1);
            heartFilledImageView = itemView.findViewById(R.id.heart2);
            writeImageView = itemView.findViewById(R.id.write);
        }

        public void setCommentCount(int count) {
            viewCommentsTextView.setText(count + " 댓글");
        }
    }
}
