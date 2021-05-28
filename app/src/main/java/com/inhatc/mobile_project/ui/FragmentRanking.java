package com.inhatc.mobile_project.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.inhatc.mobile_project.R;

import java.util.Calendar;

public class FragmentRanking extends Fragment {

    private Calendar cToday = Calendar.getInstance(), startDay = Calendar.getInstance(), endDay = Calendar.getInstance();
    private TextView rankingWeek;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);

        rankingWeek = (TextView)view.findViewById(R.id.tv_rankingWeek);


        startDay.set(cToday.getTime().getYear(), cToday.getTime().getMonth(), cToday.getTime().getDate());      startDay.getTime();
        endDay.set(cToday.getTime().getYear(), cToday.getTime().getMonth(), cToday.getTime().getDate());      endDay.getTime();
        startDay.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        endDay.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        rankingWeek.setText(String.format("%02d월 %02d일 ~ %02d월 %02d일", startDay.getTime().getMonth()+1, startDay.getTime().getDate(), endDay.getTime().getMonth()+1, endDay.getTime().getDate()));

        return view;
    }
}
