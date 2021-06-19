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
import com.google.firebase.auth.FirebaseAuth;
import com.inhatc.mobile_project.R;

public class PassordRestActivity extends AppCompatActivity {

    private static final String TAG = "SingUpActivity";
    private FirebaseAuth mAuth; //firevaseAuth 인스턴스 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passwad_rest);

        mAuth = FirebaseAuth.getInstance();//onCreate() 메서드에서 FirebaseAuth 인스턴스 초기화

        findViewById(R.id.sendBtn).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.sendBtn:
                    passwordModify(); //버튼 클릭시 로그인 로직 실행
                    break;
            }
        }
    };

    //비밀번호 재서정
    private void passwordModify(){
        String email = ((EditText)findViewById(R.id.passwordResetEmial)).getText().toString();

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if(email.length() > 0){
            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //성공시 main 로그인 액티비티로 이동
                                Toast.makeText(PassordRestActivity.this, "이메일을 보냈습니다.", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Email sent.");
                                Intent intent = new Intent(PassordRestActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                    });
        }else{
            Toast.makeText(PassordRestActivity.this, "이메일을 입력해주 세요", Toast.LENGTH_SHORT).show();
        }
    }


}
