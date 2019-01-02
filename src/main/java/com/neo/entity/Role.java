package com.neo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sys_role")
public class Role implements Serializable {
    @Id@GeneratedValue
    private Integer id; // 编号
    private String role; // 角色标识程序中判断使用,如"admin",这个是唯一的:
    private String description; // 角色描述,UI界面显示使用
    private Integer state;

    //角色 -- 权限关系：多对多关系;
    @ManyToMany(fetch= FetchType.EAGER)
    @JoinTable(name="SysRolePermission",joinColumns={@JoinColumn(name="roleId")},inverseJoinColumns={@JoinColumn(name="permissionId")})
    private List<RolePermission> permissions=new ArrayList<>();

    // 用户 - 角色关系定义;
    @ManyToMany(mappedBy = "roleList")
    private List<UserInfo> userInfos=new ArrayList<>();// 一个角色对应多个用户



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public List<RolePermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<RolePermission> permissions) {
        this.permissions = permissions;
    }
    @JsonBackReference
    public List<UserInfo> getUserInfos() {
        return userInfos;
    }
    @JsonBackReference
    public void setUserInfos(List<UserInfo> userInfos) {
        this.userInfos = userInfos;
    }

    @Override
    public String toString() {
        return "Role{" + "id=" + id + ", role='" + role + '\'' + ", description='" + description + '\'' + ", state=" + state + ", permissions=" + permissions + '}';
    }
}