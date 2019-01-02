package com.neo.web;

import com.neo.entity.RolePermission;
import com.neo.model.JsMenuTree;
import com.neo.service.PermissionService;
import com.neo.util.MenuSort;
import com.neo.util.Msg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("ALL")
@Controller
public class MenuController {
    @Autowired
    private PermissionService permissionService;
    @ResponseBody
    @GetMapping("/loadMenuTree2")
    public Msg loadMenuTree(){
        List<JsMenuTree> list=new ArrayList<>();
        List<RolePermission> permissions=permissionService.findAll();
        Collections.sort(permissions,new MenuSort());
        for(RolePermission permission:permissions){
            if(permission.getParentId()==0&&permission.getResourceType().equals("menu")){
                //获取一级菜单及所有子菜单
                JsMenuTree menuTree=getMenu(permission,permissions);
                list.add(menuTree);
            }
        }

        return Msg.success().addData(list);
    }

    private JsMenuTree getMenu(RolePermission permission, List<RolePermission> permissions){
        if(permission.getResourceType().equals("menu")) {
            JsMenuTree menuTree = new JsMenuTree();

            menuTree.setId(permission.getId().toString());
            menuTree.setIcon(permission.getIcon());
            menuTree.setText(permission.getName());
            menuTree.setState(permission.getState());
            if (!StringUtils.isEmpty(permission.getUrl())) {
                menuTree.setUrl(permission.getUrl());
            } else {
                menuTree.setUrl("");
            }
            menuTree.setParentId(permission.getParentId().toString());
            menuTree.setChildren(getAllChildrenMenu(permission, permissions));
            return menuTree;
        }
        return null;
    }
    private List<JsMenuTree> getAllChildrenMenu(RolePermission permission, List<RolePermission> permissions){
        List<JsMenuTree> list=new ArrayList<>();
        for(RolePermission permission1:permissions){
            if(permission1.getParentId().intValue()==permission.getId()){
                JsMenuTree menuTree=new JsMenuTree();
                menuTree.setId(permission1.getId().toString());
                menuTree.setIcon(permission1.getIcon());
                menuTree.setText(permission1.getName());
                menuTree.setState(permission1.getState());
                if(!StringUtils.isEmpty(permission1.getUrl())){
                    menuTree.setUrl(permission1.getUrl());
                }else{
                    menuTree.setUrl("");
                }
                menuTree.setParentId(permission.getParentId().toString());
                menuTree.setChildren(getAllChildrenMenu(permission1,permissions));
                list.add(menuTree);
            }
        }
        return list;
    }

}
