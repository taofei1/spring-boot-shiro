package com.neo.thread;

import com.neo.entity.SyncFilePlan;
import com.neo.entity.TaskEntity;
import com.neo.service.TaskEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ScheduleTaskThread extends Thread {
    private Logger log= LoggerFactory.getLogger(ScheduleTaskThread.class);
    public volatile boolean exit=false;
    private TaskEntityService taskEntityService;

    public void run() {
        log.info("计划任务启动");
        boolean isEnd=false;
        boolean isExecute=true;
        TaskEntity taskEntity=new TaskEntity();
        taskEntity.setStatus(1);
        List<TaskEntity> list=taskEntityService.findAll(taskEntity);
        for(TaskEntity te:list){
            List<SyncFilePlan> planList=te.getSyncFilePlanList();
            for(SyncFilePlan plan:planList){

            }
        }

    }
}
