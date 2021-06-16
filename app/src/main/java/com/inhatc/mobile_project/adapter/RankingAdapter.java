package com.inhatc.mobile_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.inhatc.mobile_project.R;
import com.inhatc.mobile_project.db.Post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder> {

    private Context mContext;
    private DatabaseReference pDatabase;
    private int index;
    private FirebaseUser user;
    private boolean isLikePost;

    private ArrayList<Post> rankingItems;


    private HashMap<String, Post> userMap = new HashMap<String, Post>();
    private ArrayList<Entry<String, Integer>> list_entries;
    private List<Post> list_order;

    private int cnt = 0;

    //어댑터 연결,  HashMap<String, Post>() userMap , list_entries ArrayList<Entry<String, Integer>>


    public RankingAdapter(HashMap<String, Post> userMap, ArrayList<Entry<String, Integer>> list_order, Context context) {
        this.userMap = userMap;
        this.mContext = context;
        this.list_entries = list_order;
    }

    public ArrayList<Post> getItems() {
        return rankingItems;
    }


    @Override
    public int getItemCount() {
        return (rankingItems != null ? rankingItems.size() : 0);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.ranking_recyle_itema, parent, false);
        mContext = parent.getContext();


        return new ViewHolder(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        holder.onBind(holder, postItems.get(position), position);
//        cnt =0;
//        list_entries.get()
//        for(Entry<String, Integer> entry : list_entries){
//            rankingItems.setOrder(cnt+1);
//            rankingItems.setUserName(userMap.get(entry.getKey()).getAuthor());
//            rankingItems.setUserProfile(userMap.get(entry.getKey()).getProfileImg());
//            HashMap<String, String> users = new HashMap<String, String>();
//            cnt ++;
//        }

    }


    //뷰내용 설정
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView poster_id, tv_contents, tv_location, likeCounter;
        //private int index;
        private ImageView postImage, likeImg, posterProfile;

        public ViewHolder(@NonNull View view) {
            super(view);


        }


        public void onBind(@NonNull ViewHolder holder, Post post, int position) {

        }


    }



    // 루틴 목록들 보여줌
    public void setItem(ArrayList<Post> data) {
        rankingItems = data;
        notifyDataSetChanged();
    }


}


