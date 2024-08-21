package com.example.myapplication;

public class Post1 {
    private String trackId;
    private String content;

    public Post1() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post1(String trackId, String content) {
        this.trackId = trackId;
        this.content = content;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
