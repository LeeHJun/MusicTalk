package com.example.myapplication;

public class SingerItem2 {
    private String name;
    private String mobile;
    private int resId;
    private int commentCount = 0; // 기본값 0 설정
    private int likeCount = 0;    // 기본값 0 설정
    private String userId;
    private String postId;
    private String boardName;     // 게시판 이름을 나타내는 매개변수 추가

    // 기본 생성자 (Firebase에서 객체를 역직렬화할 때 필요)
    public SingerItem2() {
        // 빈 생성자
    }

    // 매개변수를 받는 생성자
    public SingerItem2(String name, String mobile, int resId, int commentCount, int likeCount, String userId, String postId, String boardName) {
        this.name = name;
        this.mobile = mobile;
        this.resId = resId;
        this.commentCount = commentCount;
        this.likeCount = likeCount;
        this.userId = userId;
        this.postId = postId;
        this.boardName = boardName; // boardName 필드 초기화
    }

    // Getter와 Setter 메서드
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    @Override
    public String toString() {
        return "SingerItem2{" +
                "name='" + name + '\'' +
                ", mobile='" + mobile + '\'' +
                ", resId=" + resId +
                ", commentCount=" + commentCount +
                ", likeCount=" + likeCount +
                ", userId='" + userId + '\'' +
                ", postId='" + postId + '\'' +
                ", boardName='" + boardName + '\'' + // boardName 필드를 toString에 추가
                '}';
    }
}
