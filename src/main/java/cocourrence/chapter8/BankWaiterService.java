package cocourrence.chapter8;

import java.util.Map;
import java.util.concurrent.*;

public class BankWaiterService implements Runnable {
    private CyclicBarrier c=new CyclicBarrier(4,this);
    private Executor executor= Executors.newFixedThreadPool(4);
    private ConcurrentHashMap<String,Integer> sheetBankWaterCount=new ConcurrentHashMap<>();

    public static void main(String[] args) {
        BankWaiterService bws = new BankWaiterService();
        bws.count();
    }

    private void count(){
        for (int i = 0; i < 10; i++) {
            executor.execute(() -> {
                sheetBankWaterCount.put(Thread.currentThread().getName(), 1);
                try {
                    c.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void run() {
        int result = 0;
        for (Map.Entry<String, Integer> sheet : sheetBankWaterCount.entrySet()) {
            result += sheet.getValue();
        }
        sheetBankWaterCount.put("result", result);
        System.out.println(result);
    }
}
