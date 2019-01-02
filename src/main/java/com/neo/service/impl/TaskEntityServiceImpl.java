package com.neo.service.impl;

import com.neo.dao.TaskEntityDao;
import com.neo.entity.SyncFilePlan;
import com.neo.entity.TaskEntity;
import com.neo.quartz.JobUtils;
import com.neo.service.SyncFilePlanService;
import com.neo.service.TaskEntityService;
import com.neo.util.PageableUtil;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
@Service("taskEntityService")
public class TaskEntityServiceImpl implements TaskEntityService{
    @Autowired
    private TaskEntityDao taskEntityDao;
    @Autowired
    private SyncFilePlanService syncFilePlanService;
    @Autowired
    private JobUtils jobUtils;
    @Override
    public Page<TaskEntity> findAll(int pageNum, int pageSize, List<String> sortName, List<Integer> type, TaskEntity taskEntity) {

        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withMatcher("id", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("groupName", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("className", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("methodName", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("param", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("status", ExampleMatcher.GenericPropertyMatchers.exact());
        Example<TaskEntity> ex = Example.of(taskEntity, matcher);
        Pageable pageable= PageableUtil.getPageable(pageNum,pageSize,sortName,type);
        return taskEntityDao.findAll(ex,pageable);
    }

    @Override
    public Page<TaskEntity> findAll(int pageNum, int pageSize, List<String> sortName, List<Integer> type) {
        return taskEntityDao.findAll(PageableUtil.getPageable(pageNum,pageSize,sortName,type));

    }

    @Override
    public List<TaskEntity> findAll() {
        return taskEntityDao.findAll();
    }

    @Override
    public List<TaskEntity> findAll(List<Integer> ids) {
        return taskEntityDao.findAll(ids);
    }

    @Override
    public List<TaskEntity> findAll(TaskEntity taskEntity) {
        Example<TaskEntity> ex = Example.of(taskEntity);
        return taskEntityDao.findAll(ex);
    }

    @Override
    public void delete(Integer id) {
        TaskEntity taskEntity=taskEntityDao.findOne(id);

        if(taskEntity!=null){
            List<SyncFilePlan> list=taskEntity.getSyncFilePlanList();
            for(SyncFilePlan syncFilePlan:list){
                syncFilePlan.setTaskEntity(null);
                syncFilePlanService.save(syncFilePlan);
            }
            taskEntityDao.delete(id);
            jobUtils.deleteJob(taskEntity.getGroupName());
        }

    }

    @Override
    @Transactional
    public void delete(List<Integer> ids) {
        for(Integer id:ids){
            delete(id);
        }
    }

    @Override
    public TaskEntity findById(Integer id) {
        System.out.println(11111111);
        return taskEntityDao.findOne(id);
    }
    @Override
    public void addOrRemoveJob(TaskEntity taskEntity,boolean add) throws SchedulerException {
    if(taskEntity!=null) {
        if (add) {
            List<SyncFilePlan> list = taskEntity.getSyncFilePlanList();
            for (SyncFilePlan syncFilePlan : list) {
                if (syncFilePlan.getJobStatus() == 1) {
                    jobUtils.addJob(syncFilePlan);
                }
            }
        } else {
            jobUtils.deleteJob(taskEntity.getGroupName());
        }
    }
    }
    @Override
    public TaskEntity save(TaskEntity taskEntity,boolean isUpdateJob) throws SchedulerException {
        TaskEntity te=taskEntityDao.save(taskEntity);
        if (taskEntity.getId() == null) {
            addOrRemoveJob(te,true);
        }else{

            if(te.getStatus()==1) {
                if(isUpdateJob){
                    List<SyncFilePlan> list=te.getSyncFilePlanList();
                    for(SyncFilePlan syncFilePlan:list){
                        if(syncFilePlan.getJobStatus()==1){
                            jobUtils.updateJobDetail(syncFilePlan);
                        }

                    }
                }
            }

        }
        return te;
    }
}
