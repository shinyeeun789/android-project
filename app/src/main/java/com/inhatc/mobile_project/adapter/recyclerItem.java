package com.inhatc.mobile_project.adapter;

public class recyclerItem {
    private String userImage;           // profile 이미지
    private String userName;            // 사용자 이름
    private String postImage;           // 올린 이미지
    private String good;                // 좋아요 표시
    private int goodCount;              // 좋아요 개수
    private String place;               // 장소
    private String content;             // 글 내용

    public recyclerItem(String userImage, String userName, String postImage, String good, int goodCount, String place, String content) {
        this.userImage = userImage;
        this.userName = userName;
        this.postImage = postImage;
        this.good = good;
        this.goodCount = goodCount;
        this.place = place;
        this.content = content;
    }

    public String getUserImage() {
        return userImage;
    }
    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPostImage() {
        return postImage;
    }
    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }
    public String getGood() {
        return good;
    }
    public void setGood(String good) {
        this.good = good;
    }
    public int getGoodCount() {
        return goodCount;
    }
    public void setGoodCount(int goodCount) {
        this.goodCount = goodCount;
    }
    public String getPlace() {
        return place;
    }
    public void setPlace(String place) {
        this.place = place;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}
