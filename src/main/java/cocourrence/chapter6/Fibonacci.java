package cocourrence.chapter6;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public class Fibonacci extends RecursiveTask<Integer> {
    final int n;

    Fibonacci(int n) {
        this.n = n;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //1,1,2,3,5,8
        Fibonacci f = new Fibonacci(7);
        System.out.println(f.compute());

/*
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        // 生成一个计算任务，负责计算1+2+3+4
        Fibonacci a=new Fibonacci(2);
        // 执行一个任务
        Future<Integer> result = forkJoinPool.submit(a);
        System.out.println(result.get());*/
    }

    protected Integer compute() {
        if (n <= 1)
            return n;
        Fibonacci f1 = new Fibonacci(n - 1);
        f1.fork();
        Fibonacci f2 = new Fibonacci(n - 2);
        f2.fork();
        return f2.join() + f1.join();
    }
}
