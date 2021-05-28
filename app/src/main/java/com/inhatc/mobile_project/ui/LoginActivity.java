package com.inhatc.mobile_project.ui;

import android.content.Intent;
import android.os.Bundle;
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

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth; //firevaseAuth 인스턴스 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();//onCreate() 메서드에서 FirebaseAuth 인스턴스 초기화


        findViewById(R.id.loginBtn).setOnClickListener(onClickListener);
        findViewById(R.id.RegisterBtn).setOnClickListener(onClickListener);
        findViewById(R.id.pwRest).setOnClickListener(onClickListener);
    }
    
    //활동 초기화할 때 사용자가 현재 로그인되어 있는지 확인
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            goTomyActivity(MainActivity.class, true);
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.loginBtn:
                    signIn(); //버튼 클릭시 로그인 로직 실행
                    break;
                case R.id.RegisterBtn:
                    //회원가입으로 이동
                    //activity끼리 서로 호출하기 위해 Intent 사용
                    // Intent(액티비티 클래스를 구현하느 컨텍스트로 보통 this, 호출할 액티비티)
                    goTomyActivity(SignUpActivity.class, false);
                    break;
                case R.id.pwRest:
                    goTomyActivity(PassordRestActivity.class, false);
                    break;
            }
        }
    };

    //로그인
    private void signIn(){
        // 일반 view는 getText() 사용하지 못해서 (EditText나 TextView에서만 사용가능) EditText로 형변환
        String email = ((EditText)findViewById(R.id.emailEditText2)).getText().toString();
        String password = ((EditText)findViewById(R.id.passwordEiditText2)).getText().toString();

        //이메일, 비번, 값이 있는지 체크
        if(email.length() > 0 && password.length() > 0){
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();

                                //로그인 성공시 main 화면으로
                                goTomyActivity(MainActivity.class, true);

                            } else {
                                // If sign in fails, display a message to the user.
                                if(task.getException() != null){
                                    Toast.makeText(LoginActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        }else{
            Toast.makeText(this, "이메일, 비밀 번호를 입력해 주해요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void goTomyActivity(Class ac, boolean isbacktohome){
        Intent intent = new Intent(LoginActivity.this, ac);
        // 뒤로가기 버튼 누르면 로그인 화면이나 회원가입 화면으로 이동
        //--> activity 기록 지워주어야 함--> flag 사용??
        if(isbacktohome){
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        startActivity(intent);
    }

}
