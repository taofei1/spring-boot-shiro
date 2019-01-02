package com.neo.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class Monitor {
    private Logger log= LoggerFactory.getLogger(getClass());
    @Autowired
    private Scheduler scheduler;
  //  private ThreadPoolExecutor tpe= CustomThreadPoolExecutor.getThreadPoolExecutor();
    @Scheduled(fixedRate = 30000)
    public  void getAllJobs(){
     /*   if(tpe.getActiveCount()>0) {
            log.info("active count {}", tpe.getActiveCount());
        }*/
        try {
            List<JobExecutionContext> jobContexts = scheduler.getCurrentlyExecutingJobs();
            if(jobContexts.size()>0) {
               StringBuffer sb=new StringBuffer("任务名:");
                for (JobExecutionContext context : jobContexts) {
                    sb.append(context.getTrigger().getJobKey().getName()+",");
                }
               log.info("正在运行的job数："+jobContexts.size()+","+sb.substring(0,sb.length()-1).toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

    }

}
