package com.neo.service;

import com.neo.entity.TaskEntity;
import org.quartz.SchedulerException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TaskEntityService {
    Page<TaskEntity> findAll(int pageNum, int pageSize, List<String> sortName, List<Integer> type, TaskEntity taskEntity);
    Page<TaskEntity> findAll(int pageNum, int pageSize, List<String> sortName, List<Integer> type );
    List<TaskEntity> findAll();
    List<TaskEntity> findAll(List<Integer> ids);
    List<TaskEntity> findAll(TaskEntity taskEntity);
    void delete(Integer id);
    void delete(List<Integer> ids);
    TaskEntity findById(Integer id);
    TaskEntity save(TaskEntity taskEntity,boolean isUpdateJob) throws SchedulerException;
    void addOrRemoveJob(TaskEntity taskEntity,boolean add) throws SchedulerException;
}
