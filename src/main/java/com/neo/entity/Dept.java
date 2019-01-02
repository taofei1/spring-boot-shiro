package com.neo.entity;



import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sys_dept")
public class Dept implements Serializable{


    @Id@GeneratedValue
    private Integer id;
    private String name;
    private String location;
    private Integer state;
    //用户部门多对多
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="SysUserDept",joinColumns = {@JoinColumn(name="deptId",referencedColumnName = "id")},inverseJoinColumns ={@JoinColumn(name = "uid",referencedColumnName = "uid") })
    List<UserInfo> userInfoList=new ArrayList<>();

    public void setName(String name) {
        this.name = name;
    }
    @JsonBackReference
    public List<UserInfo> getUserInfoList() {
        return userInfoList;
    }
    @JsonBackReference
    public void setUserInfoList(List<UserInfo> userInfoList) {
        this.userInfoList = userInfoList;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
