package com.chanl.PoolThreads;

import java.util.concurrent.ThreadFactory;

public class ThreadDuck implements ThreadFactory {
    public Thread newThread(Runnable task){
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        return thread;
    }
}
