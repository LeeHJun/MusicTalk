package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {

    public List<Track> trackList;
    private Context context;

    public TrackAdapter(Context context, List<Track> trackList) {
        this.context = context;
        this.trackList = trackList;
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_track, parent, false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
        Track track = trackList.get(position);
        holder.trackName.setText(track.getName());
        holder.artistName.setText(track.getArtist());
        Picasso.get().load(track.getImageUrl()).into(holder.trackImage);
    }

    @Override
    public int getItemCount() {
        return trackList.size();
    }

    public static class TrackViewHolder extends RecyclerView.ViewHolder {
        ImageView trackImage;
        TextView trackName;
        TextView artistName;

        public TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            trackImage = itemView.findViewById(R.id.track_image);
            trackName = itemView.findViewById(R.id.track_name);
            artistName = itemView.findViewById(R.id.artist_name);
        }
    }
}
