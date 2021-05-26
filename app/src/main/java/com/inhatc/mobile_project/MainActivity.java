package com.inhatc.mobile_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private FragmentHome fragmentHome = new FragmentHome();
    private FragmentRanking fragmentRanking = new FragmentRanking();
    private FragmentUser fragmentUser = new FragmentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        // 프래그먼트 트랜잭션(프래그먼트 백 스택 관리, 프래그먼트 전환 애니메이션 설정 등) 시작

        // add(): 추가, remove(): 삭제, replace(): 전환
        // commit()은 activity가 state 저장하기 전에 이루어져야 함. 저장된 후 불리면 exception 발생
        // commitAllowingStateLoss(): 프래그먼트가 state 저장과 관련없게 작동되도록 사용
        replaceFragment(fragmentHome);
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());   // 메뉴 클릭에 대한 이벤트 처리
        bottomNavigationView.setSelectedItemId(R.id.page_home);     // 선택된 아이템 지정
    }

    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch(menuItem.getItemId()) {
                case R.id.page_home:
                    replaceFragment(fragmentHome);
                    break;
                case R.id.page_ranking:
                    replaceFragment(fragmentRanking);
                    break;
                case R.id.page_user:
                    replaceFragment(fragmentUser);
                    break;
            }
            return true;
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment).commitAllowingStateLoss();
    }
}