package com.igm.igm.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * PlayingActivity 의 로딩바 변경을 위한 SERVICE
 * Activity 의 상태와 무관하게 백그라운드에서 카운트 진행을 위하여 안드로이드 서비스로 등록
 */
public class CounterService extends Service {

    @SuppressWarnings("unused")
    private static final String TAG = CounterService.class.getSimpleName();
    //service binder 객체
    private final IBinder mBinder = new CountBinder();
    //1개 이상의 timer 를 관리하기 위한 맵
    private Map<String, TaskHolder> taskMap = new HashMap<>();
    // timer 의 상태
    public enum Status {
        STAND_BY, DOING, END
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //service binding 시 binder 객체 반환
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class CountBinder extends Binder {
        public CounterService getService() {
            return CounterService.this;
        }
    }
    // timer 를 생성해서 맵에 넣는다
    public void addTask(String tag) {
        taskMap.put(tag, new TaskHolder());
    }

    @SuppressWarnings("unused")
    public void removeTask(String tag) throws NullPointerException { taskMap.remove(tag); }

    @SuppressWarnings("unused")
    public void removeAllTasks() {
        taskMap.clear();
    }

    public TaskHolder getTask(String tag) {
        if (taskMap.get(tag) == null) addTask(tag);
        return taskMap.get(tag);
    }
}
