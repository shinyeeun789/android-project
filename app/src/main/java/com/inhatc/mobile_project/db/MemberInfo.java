package com.inhatc.mobile_project.db;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel
public class MemberInfo {

    final String TAG = "MemberIfo";

    private String name;
    private String phonNum;
    private String birthDay;
    private String profimageURL;


    @ParcelConstructor
    public MemberInfo(String name, String phonNum, String birthDay, String profimageURL){
        this.name = name;
        this.phonNum = phonNum;
        this.birthDay = birthDay;
        this.profimageURL = profimageURL;
    }

    public MemberInfo(){
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhonNum() {
        return phonNum;
    }

    public void setPhonNum(String phonNum) {
        this.phonNum = phonNum;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getProfimageURL() {
        return profimageURL;
    }

    public void setProfimageURL(String profimageURL) {
        this.profimageURL = profimageURL;
    }

    @Override
    public String toString() {
        return "MemberInfo{" +
                "name='" + name + '\'' +
                ", phonNum='" + phonNum + '\'' +
                ", birthDay='" + birthDay + '\'' +
                '}';
    }

}
