package com.inhatc.mobile_project.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.inhatc.mobile_project.R;

public class FragmentHome extends Fragment implements View.OnClickListener {
    private Button btnGoWrite;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        btnGoWrite = (Button) view.findViewById(R.id.btnGoWrite);
        btnGoWrite.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnGoWrite:
                Intent intentWrite = new Intent(getActivity(), WriteActivity.class);
                startActivity(intentWrite);
                break;
        }
    }
}
