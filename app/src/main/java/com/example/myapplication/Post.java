package com.example.myapplication;

public class Post {
    private String content;
    private String trackId;
    private int commentCount;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    // 세 개의 매개변수를 받는 생성자
    public Post(String content, String trackId, int commentCount) {
        this.content = content;
        this.trackId = trackId;
        this.commentCount = commentCount;
    }

    // 두 개의 매개변수를 받는 생성자 (commentCount는 0으로 기본값 설정)
    public Post(String content, String trackId) {
        this.content = content;
        this.trackId = trackId;
        this.commentCount = 0; // 기본 댓글 수는 0으로 설정
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
}
