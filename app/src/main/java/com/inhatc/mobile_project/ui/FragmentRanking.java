package com.inhatc.mobile_project.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    private TextView rankingWeek;

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


        rankingWeek = (TextView) view.findViewById(R.id.tv_rankingWeek);

        rv_ranking = (RecyclerView) view.findViewById(R.id.rankingRecyclerView);

        startDay.set(cToday.getTime().getYear(), cToday.getTime().getMonth(), cToday.getTime().getDate());      startDay.getTime();
        endDay.set(cToday.getTime().getYear(), cToday.getTime().getMonth(), cToday.getTime().getDate());      endDay.getTime();
        startDay.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        endDay.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        rankingWeek.setText(String.format("%02d월 %02d일 ~ %02d월 %02d일", startDay.getTime().getMonth()+1, startDay.getTime().getDate(), endDay.getTime().getMonth()+1, endDay.getTime().getDate()));


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
                // post 객체에 업데이트가 있을 때마다 UI를 업데이트

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){// 반복문으로 데이터 List를 추출해냄
                    String userID = snapshot.getKey(); // UID 값을 얻어서
                    starCntMap.put(userID, 0); // satrCnt를 세는 Map에 UID를 key값으로 정해서 넣음
                    for(DataSnapshot sn : snapshot.getChildren()){
                        Post post = sn.getValue(Post.class); // 해당 포스트 객체를 받아와서
                        starCntMap.put(userID, starCntMap.get(userID) + post.getStarCount());//사용자, 포스트 좋아요 갯수 더하기
                        userMap.put(userID, post); // 상용자 이름과 프로필 사진을 가져오기 위해 uid와 해당 post 정보를 받음
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
                //랭킹객체에 순위, 사용자 이름, 사용자 프로파일 이미지 url 값을 setting
                for(Entry<String, Integer> entry : list_entries) {
                    String userId = entry.getKey();
                    RankingItems rankingItems = new RankingItems();
                    rankingItems.setOrder(cnt);
                    rankingItems.setUserName(userMap.get(userId).getAuthor());
                    rankingItems.setUserProfile(userMap.get(userId).getProfileImg());
                    rankinglist.add(rankingItems);
                    cnt = cnt + 1;
                }
                rAdapter.setItem(rankinglist);//ranking 어댑터 set으로 ui 업데이트

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 실패시 메시지 츌력
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        databaseReference.addValueEventListener(postListener);

        return view;
    }
}
