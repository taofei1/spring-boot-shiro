package com.neo.web;

import com.neo.annotation.Log;
import com.neo.entity.Role;
import com.neo.entity.RolePermission;
import com.neo.entity.UserInfo;
import com.neo.enums.OperateType;
import com.neo.exception.BusinessException;
import com.neo.service.PermissionService;
import com.neo.service.RoleService;
import com.neo.util.Msg;
import com.neo.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/role")
@Controller
public class RoleController {
   private Logger log= LoggerFactory.getLogger(this.getClass());
    @Autowired
    private RoleService roleService;
    @Autowired
    private PermissionService permissionService;
    @GetMapping("/roleList")
    @ResponseBody
    public Page<Role> findAll(@RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize, @RequestParam(value = "sortName",required = false) List<String> sortName, @RequestParam(value="sortOrder",required = false)List<Integer> type, Role role)  {

        Page<Role> page;
        if(Util.isNull(role)) {
            page = roleService.findAll(pageNum, pageSize, sortName, type);
        }else{
            page=roleService.findAll(pageNum, pageSize, sortName, type,role);
        }
        return page;
    }
    @GetMapping("/{id}")
    @ResponseBody
    public Msg getById(@PathVariable("id") Integer id){
        if(id==null){
            return Msg.fail().addData("no parameter id");
        }
        Role role=roleService.findById(id);
        if(role==null){
            return Msg.fail().addData("not exist role[id="+id+"]");
        }
        return Msg.success().addData(role);
    }
    @GetMapping("/all")
    @ResponseBody
    public Msg finAll()
    {

        return Msg.success().addData(roleService.findAll());
    }
    @GetMapping("/enable")
    @ResponseBody
    public Msg findRoleByState(){
        return Msg.success().addData(roleService.findByState(1));
    }
    @PostMapping("/saveOrUpdate")
    @ResponseBody
    @Log(title = "角色表",businessType = OperateType.INSERT)
    public Msg add(Role role){
        if(role.getId()==null){
            role.setState(1);
        }else{
            Role sysRole=roleService.findById(role.getId());
            role.setPermissions(sysRole.getPermissions());
        }

        roleService.save(role);
        return Msg.success();
    }
    @PostMapping("/authorized")
    @ResponseBody
    @Log(title = "角色表",businessType = OperateType.GRANT)
    public Msg authorized(@RequestParam(value = "roleId") Integer roleId,@RequestParam(value = "permissionIds[]") List<Integer> permissionIds){
        Role role=roleService.findById(roleId);
        if(role==null){
            return Msg.fail().addData("not exist role[id="+roleId+"]");
        }
        List<RolePermission> list=new ArrayList<>();
        for(Integer id:permissionIds){
            RolePermission sysPermission=permissionService.findById(id);
            if(sysPermission==null){
                return Msg.fail().addData("not exist permission[id="+id+"]");
            }
            list.add(sysPermission);
        }
        role.setPermissions(list);
     //   ShiroUtil.clearCachedAuthorizationInfo();
        roleService.save(role);
        return Msg.success().addData(role);
    }
    @PostMapping("/delete")
    @ResponseBody
    @Log(title = "角色表",businessType = OperateType.DELETE)
    public Msg delete(@RequestBody List<Integer> ids) throws BusinessException {

        roleService.delete(ids);
        return Msg.success();
    }
    @PostMapping("/status")
    @ResponseBody
    @Log(title = "角色表",businessType = OperateType.UPDATE)
    public Msg changeStatus(Integer id,Integer status){
        Role role=roleService.findById(id);
        if(role==null){
            return Msg.fail().addData("权限不存在！");
        }
        role.setState(status);
        roleService.save(role);
        return Msg.success();
    }

}
