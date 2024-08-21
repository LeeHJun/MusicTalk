package com.example.myapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.SingerItem;
import com.example.myapplication.SingerItem2;
import com.example.myapplication.SingerItemView;
import com.example.myapplication.list;

import java.util.ArrayList;

public class Frag1 extends Fragment {
    private View view;
    private ArrayList<SingerItem2> itemList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag1, container, false);

        // Initialize itemList with default items
        itemList = new ArrayList<>();
        itemList.add(new SingerItem2("자유 게시판", "자유로운 얘기", R.drawable.icon01));
        itemList.add(new SingerItem2("창작 게시판", "음악 창작 공유", R.drawable.icon2));
        itemList.add(new SingerItem2("불만 게시판", "여러가지 불만들", R.drawable.icon3));
        itemList.add(new SingerItem2("발라드 게시판", "발라드 좋아요", R.drawable.icon4));
        itemList.add(new SingerItem2("힙합 게시판", "힙합 좋아요", R.drawable.icon5));
        itemList.add(new SingerItem2("팝송 게시판", "팝송 좋아요", R.drawable.icon6));
        itemList.add(new SingerItem2("R&B 게시판", "R&B 좋아요", R.drawable.icon7));
        itemList.add(new SingerItem2("게임 게시판", "같이 게임해요", R.drawable.icon8));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        ListView listView = view.findViewById(R.id.listView);
        SingerAdapter adapter = new SingerAdapter();
        for (SingerItem2 item : itemList) {
            adapter.addItem(item);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(requireContext(), list.class);
                intent.putExtra("BOARD_NAME", itemList.get(i).getName()); // 게시판 이름 전달
                requireActivity().startActivity(intent);
            }
        });
        listView.setAdapter(adapter);
    }

    public static class SingerAdapter extends BaseAdapter {
        private final ArrayList<SingerItem2> items = new ArrayList<>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(SingerItem2 item) {
            items.add(item);
            notifyDataSetChanged();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SingerItemView singerItemView;
            if (convertView == null) {
                singerItemView = new SingerItemView(parent.getContext());
            } else {
                singerItemView = (SingerItemView) convertView;
            }
            SingerItem2 item = items.get(position);
            singerItemView.setName(item.getName());
            singerItemView.setMobile(item.getMobile());
            singerItemView.setImage(item.getResId());
            return singerItemView;
        }
    }
}
