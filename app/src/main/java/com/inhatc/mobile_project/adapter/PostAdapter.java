package com.inhatc.mobile_project.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.inhatc.mobile_project.DownloadFilesTask;
import com.inhatc.mobile_project.R;
import com.inhatc.mobile_project.db.Post;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private ArrayList<Post> postItems;
    private Context mContext;
    private DatabaseReference mDatabase;
    private int index;

    public PostAdapter(ArrayList<Post> postItems, Context context) {
        this.postItems = postItems;
        this.mContext = context;
    }

    public List<Post> getItems() {
        return postItems;
    }


    @Override
    public int getItemCount() {
        return (postItems != null ? postItems.size() : 0);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        mContext = parent.getContext();
        return new ViewHolder(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(holder, postItems.get(position), position);
        Glide.with(holder.itemView)
                .load(postItems.get(position).getDownloadImgUri())
                .into(holder.postImage);
        Glide.with(holder.itemView)
                .load(postItems.get(position).getProfileImg())
                .into(holder.posterProfile);
        holder.poster_id.setText(postItems.get(position).getAuthor());
        holder.tv_contents.setText(postItems.get(position).getPostcontent());
        holder.tv_location.setText(postItems.get(position).getPlace());
        holder.likeCounter.setText(String.valueOf(postItems.get(position).getStarCount()));

    }


    //뷰내용 설정
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView poster_id, tv_contents, tv_location, likeCounter;
        //private int index;
        private ImageView postImage, likeImg, posterProfile;

        public ViewHolder(@NonNull View view) {
            super(view);
            //context = view.findViewById(R.id.textView3);
            poster_id = view.findViewById(R.id.item_name);
            tv_contents = view.findViewById(R.id.item_content);
            tv_location = view.findViewById(R.id.item_place);
            postImage = view.findViewById(R.id.item_image);
            posterProfile = view.findViewById(R.id.item_profile);
            likeCounter = view.findViewById(R.id.item_goodCount);
            likeImg = view.findViewById(R.id.item_goodMark);

            likeImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //클릭 시 좋아요 표시
                }
            });

        }


        public void onBind(@NonNull ViewHolder holder, Post post, int position) {
            index = position;
//            DownloadFilesTask img = new DownloadFilesTask();
////            img.setImgView(postImage);
////            img.execute(post.getDownloadImgUri());
//            img.setImgView(posterProfile);
//            img.execute(post.getProfileImg());
        }

    }

    // 루틴 목록들 보여줌
    public void setItem(ArrayList<Post> data) {
        postItems = data;
        notifyDataSetChanged();
    }

//    private void initDatabase(){
//        mChild
//    }

}


