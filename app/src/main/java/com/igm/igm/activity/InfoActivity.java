package com.igm.igm.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ImageButton;

import com.igm.igm.R;

public class InfoActivity extends BaseActivity implements View.OnClickListener {

    @SuppressWarnings("unused")
    private final String TAG = InfoActivity.class.getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_bar_home:
                onBackPressed();
                break;
        }
    }
}
