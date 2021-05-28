package com.inhatc.mobile_project.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inhatc.mobile_project.R;
import com.inhatc.mobile_project.db.MemberImple;
import com.inhatc.mobile_project.db.MemberInfo;
import com.inhatc.mobile_project.db.Post;

import java.util.HashMap;
import java.util.Map;

public class WriteActivity extends AppCompatActivity {

    private static final String TAG = "NewPostFragment";
    private static final String REQUIRED = "Required";

    private DatabaseReference mDatabase;
    private DatabaseReference conditionRef;

    private EditText txtTitle;
    private EditText txtcontent;
    private Button btnAddPost;
    private FirebaseUser user;

    private MemberInfo userInfo = new MemberInfo();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        txtTitle = findViewById(R.id.post_title);
        txtcontent = findViewById(R.id.post_content);
        btnAddPost = findViewById(R.id.btnAddPost);
        btnAddPost.setOnClickListener(onClickListener);

        user = FirebaseAuth.getInstance().getCurrentUser();

        userInfo.bringMemberInfo(user.getUid());

        mDatabase = FirebaseDatabase.getInstance().getReference();


    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnAddPost:
                    writeNewPost(user.getUid(), userInfo.getName(), txtTitle.getText().toString(), txtcontent.getText().toString());
                    finish();
                    break;
            }
        }
    };


    private void writeNewPost(String userId, String username, String title, String body) {

        String key = mDatabase.child("posts").push().getKey();
        Post post = new Post(userId, username, title, body);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        //전체 포스트 저장
        //post-postuid-내용
        childUpdates.put("/posts/" + key, postValues);
        //사용자별 포스트 저장
        //user-posts-사용자uid-내용
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);
        mDatabase.updateChildren(childUpdates);
    }
}