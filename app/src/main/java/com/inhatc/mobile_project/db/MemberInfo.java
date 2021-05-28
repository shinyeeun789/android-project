package com.inhatc.mobile_project.db;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

public class MemberInfo implements MemberImple{

    private String name;
    private String phonNum;
    private String birthDay;


    public MemberInfo(String name, String phonNum, String birthDay){
        this.name = name;
        this.phonNum = phonNum;
        this.birthDay = birthDay;
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

    public String getBirth() {
        return birthDay;
    }

    public void setBirth(String birthDay) {
        this.birthDay = birthDay;
    }

    @Override
    public String toString() {
        return "MemberInfo{" +
                "name='" + name + '\'' +
                ", phonNum='" + phonNum + '\'' +
                ", birthDay='" + birthDay + '\'' +
                '}';
    }


    @Override
    public void bringMemberInfo(String user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef =  db.collection("users").document(user);

        Source source = Source.CACHE;

        docRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                if(doc.exists()){
                    name = doc.getString("name");
                    phonNum = doc.getString("phonNum");
                    birthDay = doc.getString("birth");
                }
            }
        });
    }
}
