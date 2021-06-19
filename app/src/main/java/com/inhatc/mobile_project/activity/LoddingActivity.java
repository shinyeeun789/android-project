package com.inhatc.mobile_project.activity;

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
import com.inhatc.mobile_project.R;
import com.inhatc.mobile_project.db.MemberInfo;

import org.parceler.Parcels;

/*사용자 정보를 받아오기 위한 lodingactivity*/
public class LoddingActivity extends AppCompatActivity {
    final String TAG = "LoddingActivity";

    private Bundle bundle = new Bundle();
    private MemberInfo userInfo = new MemberInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lodding);

        Intent intent = getIntent();

        //사용자 정보를 받아오기 위해 DocumentReference 객체 생성
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

                //사용자 정보를 잘 받아오면
                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites() ? "Local" : "Server";
                if(snapshot != null && snapshot.exists()){
                    Log.d(TAG, source + "data: " +snapshot.getData());
                    userInfo = snapshot.toObject(MemberInfo.class); //받아오는 값을 userInfo 객체에 넣고
                    bundle.putParcelable("userInfoData", Parcels.wrap(userInfo)); //userInfo를 번들로 묶고
                    intent.putExtra("userInfoData", bundle); // 번들을 intent에 put함
                    setResult(RESULT_OK, intent);
                    finish();

                }else {
                    Log.d(TAG, source + " data: null");
                }
            }
        });

    }
}