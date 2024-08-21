package com.example.myapplication;

import android.app.Application;
import java.io.File;

public class BaseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        File dexOutputDir = getCodeCacheDir();  // codeCacheDir의 메서드 호출
        dexOutputDir.setReadOnly();              // 읽기 전용으로 설정
    }
}
