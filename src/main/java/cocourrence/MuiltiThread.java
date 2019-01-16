package cocourrence;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public class MuiltiThread {
    public static void main(String[] args){
        ThreadMXBean threadMXBean= ManagementFactory.getThreadMXBean();
        ThreadInfo[] threadInfo=threadMXBean.dumpAllThreads(false,false);
        for(ThreadInfo t:threadInfo){
            System.out.println(t.getThreadId()+t.getThreadName());
        }
    }
}

