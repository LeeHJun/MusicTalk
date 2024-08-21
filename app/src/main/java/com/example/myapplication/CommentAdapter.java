package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends BaseAdapter {
    private final Context context;
    private final List<Comment> comments;

    public CommentAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = new ArrayList<>(comments);
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.commnet_item, parent, false);
        }

        Comment comment = comments.get(position);
        TextView usernameView = convertView.findViewById(R.id.comment_username);
        TextView textView = convertView.findViewById(R.id.comment_text);

        usernameView.setText(comment.getUsername());
        textView.setText(comment.getText());

        return convertView;
    }

    public void clear() {
        comments.clear();
    }

    public void addAll(List<Comment> newComments) {
        comments.addAll(newComments);
    }

    public void updateComments(List<Comment> newComments) {
        clear();
        addAll(newComments);
        notifyDataSetChanged();
    }
}
