package com.neo.web;

import com.neo.annotation.Log;
import com.neo.entity.SyncFilePlan;
import com.neo.entity.TaskEntity;
import com.neo.enums.OperateType;
import com.neo.service.TaskEntityService;
import com.neo.task.TaskUtils;
import com.neo.util.ExceptionUtil;
import com.neo.util.Msg;
import com.neo.util.Util;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Controller
@RequestMapping("/taskEntity")
public class TaskEntityController {
    private Logger log= LoggerFactory.getLogger(this.getClass());
    @Autowired
    public TaskEntityService taskEntityService;
    @GetMapping("/search")
    @ResponseBody
    public Msg findAll(TaskEntity taskEntity){

        return Msg.success().addData(taskEntityService.findAll(taskEntity));
    }
    @GetMapping("/taskEntityList")
    @ResponseBody
    public Page<TaskEntity> findAll(@RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize, @RequestParam(value = "sortName",required = false) List<String> sortName, @RequestParam(value="sortOrder",required = false)List<Integer> type, TaskEntity taskEntity)  {
        Page<TaskEntity> page;
        if(Util.isNull(taskEntity)) {
            page = taskEntityService.findAll(pageNum, pageSize, sortName, type);
        }else{
            page=taskEntityService.findAll(pageNum, pageSize, sortName, type,taskEntity);
        }
        return page;
    }
    @GetMapping("/{id}")
    @ResponseBody
    public Msg getById(@PathVariable("id") Integer id){
        if(id==null){
            return Msg.fail().addData("no parameter id");
        }
        log.info("2");
        TaskEntity server=taskEntityService.findById(id);
        if(server==null){
            return Msg.fail().addData("not exist taskEntity[id="+id+"]");
        }
        return Msg.success().addData(server);
    }
    @GetMapping("/all")
    @ResponseBody
    public Msg finAll()
    {

        return Msg.success().addData(taskEntityService.findAll());
    }
    @PostMapping("/saveOrUpdate")
    @ResponseBody
    @Log(title = "任务总表",businessType = OperateType.INSERT)

    public Msg add(TaskEntity taskEntity)  {
        if(taskEntity.getId()==null){
            taskEntity.setStatus(1);
            try {
                taskEntityService.save(taskEntity, false);
            } catch (SchedulerException e) {
                return Msg.fail().addData(ExceptionUtil.getStackTraceInfo(e));
            }
        }else {
            log.info("3");
            TaskEntity task = taskEntityService.findById(taskEntity.getId());
            taskEntity.setSyncFilePlanList(task.getSyncFilePlanList());
            try {
                if (!taskEntity.equals(task)) {
                    taskEntityService.save(taskEntity, true);
                } else {
                    taskEntityService.save(taskEntity, false);
                }
            } catch (SchedulerException e) {
                e.printStackTrace();
                return Msg.fail().addData(ExceptionUtil.getStackTraceInfo(e));
            }
        }
        return Msg.success();
    }
    @PostMapping("/delete")
    @ResponseBody
    @Log(title = "任务总表",businessType = OperateType.DELETE)
    public Msg delete(@RequestBody List<Integer> ids){
        taskEntityService.delete(ids);
        return Msg.success();
    }
    @PostMapping("/status")
    @ResponseBody
    @Log(title = "任务总表",businessType = OperateType.UPDATE)
    public Msg changeStatus(Integer id,Integer status){
        log.info("4");
        TaskEntity taskEntity=taskEntityService.findById(id);
        if(taskEntity==null){
            return Msg.fail().addData("TaskEntity[id="+id+"]not exist！");
        }
        taskEntity.setStatus(status);
        TaskEntity newTask= null;
        try {
            newTask = taskEntityService.save(taskEntity,false);
        } catch (SchedulerException e) {
            e.printStackTrace();
            return Msg.fail().addData(ExceptionUtil.getStackTraceInfo(e));
        }
        try {
            if (status == 1) {
                taskEntityService.addOrRemoveJob(newTask, true);

            } else {
                taskEntityService.addOrRemoveJob(newTask, false);
            }
        }catch (SchedulerException e){
            e.printStackTrace();
            return Msg.fail().addData(ExceptionUtil.getStackTraceInfo(e));
        }
        return Msg.success();
    }
    @PostMapping("/invoke")
    @ResponseBody
    @Log(title = "任务总表",businessType = OperateType.OTHER)
    public Msg executeMethod(Integer id){
        log.info("5");
        TaskEntity te=taskEntityService.findById(id);
        if(te==null){
            return Msg.fail().addData("任务[id="+id+"]不存在!");
        }
        List<SyncFilePlan> planList=te.getSyncFilePlanList();
        for(SyncFilePlan job:planList){
            try {
                TaskUtils.invokMethod(job);
            } catch (Exception e) {
                e.printStackTrace();
                return Msg.fail().addData(ExceptionUtil.getStackTraceInfo(e));
            }
        }
        return Msg.success();
    }
}
