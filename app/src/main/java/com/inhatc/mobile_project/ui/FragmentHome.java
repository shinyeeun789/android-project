package com.inhatc.mobile_project.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.inhatc.mobile_project.DownloadFilesTask;
import com.inhatc.mobile_project.R;
import com.inhatc.mobile_project.adapter.PostAdapter;
import com.inhatc.mobile_project.db.MemberInfo;
import com.inhatc.mobile_project.db.Post;

import org.parceler.Parcels;

import java.util.ArrayList;

public class FragmentHome extends Fragment implements View.OnClickListener {
    final String TAG = "FragementHome";

    private Button btnGoWrite;
    private MemberInfo userInfo = new MemberInfo();
    private FirebaseDatabase pDatabase;
    private PostAdapter pAdapter;
    private DatabaseReference databaseReference;
    private Bundle bundle = new Bundle();

    private RecyclerView mRv_posts;
    private ArrayList<Post> postarray;

    private String userName;
    private String userPrfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        bundle = getArguments();
        if(bundle != null){
            Object value = Parcels.unwrap(bundle.getParcelable("userInfoData"));
            userInfo = (MemberInfo) value;
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef =  db.collection("users").document(user.getUid());
//        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
//                if(error != null){
//                    Log.w(TAG, "Listen failed.", error);
//                    return;
//                }
//
//                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites() ? "Local" : "Server";
//                if(snapshot != null && snapshot.exists()){
//                    Log.d(TAG, source + "data: " +snapshot.getData());
//                    userName = snapshot.getString("name");
//                    userPrfile = snapshot.getString("profimageURL");
//
//                }else {
//                    Log.d(TAG, source + " data: null");
//                }
//
//            }
//
//        });


//        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//
//                if(userInfo.getProfimageURL() != null){
//                    userInfo = documentSnapshot.toObject(MemberInfo.class);
//                    bundle.putParcelable("userInfoData", Parcels.wrap(userInfo));
//                }
//
//
//            }
//        });

        btnGoWrite = (Button) view.findViewById(R.id.btnGoWrite);
        btnGoWrite.setOnClickListener(this);
        
        mRv_posts = (RecyclerView) view.findViewById(R.id.homeRecyclerView);



        mRv_posts.setHasFixedSize(true);// 리사이클러뷰 기존성능 강화
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRv_posts.setLayoutManager(layoutManager);
        postarray = new ArrayList<>();//post 객체 담을 어레이 리스트 (어댑터 쪽으로)

        pDatabase = FirebaseDatabase.getInstance();// 파이어베이스 데이터베이스 연동
        databaseReference = pDatabase.getReference("posts"); // DB 테이블 연결

        pAdapter = new PostAdapter(postarray, getContext());
        mRv_posts.setAdapter(pAdapter); //리사이클 뷰에 어댑터 연결

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){// 반복문으로 데이터 List를 추출해냄
                    Post post = snapshot.getValue(Post.class); // Post 객체에 데이터 담기
                    postarray.add(post);
                }
                pAdapter.setItem(postarray);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        databaseReference.addValueEventListener(postListener);


//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
//                postarray.clear();
//                for (DataSnapshot snapshot : datasnapshot.getChildren()){// 반복문으로 데이터 List를 추출해냄
//                    Post post = snapshot.getValue(Post.class); // Post 객체에 데이터 담기
//                    postarray.add(post);
//                }
//                //pAdapter.notifyDataSetChanged();// 리스트 저장 및 새로 고침해야 반영이 됨
//                pAdapter.setItem(postarray);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("FramentHome", String.valueOf(error.toException()));// 디비 에러
//            }
//        });
//        pAdapter = new PostAdapter(postarray, getContext());
//        mRv_posts.setAdapter(pAdapter); //리사이클 뷰에 어댑터 연결



        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnGoWrite:
                Intent intentWrite = new Intent(getActivity(), WriteActivity.class);
//                intentWrite.putExtra("userName", userName);
//                intentWrite.putExtra("userProfile", userPrfile);
                intentWrite.putExtra("userInfoData",bundle);
                startActivity(intentWrite);
                break;
        }
    }

    private void addPostEventListener(DatabaseReference mPostReference){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Post post = dataSnapshot.getValue(Post.class);
                // ..
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mPostReference.addValueEventListener(postListener);
    }
}
