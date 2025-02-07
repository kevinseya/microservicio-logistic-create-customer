package com.microserviciologistic.createcustomer.service;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledFuture;

@Service
public class ReconnectionScheduler {
    private final ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    private ScheduledFuture<?> reconnectTask;

    public ReconnectionScheduler() {
        taskScheduler.initialize();
    }

    public void scheduleReconnect(Runnable reconnectTask) {
        if (this.reconnectTask != null && !this.reconnectTask.isCancelled()) {
            return;
        }
        System.out.println("Scheduling WebSocket reconnection...");
        this.reconnectTask = taskScheduler.scheduleWithFixedDelay(reconnectTask, 5000);
    }

    public void cancelReconnect() {
        if (reconnectTask != null) {
            reconnectTask.cancel(false);
            reconnectTask = null;
        }
    }
}
