package com.inhatc.mobile_project.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.inhatc.mobile_project.R;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth; //firevaseAuth 인스턴스 선언


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();//onCreate() 메서드에서 FirebaseAuth 인스턴스 초기화
        findViewById(R.id.complteBtn).setOnClickListener(onClickListener);
    }

    //활동 초기화할 때 사용자가 현재 로그인되어있는지 확인
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.complteBtn:
                    createAccount(); //버튼 클릭시 회원가입 로직 실행
                    break;

            }
        }
    };

    //회원가입
    private void createAccount(){
        // 일반 view는 getText() 사용하지 못해서 (EditText나 TextView에서만 사용가능) EditText로 형변환
        String email = ((EditText)findViewById(R.id.emailEditText)).getText().toString();
        String password = ((EditText)findViewById(R.id.passwordEiditText)).getText().toString();
        String passwordCheck = ((EditText)findViewById(R.id.passwordCheckEditText)).getText().toString();


        //이메일, 비번, 비번 체크 값이 있어야지 실행
        if(email.length() > 0 && password.length() > 0 && passwordCheck.length() > 0){
            if(password.equals(passwordCheck)){
                //paassword와 passwordcheck 같으면
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(SignUpActivity.this, "이메일 사용이 가능합니다.", Toast.LENGTH_SHORT).show();

                                    //회원가입 성공시 정보입력 화면으로
                                    Intent profileIntent = new Intent(SignUpActivity.this, ProfileUpdateActivity.class);
                                    profileIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    profileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    profileIntent.putExtra("userID", user.getUid());
                                    startActivity(profileIntent);

                                    //goTomyActivity(ProfileUpdateActivity.class, true);

                                } else {
                                    // If sign in fails, display a message to the user.
                                    if(task.getException() != null){
                                        Log.e("파이어베이스 오류",  task.getException().toString());
                                        Toast.makeText(SignUpActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }else{
                //비밀번호와 비밀번호 환이 불일치 시
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "이메일 또는 비밀 번호를 입력해 주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void goTomyActivity(Class ac, boolean isbacktohome){
        Intent intent = new Intent(SignUpActivity.this, ac);
        // 뒤로가기 버튼 누르면 로그인 화면이나 회원가입 화면으로 이동
        //--> activity 기록 지워주어야 함--> flag 사용??
        if(isbacktohome){
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        startActivity(intent);
    }
}
