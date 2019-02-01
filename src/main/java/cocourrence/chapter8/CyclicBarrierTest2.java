package cocourrence.chapter8;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("Duplicates")
public class CyclicBarrierTest2 {
    static CyclicBarrier c = new CyclicBarrier(101, new A());
    private static AtomicInteger a = new AtomicInteger();

    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                a.incrementAndGet();
                try {
                    c.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }

            }).start();
        }

        c.await();
        System.out.println(a);
    }

    static class A implements Runnable{
        @Override
        public void run() {
            System.out.println(3);
        }
    }
}
