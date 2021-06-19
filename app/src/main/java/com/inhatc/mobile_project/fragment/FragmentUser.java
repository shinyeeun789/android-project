package com.inhatc.mobile_project.fragment;

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

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.inhatc.mobile_project.R;
import com.inhatc.mobile_project.db.MemberInfo;
import com.inhatc.mobile_project.activity.MainActivity;
import com.inhatc.mobile_project.activity.ProfileUpdateActivity;

import org.parceler.Parcels;

import de.hdodenhof.circleimageview.CircleImageView;


public class FragmentUser extends Fragment {

    private static final String TAG = "FragmentUser";

    private TextView userName, phoneNum, birth;

    private Button btnUpdate, btnLogout;

    private MemberInfo userInfo = new MemberInfo();
    private Bundle bundle = new Bundle();


    private CircleImageView imgView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        //해당 사용장 정보를 bundle로 가져옴
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            Bundle bundle = getArguments();
            if(bundle != null){
                Object value = Parcels.unwrap(bundle.getParcelable("userInfoData"));
                userInfo = (MemberInfo) value;
                bundle.putParcelable("userInfoData", Parcels.wrap(userInfo));

            }
        }




        userName = view.findViewById(R.id.tv_userName);
        phoneNum = view.findViewById(R.id.tv_userPhone);
        birth = view.findViewById(R.id.tv_userBirth);

        btnUpdate = view.findViewById(R.id.btnUpdateUser);
        btnLogout = view.findViewById(R.id.btn_log_out);

        imgView = view.findViewById(R.id.profileImgView);

        userName.setText(userInfo.getName());
        phoneNum.setText(userInfo.getPhonNum());
        birth.setText(userInfo.getBirthDay());
        if(userInfo.getProfimageURL() != null){
            Glide.with(view)
                    .load(userInfo.getProfimageURL())
                    .into(imgView);
        }

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
                    Intent logoutintent = new Intent(getActivity(), MainActivity.class);
                    logoutintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    logoutintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(logoutintent);
                    break;
                case R.id.btnUpdateUser:
                    //사용자 프로필 수정
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("userInfoData", Parcels.wrap(userInfo));
                    Intent intent = new Intent(getActivity(), ProfileUpdateActivity.class);
                    intent.putExtra("userInfo", bundle);
                    startActivity(intent);
                    break;
            }
        }
    };

}
