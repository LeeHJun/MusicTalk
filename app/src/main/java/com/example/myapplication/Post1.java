package com.example.myapplication;

public class Post1 {
    private String trackId;
    private String content;

    // Constructor
    public Post1(String trackId, String content) {
        this.trackId = trackId;
        this.content = content;
    }

    // Getter for trackId
    public String getTrackId() {
        return trackId;
    }

    // Setter for trackId
    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    // Getter for content
    public String getContent() {
        return content;
    }

    // Setter for content
    public void setContent(String content) {
        this.content = content;
    }
}
