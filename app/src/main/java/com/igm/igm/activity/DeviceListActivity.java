package com.igm.igm.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.igm.igm.R;
import com.igm.igm.model.DeviceInfo;

import java.util.List;

public class DeviceListActivity extends BaseActivity implements View.OnClickListener {

    private Context mContext;
    @SuppressWarnings("unused")
    private final String TAG = DeviceListActivity.class.getSimpleName();
    LinearLayout itemList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        mContext = getApplicationContext();

        Button btnAdd = findViewById(R.id.btn_add);
        Button btnBack = findViewById(R.id.btn_back);
        btnAdd.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        itemList = findViewById(R.id.device_list);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_sub);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        View actionBar = getSupportActionBar().getCustomView();
        ImageButton btnHome = actionBar.findViewById(R.id.action_bar_home);
        btnHome.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        addDeviceList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_bar_home:
                onBackPressed();
                break;
            case R.id.btn_add:
                startActivity(new Intent(mContext, SettingActivity.class));
                break;
            case R.id.btn_back:
                onBackPressed();
                break;
        }
    }

    private void addDeviceList() {
        //list 초기화
        itemList.removeAllViews();
        List<DeviceInfo> list = sqLiteHelper.selectAll();
        if (list.size() <= 0) return;
        for (int i = 0; i < list.size(); i++) {
            //디바이스 리스트를 화면에 그리는 작업
            DeviceInfo deviceInfo = list.get(i);
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            @SuppressLint("InflateParams")
            View view = inflater.inflate(R.layout.content_list_item, null);
            LinearLayout item = view.findViewById(R.id.list_item);

            // 짝수 번째 리스트의 배경색을 회색으로 칠함
            if (i % 2 == 0) item.setBackgroundResource(R.drawable.txt_bg_grey);

            TextView dName = item.findViewById(R.id.d_name);
            TextView dPlace = item.findViewById(R.id.d_place);
            TextView dUser = item.findViewById(R.id.d_user);

            dName.setText(deviceInfo.getDName());
            dPlace.setText(deviceInfo.getDPlace());
            dUser.setText(deviceInfo.getDUser());

            view.setOnLongClickListener(v -> {
                LayoutInflater inflater1 = LayoutInflater.from(mContext);
                @SuppressLint("InflateParams")
                View content = inflater1.inflate(R.layout.dialog_device, null);
                AlertDialog builder = new AlertDialog.Builder(DeviceListActivity.this)
                        .setView(content)
                        .show();
                Button btnDel = content.findViewById(R.id.btn_del);
                Button btnMod = content.findViewById(R.id.btn_mod);
                ImageButton btnCancel = content.findViewById(R.id.btn_cancel);
                btnDel.setOnClickListener(v13 -> {
                    //삭제
                    sqLiteHelper.deleteData(deviceInfo.getDName());
                    builder.dismiss();
                    addDeviceList();
                });
                btnMod.setOnClickListener(v12 -> {
                    Intent intent = new Intent(mContext, ModifyActivity.class);
                    intent.putExtra("name", deviceInfo.getDName());
                    startActivity(intent);
                    builder.dismiss();
                });
                btnCancel.setOnClickListener(v1 -> builder.dismiss());
                return false;
            });
            itemList.addView(view);
        }
    }

}
