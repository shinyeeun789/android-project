package com.inhatc.mobile_project.db;

import android.net.Uri;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Post {
    public String uid;
    public String author;
    public String place;
    public String userProfile;
    public String content;
    public String downloadImgUri;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String uid, String author, String place, String content, String downloadImgUri, String userProfile) {
        this.uid = uid;
        this.author = author;
        this.place = place;
        this.content = content;
        this.downloadImgUri = downloadImgUri;
        this.userProfile = userProfile;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("place", place);
        result.put("content", content);
        result.put("starCount", starCount);
        result.put("urlImage", downloadImgUri);
        result.put("profileImage", userProfile);
        result.put("stars", stars);

        return result;
    }
}
