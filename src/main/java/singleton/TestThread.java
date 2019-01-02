package singleton;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class TestThread {
    public static void main(String[] args) throws InterruptedException {
        final Set<Object> set= Collections.synchronizedSet(new HashSet<>());
        int count=1000;
        CountDownLatch latch=new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            new Thread(() -> {
                set.add(Singleton2.getInstance());
                latch.countDown();
            }).start();

        }
        latch.await();
        System.out.println(set.size());
    }
}
