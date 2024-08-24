package com.example.myapplication;

public class Post {
    private String content;
    private String trackId;
    private int commentCount;
    private int likeCount; // 추가된 필드

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String content, String trackId, int commentCount, int likeCount) {
        this.content = content;
        this.trackId = trackId;
        this.commentCount = commentCount;
        this.likeCount = likeCount;
    }

    public Post(String content, String trackId) {
        this.content = content;
        this.trackId = trackId;
        this.commentCount = 0; // 기본 댓글 수는 0으로 설정
        this.likeCount = 0; // 기본 좋아요 수는 0으로 설정
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

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}
