package com.example.myapplication;

public class Post1 {
    private String artistName;
    private int commentCount;
    private String content;
    private int likeCount;
    private String trackId;
    private String trackName;
    private String userId; // 사용자 ID
    private String userName; // 사용자 이름
    private String postId; // 게시물 ID

    // 기본 생성자
    public Post1() {
        // Firebase Database에서 객체 생성 시 필요한 기본 생성자
    }

    // 필요한 매개변수를 받는 생성자
    public Post1(String userId, String userName, String postId, String content, String trackName, String artistName, String trackId, int likeCount, int commentCount) {
        this.userId = userId;
        this.userName = userName;
        this.postId = postId;
        this.trackId = trackId;
        this.content = content;
        this.trackName = trackName;
        this.artistName = artistName;
        this.commentCount = commentCount;
        this.likeCount = likeCount;
    }

    // Getter와 Setter 메서드
    public String getArtistName() { return artistName; }
    public void setArtistName(String artistName) { this.artistName = artistName; }
    public int getCommentCount() { return commentCount; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }
    public String getTrackId() { return trackId; }
    public void setTrackId(String trackId) { this.trackId = trackId; }
    public String getTrackName() { return trackName; }
    public void setTrackName(String trackName) { this.trackName = trackName; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }

    @Override
    public String toString() {
        return "Post1{" +
                "artistName='" + artistName + '\'' +
                ", commentCount=" + commentCount +
                ", content='" + content + '\'' +
                ", likeCount=" + likeCount +
                ", trackId='" + trackId + '\'' +
                ", trackName='" + trackName + '\'' +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", postId='" + postId + '\'' +
                '}';
    }
}
