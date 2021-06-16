package com.inhatc.mobile_project;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.inhatc.mobile_project.db.MemberInfo;

import org.parceler.Parcels;

public class LoddingActivity extends AppCompatActivity {
    final String TAG = "LoddingActivity";

    private Bundle bundle = new Bundle();
    private MemberInfo userInfo = new MemberInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lodding);

        Intent intent = getIntent();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef =  db.collection("users").document(user.getUid());


        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }

                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites() ? "Local" : "Server";
                if(snapshot != null && snapshot.exists()){
                    Log.d(TAG, source + "data: " +snapshot.getData());
                    userInfo = snapshot.toObject(MemberInfo.class);
                    bundle.putParcelable("userInfoData", Parcels.wrap(userInfo));
                    intent.putExtra("userInfoData", bundle);
                    setResult(RESULT_OK, intent);
                    finish();

                }else {
                    Log.d(TAG, source + " data: null");
                }
            }
        });

    }
}