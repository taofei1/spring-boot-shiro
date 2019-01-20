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
                String A = "������ˮA"; // A¼��������ˮ����
                Thread.sleep(1000);
                String x=exgr.exchange(A);
                System.out.println("x:"+x);
            } catch (InterruptedException e) {
            }
        });
        threadPool.execute(() -> {
            try {
                String B = "������ˮB"; // B¼��������ˮ����
                String A = exgr.exchange("a");
                System.out.println("A��B�����Ƿ�һ�£�" + A.equals(B) + "��A¼����ǣ�"+ A + "��B¼���ǣ�" + B);
            } catch (InterruptedException e) {
            }
        });
        threadPool.shutdown();
    }
}