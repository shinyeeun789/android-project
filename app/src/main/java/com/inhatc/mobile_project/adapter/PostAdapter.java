package com.inhatc.mobile_project.adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.inhatc.mobile_project.DownloadFilesTask;
import com.inhatc.mobile_project.R;
import com.inhatc.mobile_project.db.Post;
import com.inhatc.mobile_project.ui.MapActivity;
import com.inhatc.mobile_project.ui.WriteActivity;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    private Geocoder geocoder;
    private ArrayList<Post> postItems;
    private Context mContext;
    private DatabaseReference pDatabase;

    //현재 보여지고 있는 리스트의 데이터 편집 말고
    // 예를 들면 post가 보여지고 이를 편집, 삭제 시 user-post도 같이 편집 삭제 가능하게
   // private DatabaseReference subDatabaseRef;

    private int index;
    private FirebaseUser user;
    private boolean isLikePost;


    private Map<String, Boolean> stars = new HashMap<>();


    public PostAdapter(ArrayList<Post> postItems, Context context) {
        this.postItems = postItems;
        //this.subDatabaseRef = subDatabaseRef;
        this.mContext = context;
    }

    public ArrayList<Post> getItems() {
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
        pDatabase = FirebaseDatabase.getInstance().getReference();
        return new ViewHolder(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(holder, postItems.get(position), position);

        //view 클릭시 구글 맵 표시holder.itemView
        // 이미지 클릭 시 이동은 holder.postImage.setOnClickListener로 바꾸면 됨
        holder.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(mContext, MapActivity.class);
                // 위도 경도 값 구하는거
                //postItems.get(position).getmLatitude()
                //postItems.get(position).getmLongitude()
                mContext.startActivity(i);

            }
        });
    }


    //뷰내용 설정
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView poster_id, tv_contents, tv_location, likeCounter;
        //private int index;
        private ImageView postImage, likeImg, posterProfile, btn_edit;

        public ViewHolder(@NonNull View view) {
            super(view);
            poster_id = view.findViewById(R.id.item_name);
            tv_contents = view.findViewById(R.id.item_content);
            tv_location = view.findViewById(R.id.item_place);
            postImage = view.findViewById(R.id.item_image);
            posterProfile = view.findViewById(R.id.item_profile);
            likeCounter = view.findViewById(R.id.item_goodCount);
            likeImg = view.findViewById(R.id.item_goodMark);
            btn_edit = view.findViewById(R.id.edtiBtn);

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

            //주소값 셋팅
            try {
                List<Address> addressList = geocoder.getFromLocation(post.getmLatitude(), post.getmLongitude(), 10);
                tv_location.setText(addressList.get(0).getAddressLine(0)+"에서");
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 좋아요 카운터 표시
            likeCounter.setText(String.valueOf(post.getStarCount()));
            
            //좋아요 표시와 좋아요 이벤트 처리
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

                //좋아요 클릭시
                likeImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stars = post.getStars();
                        if(stars.get(user.getUid()) != null){
                            isLikePost = stars.get(user.getUid());
                        }else {
                            isLikePost = false;
                        }

                        //좋아요 처리...
                        //좋아요 된 상태에서 클릭하면 좋아요 취소
                        if(isLikePost){
                            isLikePost = false;
                            likeImg.setImageResource(R.drawable.icon_empty_good);
                            stars.put(user.getUid(), isLikePost);
                            post.setStarCount(post.getStarCount()-1);

                            Map<String, Object> taskMap = new HashMap<String, Object>();
                            taskMap.put("/posts/" + post.getPostId() + "/stars", stars);
                            taskMap.put("/posts/" + post.getPostId() + "/starCount", post.getStarCount());

                            taskMap.put("/user-posts/" + post.getUid() + "/" + post.getPostId() + "/stars", stars);
                            taskMap.put("/user-posts/" + post.getUid() + "/" + post.getPostId() + "/starCount", post.getStarCount());
                            pDatabase.updateChildren(taskMap);

                        }else {
                            //좋아요
                            isLikePost = true;
                            likeImg.setImageResource(R.drawable.icon_good);
                            post.setStarCount(post.getStarCount()+1);
                            likeCounter.setText(String.valueOf(post.getStarCount()));
                            stars.put(user.getUid(), isLikePost);


                            Map<String, Object> taskMap = new HashMap<String, Object>();
                            taskMap.put("/posts/" + post.getPostId() + "/stars", stars);
                            taskMap.put("/posts/" + post.getPostId() + "/starCount", post.getStarCount());

                            taskMap.put("/user-posts/" + post.getUid() + "/" + post.getPostId() + "/stars", stars);
                            taskMap.put("/user-posts/" + post.getUid() + "/" + post.getPostId() + "/starCount", post.getStarCount());
                            pDatabase.updateChildren(taskMap);

                        }

                    }
                });
            }

            //자기 자신만 수정 삭제 처리 가능
            if(user.getUid().equals(post.getUid())){
                btn_edit.setEnabled(true);
                btn_edit.setVisibility(View.VISIBLE);
                btn_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //수정 삭제 메뉴 모여주는 팝업 메뉴
                        PopupMenu popup = new PopupMenu(mContext, v);
                        popup.getMenuInflater().inflate(R.menu.edit_menu, popup.getMenu());

                        //수정 삭제 클릭 리슨너
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.action_remove:
                                        //삭제
                                        onDeleteContent(position);
                                        break;
                                    case R.id.action_modify:
                                        Bundle bundle = new Bundle();
                                        bundle.putParcelable("UpdatePostData", Parcels.wrap(post));

                                        Intent intent = new Intent(mContext, WriteActivity.class);
                                        intent.putExtra("UpdatePostData", bundle);
                                        mContext.startActivity(intent);
                                        break;
                                    default:
                                        break;
                                }
                                return false;
                            }
                        });
                        popup.show();
                    }
                });

            }else {
                btn_edit.setEnabled(false);
                btn_edit.setVisibility(View.INVISIBLE);
            }

        }


    }

    //삭제
    private void onDeleteContent(int position)
    {

        Map<String, Object> childUpdates = new HashMap<>();
        String postId = postItems.get(position).getPostId();
        String userID = postItems.get(position).getUid();

        try{
            childUpdates.put("/posts/" + postId, null);
            childUpdates .put("/user-posts/" + userID + "/" + postId, null);
            pDatabase.updateChildren(childUpdates);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            storage.getReference().child("photos/"+ userID +"/" + postId+".png").delete().addOnSuccessListener(new OnSuccessListener<Object>() {
                @Override
                public void onSuccess(Object o) {
                    Toast.makeText(mContext, "삭제 성공", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(mContext, "삭제 실패", Toast.LENGTH_SHORT).show();
                }
            });

        }catch (Exception e){
            Toast.makeText(mContext, "삭제 실패", Toast.LENGTH_SHORT).show();
        }

    }


    // 루틴 목록들 보여줌
    public void setItem(ArrayList<Post> data) {
        postItems = data;
        notifyDataSetChanged();
    }



}


