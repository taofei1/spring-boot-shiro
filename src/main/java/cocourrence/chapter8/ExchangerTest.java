package cocourrence.chapter8;

import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExchangerTest {
    private static final Exchanger<String> exgr = new Exchanger<String>();
    private static ExecutorService threadPool = Executors.newFixedThreadPool(2);
    public static void main(String[] args) {
        threadPool.execute(() -> {
            try {
                String A = "银行流水A"; // A录入银行流水数据
                Thread.sleep(1000);
                String x=exgr.exchange(A);
                System.out.println("x:"+x);
            } catch (InterruptedException e) {
            }
        });
        threadPool.execute(() -> {
            try {
                String B = "银行流水B"; // B录入银行流水数据
                String A = exgr.exchange("a");
                System.out.println("A和B数据是否一致：" + A.equals(B) + "，A录入的是："+ A + "，B录入是：" + B);
            } catch (InterruptedException e) {
            }
        });
        threadPool.shutdown();
    }
}