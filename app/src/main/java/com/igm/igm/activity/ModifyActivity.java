package com.igm.igm.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.igm.igm.R;
import com.igm.igm.model.DeviceInfo;

public class ModifyActivity extends BaseActivity implements View.OnClickListener {

    private Context mContext;
    @SuppressWarnings("unused")
    private final String TAG = ModifyActivity.class.getSimpleName();

    private EditText edtName;
    private EditText edtPlace;
    private EditText edtUser;
    DeviceInfo deviceInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        mContext = getApplicationContext();

        edtName = findViewById(R.id.s_name);
        edtPlace = findViewById(R.id.s_place);
        edtUser = findViewById(R.id.s_user);

        Button btnSave = findViewById(R.id.btn_save);
        Button btnBack = findViewById(R.id.btn_back);

        btnSave.setOnClickListener(this);
        btnBack.setOnClickListener(this);

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
        setViewWithData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_bar_home:
                onBackPressed();
                break;
            case R.id.btn_save:
                modify();
                break;
            case R.id.btn_back:
                onBackPressed();
                break;
        }
    }

    private void setViewWithData() {
        Intent intent = getIntent();
        if (intent != null) {
            String name = intent.getStringExtra("name");
            deviceInfo = sqLiteHelper.selectData(name);
            edtName.setText(deviceInfo.getDName());
            edtPlace.setText(deviceInfo.getDPlace());
            edtUser.setText(deviceInfo.getDUser());
        }
    }

    private void modify() {
        //data 를 db 에 저장
        String name = cleanseText(edtName.getText());
        String place = cleanseText(edtPlace.getText());
        String user = cleanseText(edtUser.getText());
        //공백 체크
        if (validation(name, place)) {
            //수정되었는지?
            if (isChange(name, place)) {
                //내용 수정됨
                //수정된 내용이 중복인지?
                if (sqLiteHelper.isDuplicate(place)) {
                    showMsg(R.string.dialog_save_err);
                    return;
                }
            }
            //1. 수정안되었음 2. 수정되었지만 중복이 아님
            if (sqLiteHelper.updateData(name, place, user)) {
                showMsg(R.string.dialog_modify);
                onBackPressed();
            }
        }
    }

    private boolean isChange(String name, String place) {
        boolean flag = true;
        if (deviceInfo.getDName().equals(name) && deviceInfo.getDPlace().equals(place))
            flag = false;
        return flag;
    }

    private boolean validation(String name, String place) {
        if (name.equals("")) {
            showMsg(R.string.err_s_name);
            return false;
        }
        if (place.equals("")) {
            showMsg(R.string.err_s_place);
            return false;
        }
        return true;
    }

    //editText 에서 가져온 값에서 공백 제거
    private String cleanseText(Editable txt) {
        return txt.toString().trim();
    }

    private void showMsg(int strResId) {
        Toast.makeText(mContext, strResId, Toast.LENGTH_SHORT).show();
    }

}
