package com.inhatc.mobile_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.inhatc.mobile_project.R;
import com.inhatc.mobile_project.RankingItems;
import com.inhatc.mobile_project.db.Post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder> {

    private Context mContext;
    private int index;

    private ArrayList<RankingItems> rankingItems;



    public RankingAdapter(ArrayList<RankingItems> rankingItems, Context context) {
        this.rankingItems = rankingItems;
        this.mContext = context;
    }

    public ArrayList<RankingItems> getItems() {
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
        holder.onBind(holder, rankingItems.get(position), position);
    }


    //뷰내용 설정
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_ranking, tv_user;
        //private int index;
        private ImageView userProfile;

        public ViewHolder(@NonNull View view) {
            super(view);
            tv_ranking = view.findViewById(R.id.ranking_order);
            tv_user =  view.findViewById(R.id.ranking_user);
            userProfile = view.findViewById(R.id.ranking_profile);
        }


        public void onBind(@NonNull ViewHolder holder, RankingItems data, int position) {
            index = position;
            tv_ranking.setText(String.valueOf(data.getOrder()));
            tv_user.setText(data.getUserName());
            Glide.with(holder.itemView)
                    .load(data.getUserProfile())
                    .into(userProfile);
        }


    }


    // 루틴 목록들 보여줌
    public void setItem(ArrayList<RankingItems> data) {
        rankingItems = data;
        notifyDataSetChanged();
    }


}


