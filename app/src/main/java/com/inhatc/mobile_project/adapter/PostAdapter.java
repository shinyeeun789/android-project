package com.inhatc.mobile_project.adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.inhatc.mobile_project.DownloadFilesTask;
import com.inhatc.mobile_project.R;
import com.inhatc.mobile_project.db.Post;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Geocoder geocoder;
    private ArrayList<Post> postItems;
    private Context mContext;
    private DatabaseReference mDatabase;
    private int index;
    private FirebaseUser user;
    private boolean isLikePost;

    private Map<String, Boolean> stars = new HashMap<>();

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
        geocoder = new Geocoder(mContext);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        return new ViewHolder(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(holder, postItems.get(position), position);
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


        }


        public void onBind(@NonNull ViewHolder holder, Post post, int position) {
            index = position;

            Glide.with(holder.itemView)
                    .load(post.getDownloadImgUri())
                    .into(postImage);
            Glide.with(holder.itemView)
                    .load(post.getProfileImg())
                    .into(posterProfile);
            poster_id.setText(post.getAuthor());
            tv_contents.setText(post.getPostcontent());

            try {
                List<Address> addressList = geocoder.getFromLocation(post.getmLatitude(), post.getmLongitude(), 10);
                tv_location.setText(addressList.get(0).getAddressLine(0)+"에서");
            } catch (IOException e) {
                e.printStackTrace();
            }
            likeCounter.setText(String.valueOf(post.getStarCount()));

            stars = post.getStars();
            if(stars.containsKey(user.getUid()) && stars.get(user.getUid())){
                //좋아요 표시 했으면
                isLikePost = true;
                likeImg.setImageResource(R.drawable.icon_good);
            }else {
                isLikePost = false;
                likeImg.setImageResource(R.drawable.icon_empty_good);
            }

            if(!(post.getUid().equals(user.getUid()))){
                likeImg.setActivated(false);
                //좋아요 클릭시
                likeImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //좋아요 취소
                        if(isLikePost){
                            isLikePost = false;
                            likeImg.setImageResource(R.drawable.icon_empty_good);
                            stars.put(user.getUid(), false);
                            post.setStarCount(post.getStarCount()-1);

                            Map<String, Object> taskMap = new HashMap<String, Object>();
                            taskMap.put("/posts/" + post.getPostId() + "/stars", stars);
                            taskMap.put("/posts/" + post.getPostId() + "/starCount", post.getStarCount());

                            taskMap.put("/user-posts/" + post.getUid() + "/" + post.getPostId() + "/stars", stars);
                            taskMap.put("/user-posts/" + post.getUid() + "/" + post.getPostId() + "/starCount", post.getStarCount());
                            mDatabase.updateChildren(taskMap);

                        }else {
                            //좋아요
                            isLikePost = true;
                            likeImg.setImageResource(R.drawable.icon_good);
                            post.setStarCount(post.getStarCount()+1);
                            likeCounter.setText(String.valueOf(post.getStarCount()));
                            stars.put(user.getUid(), true);


                            Map<String, Object> taskMap = new HashMap<String, Object>();
                            taskMap.put("/posts/" + post.getPostId() + "/stars", stars);
                            taskMap.put("/posts/" + post.getPostId() + "/starCount", post.getStarCount());

                            taskMap.put("/user-posts/" + post.getUid() + "/" + post.getPostId() + "/stars", stars);
                            taskMap.put("/user-posts/" + post.getUid() + "/" + post.getPostId() + "/starCount", post.getStarCount());
                            mDatabase.updateChildren(taskMap);

                        }

                    }
                });
            }

        }

    }



    // 루틴 목록들 보여줌
    public void setItem(ArrayList<Post> data) {
        postItems = data;
        notifyDataSetChanged();
    }


}


