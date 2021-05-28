package com.inhatc.mobile_project.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.inhatc.mobile_project.R;
import com.inhatc.mobile_project.db.MemberInfo;


import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileUpdateActivity extends AppCompatActivity {

    private EditText userName, phoneNum, birth;
    private CircleImageView profileImageVIew;


    @Override
    public void onBackPressed() {
        //메인 화면에서 뒤로가기 버튼 클릭 시 바로 종료
        super.onBackPressed();
        //finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member_info);

        userName = (EditText)findViewById(R.id.editTextName);
        phoneNum = (EditText)findViewById(R.id.editTextPhone);
        birth = (EditText)findViewById(R.id.editTextBirth);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef =  db.collection("users").document(user.getUid());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                MemberInfo minfo;
                DocumentSnapshot doc = task.getResult();
                if(doc.exists()){
                    userName.setText(doc.getString("name"));
                    phoneNum.setText(doc.getString("phonNum"));
                    birth.setText(doc.getString("birth"));
                    //userName.setText();
                }
            }
        });



        //프로필 사진
        profileImageVIew = findViewById(R.id.profileImgView);

        findViewById(R.id.complteBtn2).setOnClickListener(onClickListener);
        findViewById(R.id.profileImgView).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.complteBtn2:
                    profileUpdate();
                    break;
                case R.id.profileImgView:
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, 0);
                    //goTomyActivity(CameraActivity.class, false);
                    break;
            }
        }
    };

    //갤러리로 이동
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0){
            if(resultCode == RESULT_OK){
                try{
                    //stream : 데이터가 전송되는 통로 라고 보면 됨,
                    InputStream in = getContentResolver().openInputStream(data.getData());

                    //inputStream을 bitmap으로 decode, stream이 null이면 decodeStream null반환
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();

                    profileImageVIew.setImageBitmap(img);
                }catch (Exception e){
                    Log.e("inputStream오류(갤러리에서 사진)", e.toString());
                }
            }else if(resultCode == RESULT_CANCELED){
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void profileUpdate(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseStorage storage = FirebaseStorage.getInstance(); //스토리지 인스턴스 만들고
        StorageReference storageRef = storage.getReference(); //

        //파일 이름 사진
//        String filename = "users/"+user.getUid()+"/profileImage.jpg";
//
//        Uri file = uri;
//        Log.d("유알..", String.valueOf(file));
//
//        StorageReference riversRef = storageRef.child("users/"+user.getUid()+"/profileImage.jpg");
//        UploadTask uploadTask = riversRef.putFile(file);


        String name = ((EditText)findViewById(R.id.editTextName)).getText().toString();
        String phonNum = ((EditText)findViewById(R.id.editTextPhone)).getText().toString();
        String birthDay = ((EditText)findViewById(R.id.editTextBirth)).getText().toString();


        if(name.length() > 0){
            // Access a Cloud Firestore instance from your Activity
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            //데이터 베이스에 회원정보 넣어주기
            MemberInfo memberInfo = new MemberInfo(name, phonNum, birthDay);
            if(user != null){
                db.collection("users").document(user.getUid()).set(memberInfo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ProfileUpdateActivity.this, "회원정보를 수정하였습니다.", Toast.LENGTH_SHORT).show();
                                goTomyActivity(MainActivity.class, true);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfileUpdateActivity.this, "회원정보 등록에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }else{
            Toast.makeText(ProfileUpdateActivity.this, "회원정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void goTomyActivity(Class ac, boolean isbacktohome){
        Intent intent = new Intent(ProfileUpdateActivity.this, ac);
        // 뒤로가기 버튼 누르면 로그인 화면이나 회원가입 화면으로 이동
        //--> activity 기록 지워주어야 함--> flag 사용??
        if(isbacktohome){
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        startActivityForResult(intent, 0);
    }

}
