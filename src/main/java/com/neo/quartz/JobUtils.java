package com.neo.quartz;

import com.neo.entity.ScheduleJob;
import com.neo.entity.SyncFilePlan;
import com.neo.entity.TaskEntity;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class JobUtils {
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;
    @Autowired
    private Scheduler scheduler;
    public   void addJob(SyncFilePlan job) throws SchedulerException {
        if (job == null||job.getJobStatus()==0) {
            return;
        }
        TaskEntity taskEntity=job.getTaskEntity();
        if(taskEntity==null||taskEntity.getStatus()==0){
            return;
        }
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        TriggerKey triggerKey = TriggerKey.triggerKey(job.getId().toString(), taskEntity.getGroupName());


        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);


        // 不存在，创建一个
        if (null == trigger) {
            Class clazz = ScheduleJob.CONCURRENT_IS==job.getIsConcurrent() ? QuartzJobFactoryDisallowConcurrentExecution.class:QuartzJobFactory.class;



            JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(job.getId().toString(), taskEntity.getGroupName()).usingJobData("param", taskEntity.getParam()).build();


            jobDetail.getJobDataMap().put("scheduleJob", job);


            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());


            trigger = TriggerBuilder.newTrigger().withDescription(job.getName()).withIdentity(job.getId().toString(), taskEntity.getGroupName())
                    .withSchedule(scheduleBuilder).withPriority(job.getPriority()==null?Trigger.DEFAULT_PRIORITY:job.getPriority()).build();


            scheduler.scheduleJob(jobDetail, trigger);
        } else {
            // Trigger已存在，那么更新相应的定时设置
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());


            // 按新的cronExpression表达式重新构建trigger
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).usingJobData("param",taskEntity.getParam()).withSchedule(scheduleBuilder).build();


            // 按新的trigger重新设置job执行
            scheduler.rescheduleJob(triggerKey, trigger);
        }
        Scheduler scheduler1 = new StdSchedulerFactory().getScheduler();

        for (String groupName : scheduler1.getJobGroupNames()) {

            for (JobKey jobKey : scheduler1.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {

                String jobName = jobKey.getName();
                String jobGroup = jobKey.getGroup();

                //get job's trigger
                List<Trigger> triggers = (List<Trigger>) scheduler1.getTriggersOfJob(jobKey);
                Date nextFireTime = triggers.get(0).getNextFireTime();

                System.out.println("[jobName] : " + jobName + " [groupName] : "
                        + jobGroup + " - " + nextFireTime);

            }

        }
    }
    public void deleteJob(String jobName, String groupName) {
        JobKey jobKey = JobKey.jobKey(jobName, groupName);
        if(jobKey==null){
            return;
        }
        try {
                scheduler.deleteJob(jobKey);

        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
    public void deleteJob(String groupName)   {
        GroupMatcher<JobKey> gm = GroupMatcher.groupEquals(groupName);
        try {
            Set<JobKey> set = scheduler.getJobKeys(gm);
                scheduler.deleteJobs(new ArrayList<>(set));

        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
    //对于一个修改例如类或者方法等参数时，先删除job再添加
    public void updateJobDetail(SyncFilePlan syncFilePlan) throws SchedulerException {
        deleteJob(syncFilePlan.getId().toString(),syncFilePlan.getTaskEntity().getGroupName());
        addJob(syncFilePlan);
    }

}
