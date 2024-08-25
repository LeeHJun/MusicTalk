package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.fragments.Frag1;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class list extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private String boardName = "";
    private List<SingerItem2> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        boardName = getIntent().getStringExtra("BOARD_NAME");

        if (boardName == null || boardName.isEmpty()) {
            Log.e("list", "BOARD_NAME is not valid");
            finish();
            return;
        } else {
            Log.d("list", "BOARD_NAME: " + boardName);
        }

        initFirebase();
        initView();
    }

    private void initFirebase() {
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("notice board").child(boardName);
    }

    private void initView() {
        Button write = findViewById(R.id.reg_button);
        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(list.this, register.class);
                intent.putExtra("BOARD_NAME", boardName);
                startActivity(intent);
            }
        });

        if (databaseReference != null) {
            setBoardList();
        }
    }

    private void setBoardList() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                items.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SingerItem2 item = snapshot.getValue(SingerItem2.class);
                    if (item != null) {
                        items.add(item);
                    }
                }
                updateListView(items);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("list", "DatabaseError: " + databaseError.getMessage());
            }
        });
    }

    private void updateListView(List<SingerItem2> items) {
        ListView listView = findViewById(R.id.listView);
        Frag1.SingerAdapter adapter = new Frag1.SingerAdapter();
        for (SingerItem2 item : items) {
            adapter.addItem(item);
        }
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SingerItem2 selectedItem = (SingerItem2) adapter.getItem(position);
                Intent intent = new Intent(list.this, PostDetailActivity.class);
                intent.putExtra("POST_ID", selectedItem.getPostId()); // 수정된 부분
                intent.putExtra("BOARD_NAME", boardName);
                startActivity(intent);
            }
        });
    }
}
