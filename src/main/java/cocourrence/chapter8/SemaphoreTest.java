package cocourrence.chapter8;

import cocourrence.SleepUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class SemaphoreTest {
    private static final int THREAD_COUNT = 30;
    private static ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_COUNT);
    private static Semaphore s = new Semaphore(10);
    public static void main(String[] args) {
        for (int i = 0; i< THREAD_COUNT; i++) {
            int finalI = i;
            threadPool.execute(() -> {
                try {
                    s.acquire();
                    System.out.println("save data"+ finalI);
                  //  System.out.println(""+s.hasQueuedThreads()+s.getQueueLength()+s.availablePermits());

                  Thread.sleep(1000);

                    s.release();
                } catch (InterruptedException e) {
                }
            });
        }
        threadPool.shutdown();
    }
}
