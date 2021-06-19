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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inhatc.mobile_project.R;
import com.inhatc.mobile_project.db.RankingItems;
import com.inhatc.mobile_project.adapter.RankingAdapter;
import com.inhatc.mobile_project.db.Post;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class FragmentRanking extends Fragment {

    final String TAG = "FragmentRankin";

    private Calendar cToday = Calendar.getInstance(), startDay = Calendar.getInstance(), endDay = Calendar.getInstance();

    private RankingAdapter rAdapter;
    private RecyclerView rv_ranking;

    private FirebaseDatabase rDatabase;
    private DatabaseReference databaseReference;
    private HashMap<String, Post> userMap = new HashMap<String, Post>();
    private HashMap<String, Integer> starCntMap = new HashMap<String, Integer>();
    private List<Entry<String, Integer>> list_entries;
    private ArrayList<RankingItems> rankinglist;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);

        rv_ranking = (RecyclerView) view.findViewById(R.id.rankingRecyclerView);

        // 좋아요 수 계산
        rv_ranking.setHasFixedSize(true);// 리사이클러뷰 기존성능 강화
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rv_ranking.setLayoutManager(layoutManager);
        rankinglist = new ArrayList<>();// ranking 객체 담을 리스트

        //어댑터 연결
        rAdapter = new RankingAdapter(rankinglist, getContext());
        rv_ranking.setAdapter(rAdapter);

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
                int cnt = 1;
                for(Entry<String, Integer> entry : list_entries) {
                    String userId = entry.getKey();
                    Log.e("정렬된 값: ",entry.getKey() + " : " + entry.getValue());
                    RankingItems rankingItems = new RankingItems();
                    rankingItems.setOrder(cnt);
                    rankingItems.setUserName(userMap.get(userId).getAuthor());
                    rankingItems.setUserProfile(userMap.get(userId).getProfileImg());
                    rankinglist.add(rankingItems);
                    cnt = cnt + 1;
                }
                //Log.e("??: ",list_entries.toString());
                //어댑터 연결,  HashMap<String, Post>() userMap , list_entries ArrayList<Entry<String, Integer>>
                rAdapter.setItem(rankinglist);

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
