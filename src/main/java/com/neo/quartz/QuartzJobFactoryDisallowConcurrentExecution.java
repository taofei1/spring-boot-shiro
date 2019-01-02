package com.neo.quartz;
import com.neo.entity.SyncFilePlan;
import com.neo.service.SyncFilePlanService;
import com.neo.task.TaskUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @Description: 若一个方法一次执行不完下次轮转时则等待改方法执行完后才执行下一次操作
 * @author wison
 * @date 2017年11月11日 下午5:05:47
 */
@DisallowConcurrentExecution
public class QuartzJobFactoryDisallowConcurrentExecution implements Job {
    public final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SyncFilePlanService syncFilePlanService;

    @Override
    public void execute(JobExecutionContext context)  {
        SyncFilePlan syncFilePlan = (SyncFilePlan) context.getMergedJobDataMap().get("scheduleJob");
        int count=syncFilePlanService.updateIsConcurrent(syncFilePlan.getId(),1);
        System.out.println(count);
        try {
            TaskUtils.invokMethod(syncFilePlan);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            syncFilePlanService.updateIsConcurrent(syncFilePlan.getId(),0);
        }


    }
}
