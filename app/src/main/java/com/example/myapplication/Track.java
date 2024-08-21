package com.example.myapplication;

public class Track {
    private String id;
    private String name;
    private String artist;
    private String imageUrl;

    public Track(String id, String name, String artist, String imageUrl) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
