package com.neo.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomThreadPoolExecutor {
    private static Logger log= LoggerFactory.getLogger(CustomThreadPoolExecutor.class);

    private static ThreadPoolExecutor pool = null;


    /**
     * �̳߳س�ʼ������
     *
     * corePoolSize �����̳߳ش�С----10
     * maximumPoolSize ����̳߳ش�С----30
     * keepAliveTime �̳߳��г���corePoolSize��Ŀ�Ŀ����߳������ʱ��----30+��λTimeUnit
     * TimeUnit keepAliveTimeʱ�䵥λ----TimeUnit.MINUTES
     * workQueue ��������----new ArrayBlockingQueue<Runnable>(10)====10��������������
     * threadFactory �½��̹߳���----new CustomThreadFactory()====���Ƶ��̹߳���
     * rejectedExecutionHandler ���ύ����������maxmumPoolSize+workQueue֮��ʱ,
     * 							�����ύ��41������ʱ(ǰ���̶߳�û��ִ����,�˲��Է�������sleep(100)),
     * 						          ����ύ��RejectedExecutionHandler������
     */
    private CustomThreadPoolExecutor() {
            pool = new ThreadPoolExecutor(10, 200, 30, TimeUnit.HOURS, new LinkedBlockingQueue<>(), new CustomThreadFactory(), new CustomRejectedExecutionHandler());

    }


    public void destory() {
        if(pool != null) {
            pool.shutdownNow();
        }
    }


    public static synchronized ThreadPoolExecutor getThreadPoolExecutor() {
        if(pool==null){
            new CustomThreadPoolExecutor();
        }
        return pool;
    }

    private class CustomThreadFactory implements ThreadFactory {

        private AtomicInteger count = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            String threadName = CustomThreadPoolExecutor.class.getSimpleName() + count.addAndGet(1);
            System.out.println(threadName);
            t.setName(threadName);
            return t;
        }
    }


    private class CustomRejectedExecutionHandler implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            log.info(executor.getActiveCount()+"����");
        }
    }



    // ���Թ�����̳߳�
    public static void main(String[] args) {


        ExecutorService pool = CustomThreadPoolExecutor.getThreadPoolExecutor();
        for(int i=1; i<100; i++) {
            System.out.println("�ύ��" + i + "������!");
            pool.execute(() -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("running=====");
            });
        }



        // 2.����----�˴���������,��Ϊ����û���ύִ����,��������̳߳�,����Ҳ���޷�ִ����
        // exec.destory();

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}