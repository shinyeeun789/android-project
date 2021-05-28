package com.inhatc.mobile_project.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.inhatc.mobile_project.R;
import com.inhatc.mobile_project.db.MemberInfo;

import de.hdodenhof.circleimageview.CircleImageView;


public class FragmentUser extends Fragment {

    private TextView userName, phoneNum, birth;

    private Button btnUpdate, btnLogout;

    private MemberInfo userInfo = new MemberInfo();


    private CircleImageView imgView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        userName = view.findViewById(R.id.tv_userName);
        phoneNum = view.findViewById(R.id.tv_userPhone);
        birth = view.findViewById(R.id.tv_userBirth);

        btnUpdate = view.findViewById(R.id.btnUpdateUser);
        btnLogout = view.findViewById(R.id.btn_log_out);

        imgView = view.findViewById(R.id.profileImgView);


//        imgView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getContext(), "누름!", Toast.LENGTH_LONG).show();
//            }
//        });



        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userInfo.bringMemberInfo(user.getUid());
        DocumentReference docRef =  db.collection("users").document(user.getUid());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                if(doc.exists()){
                    userName.setText(doc.getString("name"));
                    phoneNum.setText(doc.getString("phonNum"));
                    birth.setText(doc.getString("birth"));
                }
            }
        });

//        userName.setText(userInfo.getName());
//        phoneNum.setText(userInfo.getPhonNum());
//        birth.setText(userInfo.getBirth());

        btnUpdate.setOnClickListener(onClickListener);
        btnLogout.setOnClickListener(onClickListener);


        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_log_out:
                    //로그아웃
                    FirebaseAuth.getInstance().signOut();
                    goTomyActivity(LoginActivity.class, true);
                    break;
                case R.id.btnUpdateUser:
                    //로그인
                    goTomyActivity(ProfileUpdateActivity.class, false);
                    break;
            }
        }
    };

    private void goTomyActivity(Class ac, boolean isBackToHome){
        Intent intent = new Intent(getActivity(), ac);
        // 뒤로가기 버튼 누르면 로그인 화면이나 회원가입 화면으로 이동
        //--> activity 기록 지워주어야 함
        if(isBackToHome){
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        startActivity(intent);
    }
}
