package com.inhatc.mobile_project.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.inhatc.mobile_project.R;
import com.inhatc.mobile_project.db.MemberInfo;
import com.inhatc.mobile_project.db.Post;

import org.parceler.Parcels;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WriteActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtPlace, txtcontent;
    private Button btnPlaceDialog, btnPlaceSearch;
    private Dialog placeDialog;
    private TextView tvCheckPlace;
    private Button btnAddPost;
    private ImageView postimage;


    public static final int GALLEY_CODE = 10;

    private Geocoder geocoder;
    private List<Address> addressList;

    private static final String TAG = "NewPostFragment";
    private static final String REQUIRED = "Required";

    private int GALLERY_CODE = 10;

    private DatabaseReference mDatabase;
    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private Bundle userBundle = new Bundle();
    private Bundle postBundle = new Bundle();

//    private String userName, userProfile;


    private MemberInfo userInfo = new MemberInfo();
    private FirebaseUser user;

    private String imageUrl="";
    private String path;
    private Uri filePath;

    private ImageView selectedImageVIew;
    private EditText selectedEditText;
    private ArrayList<String> pathList = new ArrayList<>();

    private Post postItems = new Post();
    private boolean isUpdate = false;

    private String key;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        txtcontent = findViewById(R.id.insertContent);
        btnAddPost = findViewById(R.id.insertBtn);
        postimage = findViewById(R.id.insertImg);

        btnAddPost.setOnClickListener(this);
        postimage.setOnClickListener(this);

        //현재 사용자 uid 가져와 userInfo 가져오기
        user = FirebaseAuth.getInstance().getCurrentUser();
        //userInfo.bringMemberInfo(user.getUid());

        mDatabase = FirebaseDatabase.getInstance().getReference();

        btnPlaceDialog = (Button) findViewById(R.id.btnPlaceDialog);        // 나는 지금 여기에 버튼
        btnPlaceDialog.setOnClickListener(this);
        geocoder = new Geocoder(this);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        Intent intent = getIntent();

        userBundle = intent.getBundleExtra("userInfoData");
        postBundle = intent.getBundleExtra("UpdatePostData");
        //추가 하면
        if(userBundle != null){
            Object value = Parcels.unwrap(userBundle.getParcelable("userInfoData"));
            userInfo = (MemberInfo) value;
            isUpdate = false;


        }
        //수정 하면
        if(postBundle != null){
            Object value = Parcels.unwrap(postBundle.getParcelable("UpdatePostData"));
            postItems = (Post) value;
            isUpdate = true;
            userInfo.setProfimageURL(postItems.getProfileImg());
            userInfo.setName(postItems.getAuthor());
            initView();

        }

    }

    // 수정시 view 셋팅
    private void initView() {
        txtcontent.setText(postItems.getPostcontent());
        Glide.with(this)
                .load(postItems.getDownloadImgUri())
                .into(postimage);

        try {
            addressList = geocoder.getFromLocation(postItems.getmLatitude(), postItems.getmLongitude(), 10);
            btnPlaceDialog.setText(addressList.get(0).getAdminArea()+"에서");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPlaceDialog :          // '나는 지금 여기에' 버튼
                showPlaceDialog();              // 다이얼로그 보여주기
                break;
            case R.id.pDia_btnSearchPlace :     // 다이얼로그의 장소 찾기 버튼
                if(btnPlaceSearch.getText().toString().equals("위치 찾기")) {
                    String address = edtPlace.getText().toString();     // 사용자가 입력한 주소 값
                    replaceLatLng(address);
                } else {
                    // addressList 사용해서 위도, 경도 파이어베이스에 저장
                    btnPlaceDialog.setText(addressList.get(0).getAdminArea()+"에!");
                    placeDialog.dismiss();
                }
                break;
            case R.id.insertImg :
                //로컬 사진첩으로 넘김
                lodadAlbum();
                break;
            case R.id.insertBtn :          // 저장
//                uploadImage(imageUrl);
                if(txtcontent.getText() != null && addressList != null){
                    if(filePath != null || postItems.getDownloadImgUri() != null){
                        pthotURL(user.getUid(), userInfo.getName(),txtcontent.getText().toString(), filePath);
                        finish();
                    }
                }else{
                    Toast.makeText(WriteActivity.this, "내용 또는 사진을 선택해주세요", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void lodadAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, GALLEY_CODE);
    }

    //사진 고른 후 돌아오는 코드
    //로컬 파일에서 업로드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_CODE)
        {
            //파일 경로 받음
            filePath = data.getData();
            //파일 이미지 뷰에 띄우기
            try{
                InputStream in = getContentResolver().openInputStream(data.getData());
                Bitmap img = BitmapFactory.decodeStream(in);
                in.close();
                postimage.setImageBitmap(img);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    // 포스트 저장
    private void writeNewPost(Post post){
        try {
            Map<String, Object> postValues = post.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            //전체 포스트 저장
            //post-postuid-내용

            childUpdates.put("/posts/" + post.getPostId(), postValues);
            //사용자별 포스트 저장
            //user-posts-사용자uid-내용
            childUpdates .put("/user-posts/" + post.getUid() + "/" + post.getPostId(), postValues);
            mDatabase.updateChildren(childUpdates);
            Toast.makeText(WriteActivity.this, "업로드 성공", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(WriteActivity.this, "업로드 오류", Toast.LENGTH_SHORT).show();
        }


    }

    //사진 저장및 사진 URL값 받기
    private void pthotURL(String uId, String name, String content, Uri filePath) {

        //포스트 키값 가져오기
        if(!isUpdate){
            key = mDatabase.child("posts").push().getKey();
        }else {
            key = postItems.getPostId();
        }

        
        //선택해서 업로드한 사진이 있으면
        if(filePath != null){
            try{
                StorageReference storageRef = storage.getReference();
                StorageReference riversRef = storageRef.child("photos/"+uId+"/"+key+".png");
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
                            Log.d("phtoURL 성공", "성공");

                            @SuppressWarnings("VisibleForTests")
                            Uri downloadUrl = task.getResult();
                            Post post = new Post(key, uId, name, addressList.get(0).getLatitude(), addressList.get(0).getLongitude(), content, downloadUrl.toString(), userInfo.getProfimageURL());
                            writeNewPost(post);


                        }else{
                            Log.d("phtoURL 성공", "실패");
                        }
                    }
                });

            }catch (NullPointerException e)
            {
                Log.d("phtoURL 성공", "이미지 선택 안함");
            }
        }else {
            Post post = new Post(key, uId, name, addressList.get(0).getLatitude(), addressList.get(0).getLongitude(), content, postItems.getDownloadImgUri(), userInfo.getProfimageURL());
            writeNewPost(post);
        }

    }

    // 다이얼로그 보여주기
    public void showPlaceDialog() {
        placeDialog = new Dialog(this);
        placeDialog.setContentView(R.layout.dialog_place);
        placeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        placeDialog.show();

        edtPlace = placeDialog.findViewById(R.id.pDia_edtPlace);
        tvCheckPlace = placeDialog.findViewById(R.id.pDia_tvCheck);
        btnPlaceSearch = placeDialog.findViewById(R.id.pDia_btnSearchPlace);
        btnPlaceSearch.setOnClickListener(this);

        edtPlace.addTextChangedListener(new TextWatcher() {         // EditText 값 변경 시
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCheckPlace.setText("");
                btnPlaceSearch.setText("위치 찾기");
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    // 입력받은 주소를 위도, 경도로 변환
    public void replaceLatLng(String address) {
        addressList = null;

        try {
            addressList = geocoder.getFromLocationName(address, 10);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러 발생");
        }

        if (addressList != null) {
            if (addressList.size() == 0) {
                Toast.makeText(this, "해당하는 주소 정보가 없습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
            } else {
                tvCheckPlace.setText(String.format("%s 이(가) 맞나요??", addressList.get(0).getAddressLine(0).toString()));
                btnPlaceSearch.setText("확인");
            }
        }
    }
}