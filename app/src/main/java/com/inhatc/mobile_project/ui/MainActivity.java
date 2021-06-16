package com.inhatc.mobile_project.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.inhatc.mobile_project.R;
import com.inhatc.mobile_project.db.MemberInfo;
import com.inhatc.mobile_project.db.Post;

import org.parceler.Parcels;

import java.io.Serializable;
import java.lang.reflect.Member;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    final String TAG="MainActivity";

    private FragmentHome fragmentHome = new FragmentHome();
    private FragmentRanking fragmentRanking = new FragmentRanking();
    private FragmentUser fragmentUser = new FragmentUser();
    private MemberInfo userInfo = new MemberInfo();

    Handler handler = new Handler();
    class handler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.e("handle", "handle");
            initView();
        }
    }


    @Override
    public void onBackPressed() {
        //메인 화면에서 뒤로가기 버튼 클릭 시 바로 종료
        super.onBackPressed();
        //finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // 현재 로그인 사용자 인스턴스 가져오기
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            //로그인이 안되있으면 로그인 화면으로
            goTomyActivity(LoginActivity.class, true);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef =  db.collection("users").document(user.getUid());

//        Task<DocumentSnapshot> task = docRef.get();
//        if(task.isSuccessful()){
//            DocumentSnapshot doc = task.getResult();
//            userInfo = doc.toObject(MemberInfo.class);
//        }
//
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                DocumentSnapshot doc = task.getResult();
//                if(doc.exists()){
//                    userInfo = doc.toObject(MemberInfo.class);
//                }
//            }
//        });


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
//                    Message msg = handler.obtainMessage();
//                    handler.sendMessage(msg);

                }else {
                    Log.d(TAG, source + " data: null");
                }
            }
        });
//        try
//        {
//            sleep(1500);
//        } catch (InterruptedException e)
//        {
//            e.printStackTrace();
//        }

        //initView();


    }

    private void initView() {
        // 프래그먼트 트랜잭션(프래그먼트 백 스택 관리, 프래그먼트 전환 애니메이션 설정 등) 시작

        // add(): 추가, remove(): 삭제, replace(): 전환
        // commit()은 activity가 state 저장하기 전에 이루어져야 함. 저장된 후 불리면 exception 발생
        // commitAllowingStateLoss(): 프래그먼트가 state 저장과 관련없게 작동되도록 사용
        replaceFragment(fragmentHome);
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());   // 메뉴 클릭에 대한 이벤트 처리
        bottomNavigationView.setSelectedItemId(R.id.page_home);     // 선택된 아이템 지정
    }

    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch(menuItem.getItemId()) {
                case R.id.page_home:
                    replaceFragment(fragmentHome);
                    break;
                case R.id.page_ranking:
                    replaceFragment(fragmentRanking);
                    break;
                case R.id.page_user:
                    replaceFragment(fragmentUser);
                    break;
            }
            return true;
        }
    }

    public void replaceFragment(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("userInfoData", Parcels.wrap(userInfo));
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment).commitAllowingStateLoss();
    }

    private void goTomyActivity(Class ac, boolean isbacktohome){
        Intent intent = new Intent(MainActivity.this, ac);
        // 뒤로가기 버튼 누르면 로그인 화면이나 회원가입 화면으로 이동
        //--> activity 기록 지워주어야 함--> flag 사용??
        if(isbacktohome){
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        startActivity(intent);
    }
}