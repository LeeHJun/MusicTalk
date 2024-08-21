package com.example.myapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.WritelistActivity;

public class NoListFragment extends Fragment {

    private Button goToWritelistButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // nolist.xml 레이아웃을 인플레이트
        View view = inflater.inflate(R.layout.nolist, container, false);

        // XML 레이아웃 파일에서 뷰를 찾기
        goToWritelistButton = view.findViewById(R.id.gofrag3);

        // 버튼 클릭 리스너 설정
        goToWritelistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // WritelistActivity로 이동
                Intent intent = new Intent(getActivity(), WritelistActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
