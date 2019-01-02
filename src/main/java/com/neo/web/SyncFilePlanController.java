package com.neo.web;

import com.neo.annotation.Log;
import com.neo.entity.DateInterval;
import com.neo.entity.SyncFilePlan;
import com.neo.entity.TaskEntity;
import com.neo.enums.OperateType;
import com.neo.service.DateIntervalService;
import com.neo.service.SyncFilePlanService;
import com.neo.service.TaskEntityService;
import com.neo.task.TaskUtils;
import com.neo.util.ExceptionUtil;
import com.neo.util.Msg;
import com.neo.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/syncFilePlan")
public class SyncFilePlanController {
    private Logger log= LoggerFactory.getLogger(this.getClass());
    @Autowired
    public SyncFilePlanService syncFilePlanService;
    @Autowired
    private DateIntervalService dateIntervalService;
    @Autowired
    private TaskEntityService taskEntityService;
    @GetMapping("/search")
    @ResponseBody
    public Msg findAll(SyncFilePlan syncFilePlan){

        return Msg.success().addData(syncFilePlanService.findAll(syncFilePlan));
    }
    @GetMapping("/syncFilePlanList")
    @ResponseBody
    public Page<SyncFilePlan> findAll(@RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize, @RequestParam(value = "sortName",required = false) List<String> sortName, @RequestParam(value="sortOrder",required = false)List<Integer> type, SyncFilePlan syncFilePlan)  {
        log.info(syncFilePlan.toString());
        Page<SyncFilePlan> page;
        if(Util.isNull(syncFilePlan)) {
            page = syncFilePlanService.findAll(pageNum, pageSize, sortName, type);
        }else{
            page=syncFilePlanService.findAll(pageNum, pageSize, sortName, type,syncFilePlan);
        }
        return page;
    }
    @GetMapping("/{id}")
    @ResponseBody
    public Msg getById(@PathVariable("id") Integer id){
        if(id==null){
            return Msg.fail().addData("no parameter id");
        }
        SyncFilePlan syncFilePlan=syncFilePlanService.findById(id);
        if(syncFilePlan==null){
            return Msg.fail().addData("not exist syncFilePlan[id="+id+"]");
        }
        log.info(syncFilePlan.getOriginId().toString());
        return Msg.success().addData(syncFilePlan);
    }
    @GetMapping("/all")
    @ResponseBody
    public Msg finAll()
    {

        return Msg.success().addData(syncFilePlanService.findAll());
    }
    @PostMapping("/saveOrUpdate")
    @ResponseBody
    @Log(title = "文件同步计划表",businessType = OperateType.INSERT)
    public Msg add(SyncFilePlan syncFilePlan,Integer dateIntervalId,Integer taskEntityId)  {

        if(dateIntervalId!=null&&dateIntervalId!=0){
            DateInterval dateInterval=dateIntervalService.findById(dateIntervalId);
            if(dateInterval==null){
                return Msg.fail().addData("日期间隔[id="+dateIntervalId+"]不存在！");
            }
            syncFilePlan.setDateInterval(dateInterval);
        }
        if(taskEntityId!=null&&taskEntityId!=0){
            log.info("1");
            TaskEntity taskEntity=taskEntityService.findById(taskEntityId);
            if(taskEntity==null){
                return Msg.fail().addData("任务组[id="+taskEntityId+"]不存在！");
            }
            syncFilePlan.setTaskEntity(taskEntity);
        }
        if(syncFilePlan.getId()==null){
            //启用任务，且未在运行
            syncFilePlan.setJobStatus(1);
            syncFilePlan.setIsConcurrent(0);

        }
        else{
            SyncFilePlan plan=syncFilePlanService.findById(syncFilePlan.getId());
            syncFilePlan.setIsConcurrent(plan.getIsConcurrent());
        }
        log.info(syncFilePlan.toString());
        syncFilePlanService.save(syncFilePlan);
        return Msg.success();
    }
    @PostMapping("/delete")
    @ResponseBody
    @Log(title = "文件同步计划表",businessType = OperateType.DELETE)

    public Msg delete(@RequestBody List<Integer> ids){
        syncFilePlanService.delete(ids);
        return Msg.success();
    }
    @PostMapping("/jobStatus")
    @ResponseBody
    @Log(title = "文件同步计划表",businessType = OperateType.UPDATE)

    public Msg changeStatus(Integer id,Integer jobStatus) {
        SyncFilePlan syncFilePlan=syncFilePlanService.findById(id);
        if(syncFilePlan==null){
            return Msg.fail().addData("文件同步计划[id="+id+"]不存在！");
        }
        log.info(syncFilePlan.toString());
        syncFilePlan.setJobStatus(jobStatus);
        syncFilePlanService.updateStatus(syncFilePlan);
        return Msg.success();
    }
    @PostMapping("/invoke")
    @ResponseBody
    @Log(title = "文件同步计划表",businessType = OperateType.OTHER)
    public Msg invokeMethod(Integer id){
        SyncFilePlan syncFilePlan=syncFilePlanService.findById(id);
        if(syncFilePlan.getTaskEntity()==null){
            return Msg.fail().addData("未配置所属任务组！");
        }
        boolean flag;
        try {
            syncFilePlanService.updateIsConcurrent(syncFilePlan.getId(),1);
            flag = TaskUtils.invokMethod(syncFilePlan);
            syncFilePlanService.updateIsConcurrent(syncFilePlan.getId(),0);

        }catch (Exception e){
            e.printStackTrace();
            syncFilePlanService.updateIsConcurrent(syncFilePlan.getId(),0);
            return Msg.fail().addData(ExceptionUtil.getStackTraceInfo(e));
        }

        if(flag){
            return Msg.success();
        }
        return Msg.fail();

    }
}
