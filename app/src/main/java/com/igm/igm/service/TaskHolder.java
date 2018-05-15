package com.igm.igm.service;

import android.annotation.SuppressLint;
import android.util.Log;

import com.igm.igm.listener.ServiceCallback;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 다수의 Timer 를 관리하기 위해
 * TimerTask 와 Task scheduling 을 위한 Timer 객체를 생성하는 클래스
 * Activity 에서 직접적인 생성을 하지 않고 서비스를 통해서 생성하여 맵에 담아서 관리한다.
 * 매번 생성되는 n개 Timer 의 Callback 은 1개의 Activity 를 바라보고 있으므로
 * Activity 에서 모니터링 하는 device 를 변경할 때마다 callback 을 연결하고 해제해 주어야 한다
 * 그래야 현재 모니터링 하는 timer 의 callback 을 받을 수 있다.
 * */
public class TaskHolder {
    //timer 진행 시간 60 * 3 초
    private static final int TOTAL_TIME = 60 * 3;
    //loading bar 변경 횟수 10회
    private static final int INTERVAL = TOTAL_TIME / 10;
    // timer 시작 전 delay
    private static final long DELAY = 3000;
    // timer 시간 간격
    private static final long PERIOD = 1000;
    private static final String TAG = TaskHolder.class.getSimpleName();
    //Task 의 상태값... 구조 변경을 하다보니 상태값 enum 이 서비스에 남아버렸다...
    private CounterService.Status status = CounterService.Status.STAND_BY;
    private String strTime;
    private int time;
    private int count;
    //PlayingActivity 에 값을 전달해 줄 콜백 인터페이스
    private ServiceCallback callback;
    private Timer mTimer;

    @SuppressLint("DefaultLocale")
    private void doRun() {
        // 타이머를 실행 상태로 기록한다
        if (this.getStatus() != CounterService.Status.DOING) this.status = CounterService.Status.DOING;
        //TimerTask 가 수행하게 될 일
        int min = time / 60;
        int sec = time % 60;
        strTime = String.format("%02d : %02d", min, sec);
        if (time % INTERVAL == 0 && time != TOTAL_TIME) {
            count++;
        }
        // callback 이 연결되어 있으면 PlayingActivity 로 callback 을 보낸다
        if (callback != null) { callback.changeCountCallback(count, strTime, status); }
        // 지정된 시간이 지나면 timer 를 멈춘다
        if (time == 0) { stopTimer(); }
        time--;
    }

    public void startTimer() {
        // 현재 timer 가 실행중인 상태 ( DOING ) 이면 리턴
        if (this.getStatus() == CounterService.Status.DOING) return;
        Log.w(TAG, "timer started");
        // 시간, 카운트 초기화
        resetTimer();
        // timer 와 task 를 생성한다.
        // timer.cancel() 호출 후 재사용이 안되므로 매번 신규로 생성한다.
        mTimer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                doRun();
            }
        };
        // 타이머 시작, 0초 딜레이 후 시작, 1000mills = 1초 간격
        mTimer.schedule(task, DELAY, PERIOD);

    }

    private void stopTimer() {
        // null pointer exception 주의
        // 타이머 스탑
        if (mTimer != null) mTimer.cancel();
        // 타이머를 종료 상태로 기록한다
        // STAND_BY 는 대기상태 이므로 count = 0, END 는 종료상태 이므로 count 가 리셋되기 전임
        status = CounterService.Status.END;
        Log.w(TAG, "timer stopped");
    }

    public void resetTimer() {
        // 타이머를 대기 상태로 기록한다
        status = CounterService.Status.STAND_BY;
        // 시간과 카운트를 리셋
        time = TOTAL_TIME;
        count = 0;
    }
    @SuppressWarnings("unused")
    public int getCount() {
        return count;
    }
    @SuppressWarnings("unused")
    public String getStrTime() {
        return strTime;
    }

    public CounterService.Status getStatus() {
        return status;
    }

    public void registerCallback(ServiceCallback callback) {
        //activity 에서 callback 연결
        this.callback = callback;
        if (status != CounterService.Status.STAND_BY) callback.changeCountCallback(count, strTime, status);
        Log.w(TAG, "callback registered");
    }

    public void unregisterCallback() {
        //activity 에서 callback 연결 해제
        this.callback = null;
        Log.w(TAG, "callback unregistered");
    }

}
