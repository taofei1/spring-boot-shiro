package com.neo.web;


import com.neo.entity.LoginLog;
import com.neo.entity.RolePermission;
import com.neo.entity.Role;
import com.neo.entity.UserInfo;
import com.neo.exception.CaptchaException;
import com.neo.model.MenuTree;
import com.neo.service.UserInfoService;

import com.neo.service.UserLoginLogService;
import com.neo.util.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class HomeController {
    Logger log=LoggerFactory.getLogger(this.getClass());
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private UserLoginLogService userLoginLogService;

    @RequestMapping({"/","/index"})
    public String index(HttpServletRequest request,Map<String,Object> map){
        //获取用户信息
        UserInfo ui = ShiroUtil.getSysUser();
        map.put("userInfo",ui);
        if(ui.getIsFirst()==0) {
            map.put("isFirst", 0);
        }
        return "index";
    }

    @RequestMapping("/isRem")
    @ResponseBody
    public Response get() {
        return Response.success(SecurityUtils.getSubject().isRemembered());
    }
    @RequestMapping("/vip")
    public String admin(){
        return "vip";
    }
    @RequiresPermissions("email:setting")
    @RequestMapping("/email_setting")
    public String emailSetting(){
        return "email_setting";
    }
    @RequestMapping("/home")
    public String home(){
        return "home";
    }
    @RequiresPermissions("userInfo:manager")
    @RequestMapping("/table")
    public String table(@ModelAttribute("name") String name){

        return "table";
    }
    @RequiresPermissions("userInfo:manager")
    @RequestMapping("/form")
    public String form(){

        return "form";
    }
    @RequiresPermissions("userInfo:manager")
    @RequestMapping("/form1")
    public String form1(){

        return "form1";
    }

    @RequiresPermissions("permission:manager")
    @RequestMapping("/permissionList")
    public String permissions(){

        return "permission_table";
    }
    @RequiresPermissions("role:manager")
    @RequestMapping("/roles")
    public String roles(){

        return "role_table";
    }
    @RequiresPermissions("role:manager")
    @RequestMapping("/role_form")
    public String rolesForm(){

        return "role_form";
    }
    @RequiresPermissions("dept:manager")
    @RequestMapping("/deptTable")
    public String deptTable(){

        return "dept_table";
    }
    @RequiresPermissions("dept:manager")
    @RequestMapping("/deptForm")
    public String deptForm(){

        return "dept_form";
    }
    @RequiresPermissions("server:manager")
    @RequestMapping("/serverTable")
    public String serverTable(){

        return "server_table";
    }
    @RequiresPermissions("server:manager")
    @RequestMapping("/serverForm")
    public String serverForm(){

        return "server_form";
    }
    @RequiresPermissions("task:manager")
    @RequestMapping("/taskTable")
    public String taskTable(){

        return "task_table";
    }
    @RequiresPermissions("task:manager")
    @RequestMapping("/taskForm")
    public String taskForm(){

        return "task_form";
    }
    @RequiresPermissions("codeTable:manager")
    @RequestMapping("/codeTable")
    public String codeTable(){

        return "code_table";
    }
    @RequiresPermissions("codeTable:manager")
    @RequestMapping("/codeForm")
    public String codeForm(){

        return "code_form";
    }
    @RequiresPermissions("dateInterval:manager")
    @RequestMapping("/dateIntervalTable")
    public String dateIntervalTable(){

        return "dateInterval_table";
    }
    @RequiresPermissions("dateInterval:manager")
    @RequestMapping("/dateIntervalForm")
    public String dateIntervalForm(){

        return "dateInterval_form";
    }
    @RequiresPermissions("file:sync")
    @RequestMapping("/syncPlanTable")
    public String syncPlanTable(){

        return "syncPlan_table";
    }
    @RequiresPermissions("file:sync")
    @RequestMapping("/syncPlanForm")
    public String syncPlanForm(){

        return "syncPlan_form";
    }
    @RequiresPermissions("file:sync")
    @RequestMapping("/serverTableAlert")
    public String serverTableAlert(){

        return "server_table_alert";
    }
    @RequiresPermissions("permission:manager")
    @RequestMapping("/permission_form")
    public String permission(){

        return "permission_form";
    }
    @RequestMapping("/modifyPwd")
    public String modifyPwd(){

        return "modifyPwd";
    }

    @RequestMapping("/mailSend")
    @RequiresPermissions("mail:send")
    public String mailSend(){
        return "email_send";
    }
    @RequiresPermissions("mail:send")
    @RequestMapping("/writeMail")
    public String writeMail(){
        return "/mail/writeMail";
    }
    @RequestMapping("/mailPersonalSetting")
    @RequiresPermissions("mail:personalSetting")
    public String mailSetting(){
        return "personalEmail_setting";
    }

    @GetMapping("/loginLogsTable")
    @RequiresPermissions("system:loginLog")
    public String loginLogTable(){
        return "/monitor/loginLog";
    }

    @RequestMapping("/403")
    public String unauthorizedRole(){
        System.out.println("------没有权限-------");
        return "403";
    }



    @ResponseBody
    @GetMapping("/loadMenuTree")
    public Msg loadMenuTree(){
        List<MenuTree> list=new ArrayList<>();
        List<RolePermission> permissions=getUserPermissions();
        Collections.sort(permissions,new MenuSort());
        for(RolePermission permission:permissions){
            if(permission.getParentId()==0&&permission.getResourceType().equals("menu")&&permission.getState()==1){
                //获取一级菜单及所有子菜单
                MenuTree menuTree=getMenu(permission,permissions);
                list.add(menuTree);
            }
        }

        return Msg.success().addData(list);
    }

    private MenuTree getMenu(RolePermission permission, List<RolePermission> permissions){
        MenuTree menuTree=new MenuTree();
        if(permission.getResourceType().equals("menu")&&permission.getState()==1) {
            menuTree.setId(permission.getId().toString());
            menuTree.setIcon(permission.getIcon());
            menuTree.setName(permission.getName());
            menuTree.setState(permission.getState());
            if (!StringUtils.isEmpty(permission.getUrl())) {
                menuTree.setUrl(permission.getUrl());
            } else {
                menuTree.setUrl("");
            }
            menuTree.setParentId(permission.getParentId().toString());
            menuTree.setChildMenus(getAllChildrenMenu(permission, permissions));
        }
        return menuTree;
    }
    private List<MenuTree> getAllChildrenMenu(RolePermission permission, List<RolePermission> permissions){
        List<MenuTree> list=new ArrayList<>();
        for(RolePermission permission1:permissions){
            if(permission1.getParentId().intValue()==permission.getId()&&permission1.getState()==1){
                MenuTree menuTree=new MenuTree();
                menuTree.setId(permission1.getId().toString());
                menuTree.setIcon(permission1.getIcon());
                menuTree.setName(permission1.getName());
                menuTree.setState(permission1.getState());
                if(!StringUtils.isEmpty(permission1.getUrl())){
                    menuTree.setUrl(permission1.getUrl());
                }else{
                    menuTree.setUrl("");
                }
                menuTree.setParentId(permission.getParentId().toString());
                menuTree.setChildMenus(getAllChildrenMenu(permission1,permissions));
                list.add(menuTree);
            }
        }
        return list;
    }

    private List<RolePermission> getUserPermissions(){
        UserInfo userInfo=(UserInfo)SecurityUtils.getSubject().getPrincipal();
        if(userInfo==null){
            return null;
        }
        UserInfo ui= userInfoService.findById(userInfo.getUid());
        List<Role> roleList=ui.getRoleList();
        Set<RolePermission> sysPermissions=new HashSet<>();
        for(Role role:roleList){
                List<RolePermission> permissions = role.getPermissions();

                for (RolePermission permission : permissions) {
                    sysPermissions.add(permission);
                }
        }

        return new ArrayList<>(sysPermissions);
    }






}