package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SimplePostAdapter extends RecyclerView.Adapter<SimplePostAdapter.PostViewHolder> {

    private List<Post1> postList;
    private Context context;

    public SimplePostAdapter(List<Post1> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_post_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post1 post = postList.get(position);
        holder.trackNameTextView.setText(post.getTrackName());
        holder.artistNameTextView.setText(post.getArtistName());
        holder.userNameTextView.setText(post.getUserName());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView trackNameTextView;
        TextView artistNameTextView;
        TextView userNameTextView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            trackNameTextView = itemView.findViewById(R.id.track_name);
            artistNameTextView = itemView.findViewById(R.id.artist_name);
            userNameTextView = itemView.findViewById(R.id.user_name);
        }
    }
}
