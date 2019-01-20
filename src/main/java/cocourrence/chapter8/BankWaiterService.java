package cocourrence.chapter8;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BankWaiterService implements Runnable {
    private CyclicBarrier c=new CyclicBarrier(4,this);
    private Executor executor= Executors.newFixedThreadPool(4);
    private ConcurrentHashMap<String,Integer> sheetBankWaterCount=new ConcurrentHashMap<>();
    private void count(){
        for (int i = 0; i < 4; i++) {
            executor.execute(() -> {

            });
        }
    }
    @Override
    public void run() {

    }
}
