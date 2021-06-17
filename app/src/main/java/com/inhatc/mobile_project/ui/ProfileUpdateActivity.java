package com.inhatc.mobile_project.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.UploadTask;
import com.inhatc.mobile_project.DownloadFilesTask;
import com.inhatc.mobile_project.R;
import com.inhatc.mobile_project.db.MemberInfo;
import com.inhatc.mobile_project.db.Post;


import org.parceler.Parcels;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileUpdateActivity extends AppCompatActivity {

    private EditText userName, phoneNum, birth;
    private CircleImageView profileImageVIew;

    public static final int GALLEY_CODE = 10;
    private Uri filePath;
    private String strUrl;
    private MemberInfo userInfo = new MemberInfo();
    private FirebaseUser user;


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

        user = FirebaseAuth.getInstance().getCurrentUser();//현재 user값 받아서
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("userInfo");
        if(bundle != null){
            Object value = Parcels.unwrap(bundle.getParcelable("userInfoData"));
            userInfo = (MemberInfo) value;
        }

        userName = (EditText)findViewById(R.id.editTextName);
        phoneNum = (EditText)findViewById(R.id.editTextPhone);
        birth = (EditText)findViewById(R.id.editTextBirth);
        //프로필 사진
        profileImageVIew = findViewById(R.id.profileImgView);



        userName.setText(userInfo.getName());
        phoneNum.setText(userInfo.getPhonNum());
        birth.setText(userInfo.getBirthDay());

        if(userInfo.getProfimageURL() != null){
            new DownloadFilesTask(profileImageVIew).execute(userInfo.getProfimageURL());
        }



        findViewById(R.id.complteBtn2).setOnClickListener(onClickListener);
        findViewById(R.id.profileImgView).setOnClickListener(onClickListener);
    }



    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.complteBtn2:
                    pthotURL();
                    break;
                case R.id.profileImgView:
                    //로컬 사진첩으로 넘김
                    lodadAlbum();
                    break;
            }
        }
    };

    private void lodadAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, GALLEY_CODE);
    }

    //갤러리로 이동
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLEY_CODE)
        {
            try{
                //파일 경로 받음
                filePath = data.getData();
                //파일 이미지 뷰에 띄우기
                InputStream in = getContentResolver().openInputStream(data.getData());
                Bitmap img = BitmapFactory.decodeStream(in);
                in.close();
                profileImageVIew.setImageBitmap(img);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e){
                e.printStackTrace();
            }

        }
    }

    private void pthotURL(){

        FirebaseStorage storage = FirebaseStorage.getInstance(); //스토리지 인스턴스 만들고
        StorageReference storageRef = storage.getReference();
        StorageReference riversRef = storageRef.child("profile/" + user.getUid() + ".png");//프로필 사진 경로 지정

        if(filePath != null){
            try {
                //사진 업로드
                UploadTask uploadTask = riversRef.putFile(filePath);
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        // Continue with the task to get the download URL
                        return riversRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ProfileUpdateActivity.this, "업로드 성공", Toast.LENGTH_SHORT).show();

                            @SuppressWarnings("VisibleForTests")
                            Uri downloadUrl = task.getResult();                                     //업로드한 사진 다운로드 url
                            strUrl = downloadUrl.toString();
                            profileUpdate();

                        }else{
                            Toast.makeText(ProfileUpdateActivity.this, "업로드 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }catch (NullPointerException e)
            {
                Toast.makeText(ProfileUpdateActivity.this, "이미지 선택 안함", Toast.LENGTH_SHORT).show();
            }

        }else {
            //프로필 사진을 선택하지않고 기본 값으로 하면
            if(userInfo.getProfimageURL() != null){
                strUrl = userInfo.getProfimageURL();
            }else {
                strUrl = "https://firebasestorage.googleapis.com/v0/b/mobile-project-31597.appspot.com/o/profile%2Ficon_user.png?alt=media&token=1f46c19a-0173-4e29-ae8c-a05d2d84769c";
            }

            profileUpdate();

        }
    }

    /*회원정보 업로드*/
    private void profileUpdate(){


        FirebaseStorage storage = FirebaseStorage.getInstance(); //스토리지 인스턴스 만들고
        StorageReference storageRef = storage.getReference();
        StorageReference riversRef = storageRef.child("profile/" + user.getUid() + ".png");//프로필 사진 경로 지정

        //입력 정보 가져오기
        String strname = ((EditText)findViewById(R.id.editTextName)).getText().toString();
        String strphoneNum = ((EditText)findViewById(R.id.editTextPhone)).getText().toString();
        String strbirthDay = ((EditText)findViewById(R.id.editTextBirth)).getText().toString();

        //필수 입력 사항이 다 입력 되면
        if(strname.length() > 0 && strphoneNum.length() > 0 && strbirthDay.length() > 0){
            if(strphoneNum.charAt(3) == '-' && strphoneNum.charAt(8) == '-'){
                if(strbirthDay.charAt(4) == '.' && strbirthDay.charAt(7) == '.'){
                    // Access a Cloud Firestore instance from your Activity
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    //데이터 베이스에 회원정보 넣어주기
                    MemberInfo memberInfo = new MemberInfo(strname, strphoneNum, strbirthDay, strUrl);

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
                                        riversRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.e("imageErro:", "프로필 파일 삭제 완료");
                                                // File deleted successfully
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                // Uh-oh, an error occurred!
                                            }
                                        });
                                    }
                                });
                    }

                }else {
                    Toast.makeText(ProfileUpdateActivity.this, "생년월일을 형식에 맞게 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(ProfileUpdateActivity.this, "전화번호를 형식에 맞게 입력해 주세요.", Toast.LENGTH_SHORT).show();
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
