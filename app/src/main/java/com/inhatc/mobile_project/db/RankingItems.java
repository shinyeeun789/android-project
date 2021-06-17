package com.inhatc.mobile_project.db;

public class RankingItems {
    private int order;
    private String userName;
    private String userProfile;

    public RankingItems(int order, String userName, String userProfile) {
        this.order = order;
        this.userName = userName;
        this.userProfile = userProfile;
    }
    public RankingItems(){

    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }
}
