package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SimplePostAdapter2 extends RecyclerView.Adapter<SimplePostAdapter2.ViewHolder> {

    private final List<SingerItem2> boardPostList;
    private final Context context;

    public SimplePostAdapter2(List<SingerItem2> boardPostList, Context context) {
        this.boardPostList = boardPostList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_board_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SingerItem2 post = boardPostList.get(position);
        holder.boardTextView.setText(post.getBoardName());
        holder.titleTextView.setText(post.getName());
        holder.contentTextView.setText(post.getMobile());
    }

    @Override
    public int getItemCount() {
        return boardPostList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView boardTextView, titleTextView, contentTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            boardTextView = itemView.findViewById(R.id.text_view_board);
            titleTextView = itemView.findViewById(R.id.text_view_title);
            contentTextView = itemView.findViewById(R.id.text_view_content);
        }
    }
}
