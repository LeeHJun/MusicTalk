package com.example.myapplication.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;

import com.example.myapplication.MainActivity;
import com.example.myapplication.SessionManager;

public class LogoutHelper {

    public static void showLogoutDialog(final Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle("로그아웃")
                .setMessage("로그아웃을 하시겠습니까?")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 로그아웃 수행
                        SessionManager sessionManager = new SessionManager(activity);
                        sessionManager.logoutUser();
                        Intent intent = new Intent(activity, MainActivity.class);
                        activity.startActivity(intent);
                        activity.finish(); // 현재 액티비티 종료
                    }
                })
                .setNegativeButton("아니요", null)
                .show();
    }
}
