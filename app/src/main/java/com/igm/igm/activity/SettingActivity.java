package com.igm.igm.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.igm.igm.R;
import com.igm.igm.adapter.RecyclerAdapter;
import com.igm.igm.model.Item;
import com.igm.igm.model.ItemSelectionStatus;

import java.util.ArrayList;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private Context mContext;
    @SuppressWarnings("unused")
    private final String TAG = SettingActivity.class.getSimpleName();

    private EditText edtName;
    private EditText edtPlace;
    private EditText edtUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mContext = getApplicationContext();

        edtName = findViewById(R.id.s_name);
        edtName.setOnClickListener(v -> showDeviceDialog());
        edtPlace = findViewById(R.id.s_place);
        edtUser = findViewById(R.id.s_user);

        Button btnSave = findViewById(R.id.btn_save);
        Button btnBack = findViewById(R.id.btn_back);
        ImageButton btnBt = findViewById(R.id.btn_bt);

        btnSave.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnBt.setOnClickListener(this);

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
        showDeviceDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_bar_home:
                onBackPressed();
                break;
            case R.id.btn_save:
                save();
                break;
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.btn_bt:
                showDeviceDialog();
                break;
        }
    }

    private void save() {
        //data 를 db 에 저장
        String name = cleanseText(edtName.getText());
        String place = cleanseText(edtPlace.getText());
        String user = cleanseText(edtUser.getText());
        if (validation(name, place)) {
            if (sqLiteHelper.isDuplicate(name, place)) {
                showMsg(R.string.dialog_save_err);
            } else {
                if (sqLiteHelper.insertData(name, place, user)) {
                    showMsg(R.string.dialog_save);
                    onBackPressed();
                }
            }
        }
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

    private void showDeviceDialog() {
        ArrayList<Item> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(new Item("RAON-000" + (i + 1)));
        }

        RecyclerView recyclerView;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        @SuppressLint("InflateParams") View content = inflater.inflate(R.layout.dialog_bluetooth_device, null);
        recyclerView = content.findViewById(R.id.bluetooth_list);

        RecyclerAdapter adapter = new RecyclerAdapter(list, this);
        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

//        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//        layoutParams.height = 1200;
        new AlertDialog.Builder(this)
                .setTitle(R.string.bt_title)
                .setView(content)
                .setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
                    edtName.setText(ItemSelectionStatus.currentItem.getName());
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.dialog_cancel, (dialog, which) -> {
                    edtName.setText("");
                    dialog.dismiss();
                })
                .show();
//                .getWindow().setAttributes(layoutParams);
    }

}
