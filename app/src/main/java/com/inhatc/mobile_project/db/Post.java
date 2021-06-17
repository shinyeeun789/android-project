package com.inhatc.mobile_project.db;

import android.location.Address;
import android.net.Uri;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
@Parcel
public class Post {
    private String uid;
    private String author;
    private String postId;
    private String postcontent;
    private String title;
    private String downloadImgUri;
    private String profileImg;
    private double mLatitude;
    private double mLongitude;
    private int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    @ParcelConstructor
    public Post(String postId, String uid, String author, double mLatitude, double mLongitude, String postcontent, String downloadImgUri, String profileImg) {
        this.postId = postId;
        this.uid = uid;
        this.author = author;
        this.postcontent = postcontent;
        this.downloadImgUri = downloadImgUri;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.profileImg = profileImg;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("postId", postId);
        result.put("uid", uid);
        result.put("author", author);
        result.put("mLatitude", mLatitude);
        result.put("mLongitude", mLongitude);
        result.put("postcontent", postcontent);
        result.put("starCount", starCount);
        result.put("downloadImgUri", downloadImgUri);
        result.put("stars", stars);
        result.put("title", title);
        result.put("profileImg", profileImg);

        return result;
    }


    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
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

    public double getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public double getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
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

    public Map<String, Boolean> getStars() {
        return stars;
    }

    public void setStars(Map<String, Boolean> stars) {
        this.stars = stars;
    }

}
