package com.neo.web;

import com.neo.annotation.Log;
import com.neo.entity.Dept;
import com.neo.entity.UserInfo;
import com.neo.enums.OperateType;
import com.neo.exception.BusinessException;
import com.neo.service.DeptService;
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
@RequestMapping("/dept")
public class DeptController {
    private Logger log= LoggerFactory.getLogger(this.getClass());
    @Autowired
    public DeptService deptService;
    @GetMapping("/enable")
    @ResponseBody
    public Msg findAll(){
        return Msg.success().addData(deptService.findByState(1));
    }
    @GetMapping("/deptList")
    @ResponseBody
    public Page<Dept> findAll(@RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize, @RequestParam(value = "sortName",required = false) List<String> sortName, @RequestParam(value="sortOrder",required = false)List<Integer> type, Dept dept)  {
        log.info(dept.toString());
        Page<Dept> page;
        if(Util.isNull(dept)) {
            page = deptService.findAll(pageNum, pageSize, sortName, type);
        }else{
            page=deptService.findAll(pageNum, pageSize, sortName, type,dept);
        }
        return page;
    }
    @GetMapping("/{id}")
    @ResponseBody
    public Msg getById(@PathVariable("id") Integer id){
        if(id==null){
            return Msg.fail().addData("no parameter id");
        }
        Dept dept=deptService.findById(id);
        if(dept==null){
            return Msg.fail().addData("not exist department[id="+id+"]");
        }
        return Msg.success().addData(dept);
    }
    @GetMapping("/all")
    @ResponseBody
    public Msg finAll()
    {

        return Msg.success().addData(deptService.findAll());
    }
    @PostMapping("/saveOrUpdate")
    @ResponseBody
    @Log(title = "部门表",businessType = OperateType.INSERT)
    public Msg add(Dept dept){
        if(dept.getId()==null){
            dept.setState(1);
        }
        deptService.save(dept);
        return Msg.success();
    }
    @PostMapping("/delete")
    @ResponseBody
    @Log(title = "部门表",businessType = OperateType.DELETE)
    public Msg delete(@RequestBody List<Integer> ids) throws BusinessException {
        deptService.delete(ids);
        return Msg.success();
    }
    @PostMapping("/status")
    @ResponseBody
    @Log(title = "部门表",businessType = OperateType.UPDATE)
    public Msg changeStatus(Integer id,Integer status){
        Dept dept=deptService.findById(id);
        if(dept==null){
            return Msg.fail().addData("部门[id="+id+"]不存在！");
        }
        dept.setState(status);
        deptService.save(dept);
        return Msg.success();
    }


}
