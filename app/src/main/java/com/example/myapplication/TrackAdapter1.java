package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TrackAdapter1 extends RecyclerView.Adapter<TrackAdapter1.ViewHolder> {

    private final Context context;
    public final List<Track1> trackList;

    public TrackAdapter1(Context context, List<Track1> trackList) {
        this.context = context;
        this.trackList = trackList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_track, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Track1 track = trackList.get(position);
        holder.trackName.setText(track.getName());
        holder.artistName.setText(track.getArtist());

        holder.albumImage.setImageResource(R.drawable.albume);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra("url", track.getId()); // "url"로 인텐트 키 설정
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return trackList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView trackName;
        private final TextView artistName;
        private final ImageView albumImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            trackName = itemView.findViewById(R.id.track_name);
            artistName = itemView.findViewById(R.id.artist_name);
            albumImage = itemView.findViewById(R.id.album_image);
        }
    }
}
