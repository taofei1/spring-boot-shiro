package com.neo.web;

import com.neo.annotation.Log;
import com.neo.entity.RolePermission;
import com.neo.entity.Role;
import com.neo.enums.OperateType;
import com.neo.service.PermissionService;
import com.neo.service.RoleService;
import com.neo.util.Msg;
import com.neo.util.StringUtils;
import com.neo.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/permission")
public class PermissionController {
    private Logger log= LoggerFactory.getLogger(this.getClass());
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private RoleService roleService;
    @GetMapping("/permissionList")
    @ResponseBody
    public Page<RolePermission> findAll(@RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize, @RequestParam(value = "sortName",required = false) List<String> sortName, @RequestParam(value="sortOrder",required = false)List<Integer> type, RolePermission permission)  {

        Page<RolePermission> page;
        if(Util.isNull(permission)) {
            page = permissionService.findAll(pageNum, pageSize, sortName, type);
        }else{
            page=permissionService.findAll(pageNum, pageSize, sortName, type,permission);
        }
        return page;
    }
    @GetMapping("/filter")
    @ResponseBody
    public Page<RolePermission> findAll(@RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize, @RequestParam(value = "sortName",required = false) List<String> sortName, @RequestParam(value="sortOrder",required = false)List<Integer> type, @RequestParam(value="ids[]",required = false)List<Integer> ids){

        Page<RolePermission> page=permissionService.findAll(pageNum,pageSize,sortName,type,ids);
        return page;
    }
    @GetMapping("/find")
    @ResponseBody
    public Msg getByIds(List<Integer> ids){
        List<RolePermission> list=permissionService.findAll(ids);
        return Msg.success().addData(list);
    }
    @GetMapping("/{id}")
    @ResponseBody
    public Msg getById(@PathVariable("id") Integer id){
        if(id==null){
            return Msg.fail().addData("no parameter id");
        }
        RolePermission permission=permissionService.findById(id);
        if(permission==null){
            return Msg.fail().addData("not exist permission[id="+id+"]");
        }
        return Msg.success().addData(permission);
    }
    @PostMapping("/saveOrUpdate")
    @ResponseBody
    @Log(title = "权限表",businessType = OperateType.INSERT)
    public Msg add(RolePermission sysPermission){
        if(sysPermission.getId()==null){
            sysPermission.setState(1);
            sysPermission.setIcon("");
        }

        permissionService.save(sysPermission);

        return Msg.success();
    }
    @PostMapping("/delete")
    @ResponseBody
    @Log(title = "权限表",businessType = OperateType.DELETE)
    public Msg delete(@RequestBody List<Integer> ids){

        permissionService.delete(ids);
        return Msg.success();
    }
    @PostMapping("/status")
    @ResponseBody
    @Log(title = "权限表",businessType = OperateType.UPDATE)
    public Msg changeStatus(Integer id,Integer status){
        RolePermission permission=permissionService.findById(id);
        if(permission==null){
            return Msg.fail().addData("权限不存在！");
        }
        permission.setState(status);
        permissionService.save(permission);
        return Msg.success();
    }

}
