package com.example.myapplication.database;

import android.content.Context;
import android.content.SharedPreferences;

//데이터를 Preference에 저장하기 위한 클래스
public class DataStoreManager {

    private final String PREFERENCES_NAME = "DocumentManageApplication";
    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;

    public DataStoreManager(Context context) {
        preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    //key값으로 정보를 Preference으로 부터 가져온다
    public String getInformation(String key) {
        return preferences.getString(key, "");
    }

    //key값을 이용해 value를 Preference에 저장한다.
    public void saveInformation(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }
}

