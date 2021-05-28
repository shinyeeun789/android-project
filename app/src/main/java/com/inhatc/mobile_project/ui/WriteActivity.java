package com.inhatc.mobile_project.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.inhatc.mobile_project.R;

import java.io.IOException;
import java.util.List;

public class WriteActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtPlace;
    private Button btnPlaceDialog, btnPlaceSearch;
    private Dialog placeDialog;
    private TextView tvCheckPlace;

    private Geocoder geocoder;
    private List<Address> addressList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        btnPlaceDialog = (Button) findViewById(R.id.btnPlaceDialog);        // 나는 지금 여기에서 버튼
        btnPlaceDialog.setOnClickListener(this);
        geocoder = new Geocoder(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPlaceDialog :          // '나는 지금 여기에서' 버튼
                showPlaceDialog();              // 다이얼로그 보여주기
                break;
            case R.id.pDia_btnSearchPlace :     // 다이얼로그의 장소 찾기 버튼
                if(btnPlaceSearch.getText().toString().equals("위치 찾기")) {
                    String address = edtPlace.getText().toString();     // 사용자가 입력한 주소 값
                    replaceLatLng(address);
                } else {
                    // addressList 사용해서 위도, 경도 파이어베이스에 저장
                    placeDialog.dismiss();
                }
                break;
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