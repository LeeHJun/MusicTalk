package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.myapplication.fragments.Frag1;
import com.example.myapplication.fragments.Frag2;
import com.example.myapplication.fragments.Frag3;
import com.example.myapplication.fragments.Frag4;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TabActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        initView();
    }

    private void initView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.setSelectedItemId(R.id.actionwrap);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Frag2()).commit();
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.actionwrap:
                    selectedFragment = new Frag1();
                    break;
                case R.id.actionmypage:
                    selectedFragment = new Frag2();
                    break;
                case R.id.actionmusic:
                    selectedFragment = new Frag3();
                    break;
                case R.id.actionsearch:
                    selectedFragment = new Frag4();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };
}