package cocourrence.chapter4;

import cocourrence.SleepUtils;

import java.util.concurrent.CountDownLatch;

public class DefaultThreadPoolTest {
    static DefaultThreadPool pool=new DefaultThreadPool();
    static CountDownLatch c=new CountDownLatch(20);
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 20; i++) {
            pool.execute(() -> {

                System.out.println("Thread:"+Thread.currentThread());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                c.countDown();

            });


        }
        c.await();
        pool.shutdown();
        /**
         * 如果不继续执行Job，工作不会停止因为在
         * 没有Job的时候，线程会阻塞不会向下执行判断
         * running是否为true
         */
        for (int i = 0; i < 5; i++) {
            pool.execute(() -> {
                System.out.println("Thread:"+Thread.currentThread());

            });


        }
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        ThreadGroup topGroup = group;
// 遍历线程组树，获取根线程组
        while (group != null) {
            topGroup = group;
            group = group.getParent();
        }
// 激活的线程数加倍
        int estimatedSize = topGroup.activeCount() * 2;
        Thread[] slackList = new Thread[estimatedSize];
// 获取根线程组的所有线程
        int actualSize = topGroup.enumerate(slackList);
// copy into a list that is the exact size
        Thread[] list = new Thread[actualSize];
        System.arraycopy(slackList, 0, list, 0, actualSize);
        System.out.println("Thread list size == " + list.length);
        for (Thread thread : list) {
            System.out.println(thread.getName());
        }

    }
}
