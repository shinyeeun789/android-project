package com.inhatc.mobile_project.db;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.inhatc.mobile_project.ui.ProfileUpdateActivity;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@Parcel
public class MemberInfo implements MemberImple{

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

    @Override
    public void bringMemberInfo(String user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef =  db.collection("users").document(user);

        Source source = Source.CACHE;

//        docRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                name = snapshot.getValue(String.class);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        })

        docRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                if(doc.exists()){
                    name = doc.getString("name");
                    phonNum = doc.getString("phonNum");
                    birthDay = doc.getString("birth");
                    profimageURL = doc.getString("profimageURL");
                }
            }
        });
    }

}
