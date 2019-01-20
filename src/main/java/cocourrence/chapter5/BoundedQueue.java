package cocourrence.chapter5;

import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedQueue<T> {
    private Object[] items;
    private int addIndex, removeIndex, count;
    private Lock lock = new ReentrantLock();
    private Condition notEmpty = lock.newCondition();
    private Condition notFull = lock.newCondition();

    public BoundedQueue(int size) {
        items = new Object[size];
    }

    public static void main(String[] args) throws InterruptedException {
        BoundedQueue boundedQueue=new BoundedQueue(5);
        for(int i=0;i<10;i++){
            int finalI = i;
            new Thread(()-> {
                try {
                    boundedQueue.add(finalI);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        Thread.sleep(1000);
        boundedQueue.remove(0);
        boundedQueue.remove(2);
        boundedQueue.remove(0);
        boundedQueue.remove(2);
        boundedQueue.remove(0);
        boundedQueue.remove(2);


    }
    public void add(T t) throws InterruptedException {
        lock.lock();
        //数组已满,线程等待且释放锁
        try {

            while (count == items.length)
                notFull.await();
            //将addIndex位置赋值
            items[addIndex]=t;
            //数组添加到最后一位则从头开始
            if(++addIndex==items.length)
                addIndex=0;
            ++count;
            //数组有元素，提醒移除线程
            notEmpty.signal();
            System.out.println(Arrays.asList(items));

        }finally {
            lock.unlock();
        }
    }
    public T remove(T t) throws InterruptedException {
        lock.lock();
        //数组为空,线程等待且释放锁
        try {
            while (count == 00)
                notEmpty.await();
            //将addIndex位置赋值
            Object x = items[removeIndex];
            //数组添加到最后一位则从头开始
            if(++removeIndex==items.length)
                removeIndex=0;
            --count;
            //数组有空，提醒添加线程
            notFull.signal();
            System.out.println(Arrays.asList(items));
            return (T)x;
        }finally {
            lock.unlock();
        }
    }
}
