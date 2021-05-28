package com.inhatc.mobile_project.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
    private CircleImageView imgView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        userName = view.findViewById(R.id.tv_userName);
        phoneNum = view.findViewById(R.id.tv_userPhone);
        birth = view.findViewById(R.id.tv_userBirth);
        imgView = view.findViewById(R.id.circleImageView);

//        imgView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getContext(), "누름!", Toast.LENGTH_LONG).show();
//            }
//        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

//        CollectionReference userInfo = db.collection("users").document(user.getUid()).getParent();
        DocumentReference docRef =  db.collection("users").document(user.getUid());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                MemberInfo minfo;
                DocumentSnapshot doc = task.getResult();
                if(doc.exists()){
                    userName.setText(doc.getString("name"));
                    phoneNum.setText(doc.getString("phonNum"));
                    birth.setText(doc.getString("birth").toString());
                    //userName.setText();
                }
            }
        });

        return view;
    }
}
