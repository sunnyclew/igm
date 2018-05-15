package com.igm.igm.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.igm.igm.database.SQLiteHelper;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

//    protected SharedPreferences sharedPreferences;
//
//    protected static final String KEY_1 = "d_name";
//    protected static final String KEY_2 = "d_place";
//    protected static final String KEY_3 = "d_user";

    protected SQLiteHelper sqLiteHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //shared preferences 를 사용하는 것에서 내부 db 를 사용하는 것으로 자체 변경
        //instance 를 baskActivity 에서 생성하여 상속받는 모든 activity 에서 함께 사용
        sqLiteHelper = SQLiteHelper.getInstance(getApplicationContext());
        //shared preferences 접근
//        sharedPreferences = getSharedPreferences("setting", MODE_PRIVATE);
    }

    //shared preferences 저장
//    protected void saveSetting(String name, String place, String user) {
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString(KEY_1, name);
//        editor.putString(KEY_2, place);
//        editor.putString(KEY_3, user);
//        editor.apply();
//    }

}
