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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.inhatc.mobile_project.R;
import com.inhatc.mobile_project.adapter.PostAdapter;
import com.inhatc.mobile_project.db.MemberInfo;
import com.inhatc.mobile_project.db.Post;

import org.parceler.Parcels;

public class FragmentHome extends Fragment implements View.OnClickListener {
    private Button btnGoWrite;
    private MemberInfo userInfo = new MemberInfo();
    private DatabaseReference pDatabase;
    private PostAdapter pAdapter;

    private RecyclerView mRv_posts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Bundle bundle = getArguments();
        if(bundle != null){
            Object value = Parcels.unwrap(bundle.getParcelable("userInfoData"));
            userInfo = (MemberInfo) value;
           // Log.e("생일",userInfo.getPhonNum());
        }
        btnGoWrite = (Button) view.findViewById(R.id.btnGoWrite);
        btnGoWrite.setOnClickListener(this);
        mRv_posts = (RecyclerView) view.findViewById(R.id.homeRecyclerView);

        pDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mRv_posts.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRv_posts.setLayoutManager(layoutManager);
        // = new PostAdapter(pDatabase);




        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnGoWrite:
                Intent intentWrite = new Intent(getActivity(), WriteActivity.class);
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
