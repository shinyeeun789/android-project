package com.inhatc.mobile_project.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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
import java.util.Collection;
import java.util.Collections;

import static com.inhatc.mobile_project.R.drawable.material_cursor_drawable;
import static com.inhatc.mobile_project.R.drawable.select_background;
import static com.inhatc.mobile_project.R.drawable.unselect_background;

public class FragmentHome extends Fragment implements View.OnClickListener {
    final String TAG = "FragementHome";


    private Button btnGoWrite;
    private MemberInfo userInfo = new MemberInfo();

    private FirebaseUser user;
    private FirebaseDatabase pDatabase;
    private PostAdapter pAdapter;
    private DatabaseReference databaseReference;
    private Bundle bundle = new Bundle();

    private RecyclerView pRv_posts;
    private ArrayList<Post> postarray;

    private TextView btn_allpost, btn_mypot;

    private boolean isAllPost = true;

    Handler handler = new Handler();
    class handler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.e("handle", "handle");
            showUI();

        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //사용자 정보있는 번들 값 가져오기(userInfo)
        bundle = getArguments();
        if(bundle != null){
            Object value = Parcels.unwrap(bundle.getParcelable("userInfoData"));
            userInfo = (MemberInfo) value;
        }
        user = FirebaseAuth.getInstance().getCurrentUser();//현재 사용자 가져오기


        btnGoWrite = (Button) view.findViewById(R.id.btnGoWrite);
        btn_allpost = view.findViewById(R.id.btn_allpost);
        btn_mypot = view.findViewById(R.id.btn_mypot);

        btnGoWrite.setOnClickListener(this);
        btn_allpost.setOnClickListener(this);
        btn_mypot.setOnClickListener(this);

        pRv_posts = (RecyclerView) view.findViewById(R.id.homeRecyclerView);
        showUI();


        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnGoWrite:
                Intent intentWrite = new Intent(getActivity(), WriteActivity.class);;
                intentWrite.putExtra("userInfoData",bundle);
                startActivity(intentWrite);
                isAllPost = true;
                break;
            case R.id.btn_allpost:
                isAllPost = true;
                custeomBtn();
                break;
            case R.id.btn_mypot:
                isAllPost = false;
                custeomBtn();
                break;
        }
    }

    private void showUI() {
        pRv_posts.setHasFixedSize(true);// 리사이클러뷰 기존성능 강화
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        pRv_posts.setLayoutManager(layoutManager);
        postarray = new ArrayList<>();//post 객체 담을 어레이 리스트 (어댑터 쪽으로)

        pDatabase = FirebaseDatabase.getInstance();// 파이어베이스 데이터베이스 연동

        if(isAllPost){
            databaseReference = pDatabase.getReference("posts"); // DB 테이블 연결
//            pAdapter = new PostAdapter(postarray, pDatabase.getReference("user-posts").child(user.getUid()), getContext());
        }else{
            databaseReference = pDatabase.getReference("user-posts").child(user.getUid()); // DB 테이블 연결
        }


        pAdapter = new PostAdapter(postarray, getContext());

        pRv_posts.setAdapter(pAdapter); //리사이클 뷰에 어댑터 연결

        //데이터변경 있을때마다
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                isAllPost = true;
                postarray.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){// 반복문으로 데이터 List를 추출해냄
                    Post post = snapshot.getValue(Post.class); // Post 객체에 데이터 담기
                    postarray.add(post);
                }
                Collections.reverse(postarray);//postarray 순서 변경
                pAdapter.setItem(postarray);//어댑터에 post객체 어레이 set

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        databaseReference.addValueEventListener(postListener);

    }

    private void custeomBtn() {
        if(isAllPost){
            btn_allpost.setBackground(ContextCompat.getDrawable(getContext(), select_background));
            btn_allpost.setTextColor(Color.parseColor("#FFFFFF"));

            btn_mypot.setBackground(ContextCompat.getDrawable(getContext(), unselect_background));
            btn_mypot.setTextColor(Color.parseColor("#000000"));
        }else {
            btn_mypot.setBackground(ContextCompat.getDrawable(getContext(), select_background));
            btn_mypot.setTextColor(Color.parseColor("#FFFFFF"));

            btn_allpost.setBackground(ContextCompat.getDrawable(getContext(), unselect_background));
            btn_allpost.setTextColor(Color.parseColor("#000000"));
        }

        showUI();
    }


}
