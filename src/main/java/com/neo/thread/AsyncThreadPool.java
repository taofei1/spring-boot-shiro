package com.neo.thread;

import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 异步任务管理器
 *
 * @author liuhulu
 */
public class AsyncThreadPool {


    /**
     * 单例模式
     */
    private static AsyncThreadPool me = new AsyncThreadPool();
    /**
     * 操作延迟10毫秒
     */
    private final int OPERATE_DELAY_TIME = 10;
    /**
     * 异步操作任务调度线程池
     */
    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5);

    public static AsyncThreadPool getInstance() {
        return me;
    }

    /**
     * 执行任务
     *
     * @param task 任务
     */
    public void execute(TimerTask task) {
        executor.schedule(task, OPERATE_DELAY_TIME, TimeUnit.MILLISECONDS);
    }

    /**
     * 停止任务线程池
     */
    public void shutdown() {
        ThreadUtil.shutdownAndAwaitTermination(executor);
    }


}
