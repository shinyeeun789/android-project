package com.inhatc.mobile_project;


import java.util.Date;

public class MemberInfo {

    private String name;
    private String phonNum;
    private String birthDay;


    public MemberInfo(String name, String phonNum, String birthDay){
        this.name = name;
        this.phonNum = phonNum;
        this.birthDay = birthDay;
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

    public String getBirth() {
        return birthDay;
    }

    public void setBirth(String birthDay) {
        this.birthDay = birthDay;
    }
}
