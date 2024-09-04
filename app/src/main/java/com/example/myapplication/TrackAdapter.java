package com.example.myapplication;

import static android.app.Activity.RESULT_OK;

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

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder> {

    private final Context context;
    public final List<Track> trackList;

    public TrackAdapter(Context context, List<Track> trackList) {
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
        Track track = trackList.get(position);
        holder.trackName.setText(track.getName());
        holder.artistName.setText(track.getArtist());

        holder.albumImage.setImageResource(R.drawable.albume);

        holder.itemView.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("trackId", track.getId());
            resultIntent.putExtra("trackName", track.getName());
            resultIntent.putExtra("artistName", track.getArtist());
            resultIntent.putExtra("imageUrl", track.getImageUrl());
            ((SearchActivity) context).setResult(RESULT_OK, resultIntent);
            ((SearchActivity) context).finish();
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
