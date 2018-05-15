package com.igm.igm.listener;

import com.igm.igm.service.CounterService;

public interface ServiceCallback {
    void changeCountCallback(int count, String time, CounterService.Status status);
}
