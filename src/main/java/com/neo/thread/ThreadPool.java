package com.neo.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class ThreadPool {
    private static Logger log= LoggerFactory.getLogger(ThreadPool.class);
    private static volatile ThreadPool threadPool;
    private static HashMap<String,Thread> threadMap=null;
    private ThreadPool(){
        load();
    }
    public static ThreadPool getThreadPool(){
        if(threadPool==null){
            synchronized (ThreadPool.class){
                if(threadPool==null){
                    threadPool=new ThreadPool();
                }
            }
        }
        return threadPool;
    }
    public static void load(){
        threadMap = new HashMap<>();

        threadMap.put("schedule_task",new ScheduleTaskThread());
    }
    //获取线程列表
    public HashMap<String,Thread> getThreadMap()
    {
        return threadMap;
    }

    //启动线程
    public static void start(String name)
    {
        Thread t = getThreadPool().getThreadMap().get(name);

        if ( t != null )
        {
            if ( t.getState() == Thread.State.NEW )
            {
                t.start();
            }
            else if ( t.getState() == Thread.State.TERMINATED )
            {
                t = new ScheduleTaskThread();

                t.start();

                threadMap.put("schedule_task",t);
            }
        }
    }

    //关闭线程
    public static void stop(String name) throws Exception
    {
        ScheduleTaskThread t = (ScheduleTaskThread)getThreadPool().getThreadMap().get(name);

        t.exit = true;

        t.join();
    }
}
