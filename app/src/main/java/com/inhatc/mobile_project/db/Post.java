package com.inhatc.mobile_project.db;

import android.net.Uri;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Post {
    private String uid;
    private String author;
    private String place;
    private String postcontent;
    private String title;
    private String downloadImgUri;
    private String profileImg;
    private int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String uid, String author, String place, String postcontent, String title, String downloadImgUri, String profileImg) {
        this.uid = uid;
        this.author = author;
        this.place = place;
        this.postcontent = postcontent;
        this.downloadImgUri = downloadImgUri;
        this.title = title;
        this.profileImg = profileImg;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("place", place);
        result.put("postcontent", postcontent);
        result.put("starCount", starCount);
        result.put("urlImage", downloadImgUri);
        result.put("stars", stars);
        result.put("title", title);
        result.put("profileImg", profileImg);

        return result;
    }


    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPostcontent() {
        return postcontent;
    }

    public void setPostcontent(String postcontent) {
        this.postcontent = postcontent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDownloadImgUri() {
        return downloadImgUri;
    }

    public void setDownloadImgUri(String downloadImgUri) {
        this.downloadImgUri = downloadImgUri;
    }

    public int getStarCount() {
        return starCount;
    }

    public void setStarCount(int starCount) {
        this.starCount = starCount;
    }
}
