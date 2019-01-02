package com.neo.quartz;

import com.neo.entity.SyncFilePlan;
import com.neo.service.SyncFilePlanService;
import com.neo.task.TaskUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class QuartzJobFactory implements Job {
    private final Logger log= LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SyncFilePlanService syncFilePlanService;
    @Override
    public void execute(JobExecutionContext jobExecutionContext)  {
        SyncFilePlan syncFilePlan=(SyncFilePlan)jobExecutionContext.getMergedJobDataMap().get("scheduleJob");
        syncFilePlanService.updateIsConcurrent(syncFilePlan.getId(),1);
        try {
            TaskUtils.invokMethod(syncFilePlan);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            syncFilePlanService.updateIsConcurrent(syncFilePlan.getId(),0);
        }



    }
}
