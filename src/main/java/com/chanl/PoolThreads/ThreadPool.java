package com.chanl.PoolThreads;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;

public class ThreadPool {
    private int DELAY; //Задержка перед первым выполнением таска
    private int PERIOD; //Период повтора
    private ThreadDuck duck; //Описание потока (ThreadFactory)
    private final int THREAD_COUNT = 2; //Размер пула
    private static ScheduledExecutorService pool;

    /* Сеттеры */
    public void setDelay(int delay){
        this.DELAY = delay;
    }
    public int getDelay(){
        return this.DELAY;
    }

    public void setPeriod(int delay){
        this.PERIOD = delay;
    }
    public int getPeriod(){
        return this.PERIOD;
    }
    /*****/

    public ThreadPool(){
        this.duck = new ThreadDuck();
        pool = Executors.newScheduledThreadPool(THREAD_COUNT, duck);
    }

    public ThreadPool(int delay, int period){
        this.duck = new ThreadDuck();
        pool = Executors.newScheduledThreadPool(THREAD_COUNT, duck);
        this.DELAY = delay;
        this.PERIOD = period;
    }

    /** Добавить таск в пул потоков
     * @param task
     * @param unit
     */
    public void addTask(Runnable task, TimeUnit unit){
        pool.scheduleAtFixedRate(task, DELAY, PERIOD, unit);
    }
}
