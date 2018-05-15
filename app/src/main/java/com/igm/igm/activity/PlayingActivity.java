package com.igm.igm.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.igm.igm.R;
import com.igm.igm.listener.ServiceCallback;
import com.igm.igm.model.DeviceInfo;
import com.igm.igm.service.CounterService;
import com.igm.igm.service.TaskHolder;

import java.util.ArrayList;

public class PlayingActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Context mContext;
    @SuppressWarnings("unused")
    private final String TAG = PlayingActivity.class.getSimpleName();
    private static final long DURATION = 500;
    private String tag = null;
    private CounterService countService;
    private TaskHolder taskHolder;
    private ArrayList<String> list;
    private ArrayList<DeviceInfo> deviceInfos;
    Button btnIng;
    Button btnOk;
    Button btnClean;
    TextView txtRemain;
    Spinner spinnerName;

    ImageView[] imageViews;
    TypedArray colorArr;
    Animation mAnimation;

    //CounterService 에서 결과값을 받아 화면을 변경하기 위한 콜백 선언
    private ServiceCallback callback = (count, time, status) -> {
        changeColor(count);
        changeTime(time);
    };

    //service 연결을 위한 connection 객체
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //service 에서 binder 객체를 받아서 연결
            CounterService.CountBinder countBinder = (CounterService.CountBinder) service;
            countService = countBinder.getService();
            Log.w(TAG, "service connected");
            int p = spinnerName.getSelectedItemPosition();
            setViewData(p);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            countService = null;
            Log.w(TAG, "service disconnected");
        }
    };

    //spinner list 설정
    private void setArrayList() {
        list = new ArrayList<>();
        deviceInfos = sqLiteHelper.selectAll();
        for (DeviceInfo info : deviceInfos) {
            list.add(info.getDName());
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);
        //color array 등록
        colorArr = getResources().obtainTypedArray(R.array.loadingColors);
        mContext = getApplicationContext();
        //buttons 연결
        btnIng = findViewById(R.id.btn_ing);
        btnClean = findViewById(R.id.btn_clean);
        btnOk = findViewById(R.id.btn_ok);
        btnClean.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        //spinner 연결
        spinnerName = findViewById(R.id.spinner_name);
        spinnerName.setOnItemSelectedListener(this);
        setArrayList();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, R.layout.custom_spinner, list);
        adapter.setDropDownViewResource(R.layout.custom_dropdown);
        spinnerName.setAdapter(adapter);

        txtRemain = findViewById(R.id.txt_remain);
        //toolbar 설정
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_sub);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //toolbar 에 뒤로 가기 버튼을 만들려면 주석 해제
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //toolbar 우측 홈 버튼 연결
        View actionBar = getSupportActionBar().getCustomView();
        ImageButton btnHome = actionBar.findViewById(R.id.action_bar_home);
        btnHome.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //loading bar 로 사용할 imageView 동적 생성
        initLoadingBar();
        initAnimation(DURATION);
        //activity resume 상태 진입시 service 상태 확인
        //service 가 해제되어 있으면 service 연결
        if (countService == null) {
            bindService(new Intent(this, CounterService.class), conn, BIND_AUTO_CREATE);
        }
        startService(new Intent(getApplicationContext(), CounterService.class));
        spinnerName.setSelection(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //activity pause 상태 진입시 service 상태 확인
        if (countService != null) {
            unbindService(conn);
            countService = null;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_bar_home:
                onBackPressed();
                break;
            case R.id.btn_clean:
//                startPlaying();
                break;
            case R.id.btn_ok:
                // 연결된 timer 가 없거나, timer 의 상태가 종료가 아니라면 클릭 이벤트 막음
                // 08. 05 추가
                // timer 의 상태에 따라 clickListener 를 연결 또는 해제하는 것이 메모리 관리에서 이득일 것 같다
                if(taskHolder == null || taskHolder.getStatus() != CounterService.Status.END) return;
                // 대기 상태로 변경
                setBtnAnimation(CounterService.Status.STAND_BY);
                taskHolder.resetTimer();
                initLoadingBar();
                break;
        }
    }

    private void startPlaying() {
        Log.w(TAG, "start");
        //count 가 진행 중이면 return
        if (taskHolder == null) initTask();
        if (taskHolder.getStatus() != CounterService.Status.STAND_BY) return;
        //count 가 진행 중이 아니면 count 시작
        taskHolder.startTimer();
        setBtnAnimation(taskHolder.getStatus());
    }

    private void initLoadingBar() {
        txtRemain.setText("");
        txtRemain.setBackground(null);
        //imageView 동적 생성 및 초기화, 배열 등록
        LinearLayout layout = findViewById(R.id.loading);
        layout.removeAllViews();
        imageViews = new ImageView[11];
        for (int i = 0; i < 11; i++) {
            ImageView imageView = new ImageView(mContext);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
            if (i < 10) layoutParams.setMarginEnd(5);
            imageView.setLayoutParams(layoutParams);
            imageView.setBackgroundResource(R.color.transparent);
            imageViews[i] = imageView;
            layout.addView(imageView);
        }
    }

    private void changeTime(final String time) {
        new Thread(() -> runOnUiThread(() -> {
            txtRemain.setText(time);
            txtRemain.setBackgroundResource(R.color.black_mask);
        })).start();
    }

    private void changeColor(final int count) {
        //service 의 callback 에 의해 호출됨
        //but, service thread 는 activity 의 화면을 변경할 수 없다 ( 안드로이드 제한사항 )
        //uiThread 를 호출해서 activity 의 화면을 변경한다
        new Thread(() -> runOnUiThread(() -> {
            for (int i = 0; i <= count; i++) {
                imageViews[i].setBackgroundResource(colorArr.getResourceId(i, R.color.transparent));
                setBtnAnimation(CounterService.Status.DOING);
            }
            if (count == 10) {
                setBtnAnimation(CounterService.Status.END);
            }
        })).start();
    }

    //깜박임 animation 정의
    private void initAnimation(long duration) {
        mAnimation = new AlphaAnimation(0, 1);
        mAnimation.setDuration(duration);
        mAnimation.setInterpolator(new LinearInterpolator());
        mAnimation.setRepeatCount(Animation.INFINITE);
        mAnimation.setRepeatMode(Animation.REVERSE);
    }

    private void setBtnAnimation(CounterService.Status status) {
        // 3개의 버튼 모두 상황에 따라 깜박이게 했었지만
        // 워낙 볼품 없어서 대기 버튼은 깜박임 삭제
        initBtns();
        switch (status) {
            case STAND_BY:
                btnIng.setBackgroundResource(R.drawable.btn_ing_v);
                break;
            case DOING:
                btnClean.startAnimation(mAnimation);
                btnClean.setBackgroundResource(R.drawable.btn_clean_v);
                break;
            case END:
                btnOk.startAnimation(mAnimation);
                btnOk.setBackgroundResource(R.drawable.btn_ok_v);
                break;
        }
    }

    private void initBtns() {
        //버튼 이미지, 애니메이션 초기화
        btnIng.clearAnimation();
        btnOk.clearAnimation();
        btnClean.clearAnimation();
        btnIng.setBackgroundResource(R.drawable.btn_ing);
        btnClean.setBackgroundResource(R.drawable.btn_clean);
        btnOk.setBackgroundResource(R.drawable.btn_ok);
    }

    private void initTask() {
        //현재 연결되어 있는 timer, task 의 callback 해제
        if (taskHolder != null) {
            taskHolder.unregisterCallback();
        }
        //선택된 디바이스에 맞는 timer, task 의 callback 연결
        taskHolder = countService.getTask(tag);
        taskHolder.registerCallback(callback);

    }

    private void setViewData(int position) {
        DeviceInfo info = deviceInfos.get(position);
        // 선택된 dropdown item 을 기준으로 디바이스 정보를 가져온다
        tag = String.valueOf(info.getIdx());
        // 로딩바 초기화
        initLoadingBar();
        // 현재 선택된 디바이스에 맞는 timer 를 연결한다
        if (countService != null) {
            initTask();
            startPlaying();
        }
//        // timer, task 정보를 가져와서 화면에 반영
//        if (taskHolder != null && taskHolder.getStatus() != null) {
//            setBtnAnimation(taskHolder.getStatus());
//            changeColor(taskHolder.getCount());
//            changeTime(taskHolder.getStrTime());
//        }
        // timer 가 실행중이 아니라면 '대기'버튼에 불이 들어오게 한다
        else { setBtnAnimation(CounterService.Status.STAND_BY); }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        setViewData(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }
}
