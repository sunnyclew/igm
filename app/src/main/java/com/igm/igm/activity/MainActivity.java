package com.igm.igm.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.igm.igm.R;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private Context mContext;
    @SuppressWarnings("unused")
    private final String TAG = MainActivity.class.getSimpleName();
    private static final String IGM_URL = "http://www.igm7.com";

    Button btnSetting;
    Button btnInfo;
    Button btnPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();
        //button 연결
        btnSetting = findViewById(R.id.btn_setting);
        btnInfo = findViewById(R.id.btn_info);
        btnPlay = findViewById(R.id.btn_play);

        btnSetting.setOnClickListener(this);
        btnInfo.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        //플레이 버튼의 깜박임 애니메이션 지정
        //이미지를 gif 처럼 6개의 프레임으로 받아 배열로 지정했고
        //thread 를 이용해 이미지를 변경하는 애니메이션 시작
        final AnimationDrawable animationDrawable = (AnimationDrawable) btnPlay.getBackground();
        btnPlay.post(animationDrawable::start);
        //toolbar 연결
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        View actionBar = getSupportActionBar().getCustomView();
        ImageView logo = actionBar.findViewById(R.id.logo);
        logo.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_setting:
                startActivity(new Intent(mContext, DeviceListActivity.class));
                break;
            case R.id.btn_info:
                startActivity(new Intent(mContext, InfoActivity.class));
                break;
            case R.id.btn_play:
                // sharedPreferences 관련한 내용은 BaseActivity 에서 초기화 하여 공통으로 상속받아 사용한다.
                // 기존에 저장된 값이 없다면 기기설정 화면으로 보낸다
//                if (sharedPreferences.contains(KEY_1)) {

                //sharedPreference 사용에서 SQLite 사용으로 변경
                if (sqLiteHelper.selectAll().size() > 0) {
                    startActivity(new Intent(mContext, PlayingActivity.class));
                } else {
                    showDialog();
                }
                break;
            case R.id.logo:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(IGM_URL));
                startActivity(intent);
//                finish();
        }
    }

    private void showDialog() {
        //기기설정 화면으로 이동한다는 알림 다이얼로그
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title).setMessage(R.string.dialog_msg)
            .setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
//                    btnSetting.callOnClick();
                startActivity(new Intent(mContext, DeviceListActivity.class));
            }).show();
    }
}
