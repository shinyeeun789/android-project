package com.inhatc.mobile_project.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inhatc.mobile_project.R;
import com.inhatc.mobile_project.db.MemberInfo;
import com.inhatc.mobile_project.db.Post;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FragmentRanking extends Fragment {

    final String TAG = "FragmentRankin";

    private Calendar cToday = Calendar.getInstance(), startDay = Calendar.getInstance(), endDay = Calendar.getInstance();
    private TextView rankingWeek;
    private Button test;


    private FirebaseDatabase rDatabase;
    private DatabaseReference databaseReference;
    private ArrayList<Post> postarray;
    private HashMap<String, Post> userMap = new HashMap<String, Post>();
    private HashMap<String, Integer> starCntMap = new HashMap<String, Integer>();
    private ArrayList<String> usersArray = new ArrayList<String>();
    private List<Entry<String, Integer>> list_entries;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);


        rankingWeek = (TextView) view.findViewById(R.id.tv_rankingWeek);
        test = (Button) view.findViewById(R.id.test);
        //pRv_posts = (RecyclerView) view.findViewById(R.id.homeRecyclerView);

        startDay.set(cToday.getTime().getYear(), cToday.getTime().getMonth(), cToday.getTime().getDate());      startDay.getTime();
        endDay.set(cToday.getTime().getYear(), cToday.getTime().getMonth(), cToday.getTime().getDate());      endDay.getTime();
        startDay.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        endDay.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        rankingWeek.setText(String.format("%02d월 %02d일 ~ %02d월 %02d일", startDay.getTime().getMonth()+1, startDay.getTime().getDate(), endDay.getTime().getMonth()+1, endDay.getTime().getDate()));

        // 구글맵 테스트!!! 테스트 후 삭제 예정
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), MapActivity.class);
                startActivity(i);
            }
        });

        // 좋아요 수 계산

//        pRv_posts.setHasFixedSize(true);// 리사이클러뷰 기존성능 강화
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
//        pRv_posts.setLayoutManager(layoutManager);

        rDatabase = FirebaseDatabase.getInstance();
        databaseReference = rDatabase.getReference("user-posts");//DB연결
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){// 반복문으로 데이터 List를 추출해냄
                    String userID = snapshot.getKey();
                    starCntMap.put(userID, 0);
                    for(DataSnapshot sn : snapshot.getChildren()){
                        Post post = sn.getValue(Post.class);
                        starCntMap.put(userID, starCntMap.get(userID) + post.getStarCount());//사용자, 포스트 좋아요 갯수 더하기
                        userMap.put(userID, post);
                    }
                }
                list_entries =new ArrayList<Entry<String, Integer>>(starCntMap.entrySet());
                // 비교함수 Comparator를 사용하여 오름차순으로 정렬
                Collections.sort(list_entries, new Comparator<Entry<String, Integer>>() {
                    // compare로 값을 비교
                    public int compare(Entry<String, Integer> obj1, Entry<String, Integer> obj2) {
                        // 오름 차순 정렬
                        return obj2.getValue().compareTo(obj1.getValue());
                    }
                });
                //
                for(Entry<String, Integer> entry : list_entries) {
                    Log.e("정렬된 값: ",entry.getKey() + " : " + entry.getValue());
                    //System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                Log.e("??: ",list_entries.toString());
                //어댑터 연결,  HashMap<String, Post>() userMap , list_entries ArrayList<Entry<String, Integer>>

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        databaseReference.addValueEventListener(postListener);

        return view;
    }
}
