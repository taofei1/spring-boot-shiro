package com.neo.web;

import com.neo.annotation.Log;
import com.neo.entity.DateInterval;
import com.neo.enums.OperateType;
import com.neo.service.DateIntervalService;
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
@RequestMapping("/dateInterval")
public class DateIntervalController {
    private Logger log= LoggerFactory.getLogger(this.getClass());
    @Autowired
    public DateIntervalService intervalService;
    @GetMapping("/search")
    @ResponseBody
    public Msg findAll(DateInterval interval){

        return Msg.success().addData(intervalService.findAll(interval));
    }
    @GetMapping("/dateIntervalList")
    @ResponseBody
    public Page<DateInterval> findAll(@RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize, @RequestParam(value = "sortName",required = false) List<String> sortName, @RequestParam(value="sortOrder",required = false)List<Integer> type, DateInterval interval)  {
        Page<DateInterval> page;
        if(Util.isNull(interval)) {
            page = intervalService.findAll(pageNum, pageSize, sortName, type);
        }else{
            page=intervalService.findAll(pageNum, pageSize, sortName, type,interval);
        }
        System.out.println(page.getContent());
        return page;
    }
    @GetMapping("/{id}")
    @ResponseBody
    public Msg getById(@PathVariable("id") Integer id){
        if(id==null){
            return Msg.fail().addData("no parameter id");
        }
        DateInterval server=intervalService.findById(id);
        if(server==null){
            return Msg.fail().addData("not exist interval[id="+id+"]");
        }
        return Msg.success().addData(server);
    }
    @GetMapping("/all")
    @ResponseBody
    public Msg finAll()
    {

        return Msg.success().addData(intervalService.findAll());
    }
    @PostMapping("/saveOrUpdate")
    @ResponseBody
    @Log(title = "日期间隔表",businessType = OperateType.INSERT)
    public Msg add(DateInterval interval){
        if(interval.getId()==null){
            interval.setStatus(1);
        }
        log.info(interval.toString());
        intervalService.save(interval);
        return Msg.success();
    }
    @PostMapping("/delete")
    @ResponseBody
    @Log(title = "日期间隔表",businessType = OperateType.DELETE)
    public Msg delete(@RequestBody List<Integer> ids){
        String str="[";
        for(Integer id:ids){
            DateInterval interval=intervalService.findById(id);
            if(interval.getSyncFilePlan()!=null&&interval.getSyncFilePlan().size()>0){
                str+=interval.getId()+",";
            }
        }
        if(!str.equals("[")){
            str=str.substring(0,str.length()-1);
            return Msg.fail().addData(str+"]被同步计划表引用，请解除引用关系后再删除!");
        }
        intervalService.delete(ids);
        return Msg.success();
    }
    @PostMapping("/status")
    @ResponseBody
    @Log(title = "代码表",businessType = OperateType.UPDATE)
    public Msg changeStatus(Integer id,Integer status){
        DateInterval server=intervalService.findById(id);
        if(server==null){
            return Msg.fail().addData("not exist interval[id="+id+"]！");
        }
        server.setStatus(status);
        intervalService.save(server);
        return Msg.success();
    }
    @GetMapping("/enable")
    @ResponseBody
    public Msg findByStatus(){
        return Msg.success().addData(intervalService.findByStatus(1));
    }
}
