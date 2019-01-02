package com.neo.service.impl;

import com.neo.dao.ServerDao;
import com.neo.dao.SyncFilePlanDao;
import com.neo.entity.ScheduleJob;
import com.neo.entity.Server;
import com.neo.entity.SyncFilePlan;
import com.neo.quartz.JobUtils;
import com.neo.quartz.QuartzJobFactory;
import com.neo.quartz.QuartzJobFactoryDisallowConcurrentExecution;
import com.neo.service.SyncFilePlanService;
import com.neo.util.PageableUtil;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service("syncFilePlanService")
public class SyncFilePlanServiceImpl implements SyncFilePlanService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SyncFilePlanDao syncFilePlanDao;
    @Autowired
    private ServerDao serverDao;
    @Autowired
    private JobUtils jobUtils;
    public static final String CACHE_KEY   = "fileInfo";

    @Override
    public Page<SyncFilePlan> findAll(int pageNum, int pageSize, List<String> sortName, List<Integer> type, SyncFilePlan syncFilePlan) {
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withMatcher("id", ExampleMatcher.GenericPropertyMatchers.exact()).withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains()).withMatcher("originId", ExampleMatcher.GenericPropertyMatchers.exact()).withMatcher("originPath", ExampleMatcher.GenericPropertyMatchers.contains()).withMatcher("targetId", ExampleMatcher.GenericPropertyMatchers.exact()).withMatcher("targetPath", ExampleMatcher.GenericPropertyMatchers.contains()).withMatcher("startTime", ExampleMatcher.GenericPropertyMatchers.contains()).withMatcher("endTime", ExampleMatcher.GenericPropertyMatchers.contains()).withMatcher("cronExpression", ExampleMatcher.GenericPropertyMatchers.contains()).withMatcher("jobStatus", ExampleMatcher.GenericPropertyMatchers.exact()).withMatcher("isConcurrent", ExampleMatcher.GenericPropertyMatchers.exact()).withMatcher("className", ExampleMatcher.GenericPropertyMatchers.contains()).withMatcher("methodName", ExampleMatcher.GenericPropertyMatchers.contains()).withMatcher("methodParam", ExampleMatcher.GenericPropertyMatchers.contains());

        Example<SyncFilePlan> ex = Example.of(syncFilePlan, matcher);
        Pageable pageable = PageableUtil.getPageable(pageNum, pageSize, sortName, type);
        return syncFilePlanDao.findAll(ex, pageable);
    }

    @Override
    public Page<SyncFilePlan> findAll(int pageNum, int pageSize, List<String> sortName, List<Integer> type) {
        return syncFilePlanDao.findAll(PageableUtil.getPageable(pageNum, pageSize, sortName, type));
    }

    @Override
    public List<SyncFilePlan> findAll() {
        return syncFilePlanDao.findAll();
    }

    @Override
    public List<SyncFilePlan> findAll(List<Integer> ids) {
        return syncFilePlanDao.findAll(ids);
    }

    @Override
    public List<SyncFilePlan> findAll(SyncFilePlan syncFilePlan) {
        Example<SyncFilePlan> ex = Example.of(syncFilePlan);
        return syncFilePlanDao.findAll(ex);
    }

    @Override
    public void delete(Integer id) {
        SyncFilePlan syncFilePlan = syncFilePlanDao.findOne(id);
        syncFilePlanDao.delete(syncFilePlan);
        jobUtils.deleteJob(syncFilePlan.getName(), syncFilePlan.getTaskEntity().getGroupName());

    }

    @Override
    @Transactional
    public void delete(List<Integer> ids) {

        for (Integer id : ids) {
            delete(id);
        }
    }

    @Override
    public SyncFilePlan findById(Integer id) {
        SyncFilePlan syncFilePlan= syncFilePlanDao.findOne(id);
        if(syncFilePlan.getOriginId()!=-1){
            syncFilePlan.setOriginServer(serverDao.findOne(syncFilePlan.getOriginId()));
        }
        if(syncFilePlan.getTargetId()!=-1){
            syncFilePlan.setTargetServer(serverDao.findOne(syncFilePlan.getTargetId()));
        }
        return syncFilePlan;
    }

    @Override
    public SyncFilePlan save(SyncFilePlan syncFilePlan)  {
        SyncFilePlan plan=syncFilePlanDao.save(syncFilePlan);
        if (syncFilePlan.getId() == null) {
            try {
                jobUtils.addJob(plan);
            } catch (SchedulerException e) {
                e.printStackTrace();
                log.error("添加"+plan.getName()+"任务失败!");
            }
        }else{

            try {
                if(syncFilePlan.getJobStatus()==1&&plan.getTaskEntity()!=null&&plan.getTaskEntity().getStatus()==1) {
                    jobUtils.updateJobDetail(plan);
                }
            } catch (SchedulerException e) {
                e.printStackTrace();
                log.error("修改"+plan.getName()+"任务失败!");
            }
        }
        return plan;
    }

    @Override
    public SyncFilePlan updateStatus(SyncFilePlan syncFilePlan)  {
        SyncFilePlan syncFilePlan1 = syncFilePlanDao.save(syncFilePlan);
        if (syncFilePlan1.getJobStatus() == 1) {
            try {
                jobUtils.addJob(syncFilePlan1);
            } catch (SchedulerException e) {
                e.printStackTrace();
                log.error("开启"+syncFilePlan1.getName()+"任务失败!");
            }
        } else if(syncFilePlan.getTaskEntity()!=null){

            jobUtils.deleteJob(syncFilePlan1.getId().toString(), syncFilePlan.getTaskEntity().getGroupName());

        }
        return syncFilePlan1;
    }

    @Override
    @Transactional
    public int updateIsConcurrent(Integer id, Integer status) {
        int count=0;
        try{
            count=syncFilePlanDao.updateIsConcurrent(id,status);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return count;
        }
    }
}
