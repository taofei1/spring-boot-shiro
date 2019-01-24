package cocourrence.chapter10;

import java.util.concurrent.*;

public class FutureTaskTest {
    private final ConcurrentHashMap<Object,Future<String>> taskCache= new ConcurrentHashMap<>();
    private String executionTask(final String taskname){
        while (true){
            Future<String> future=taskCache.get(taskname);
            if(future==null){
                Callable<String> task= () -> taskname;
                FutureTask<String> futureTask=new FutureTask<>(task);
                future=taskCache.putIfAbsent(taskname,futureTask);
                if(future==null){
                    future=futureTask;
                    futureTask.run();
                }
            }
            try {
                return future.get();
            } catch (InterruptedException e) {
                taskCache.remove(taskname,future);
                e.printStackTrace();
            } catch (ExecutionException e) {
                taskCache.remove(taskname,future);
                e.printStackTrace();
            }


        }
    }
}
