package com.neo.quartz;

import com.neo.entity.SyncFilePlan;
import com.neo.entity.TaskEntity;
import com.neo.service.TaskEntityService;
import com.neo.thread.ThreadPool;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

/**
 * 根据上下文获取spring类
 *
 * @author
 */
@Component

public class InitQuartzJob implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(InitQuartzJob.class);

    @Autowired
    private TaskEntityService taskEntityService;



    @Autowired
    private JobUtils jobUtils;
    public  void init() {
        TaskEntity taskEntity=new TaskEntity();
        taskEntity.setStatus(1);
        List<TaskEntity> taskList=taskEntityService.findAll(taskEntity);
        for(TaskEntity taskEntity1:taskList) {
            List<SyncFilePlan> list=taskEntity1.getSyncFilePlanList();
            for (SyncFilePlan job : list) {
                if(job.getJobStatus()==1) {
                    try {
                        jobUtils.addJob( job);
                    } catch (SchedulerException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void afterPropertiesSet() {
        init();
    }

}

