package com.neo.model;

import com.neo.entity.UserInfo;

/**
 * dubbo中consumer调用provider
 * 进行分页排序时的参数实体
 */
public class PageParam {
    //页数
    private int pageNum;
    //每页显示的个数
    private int pageSize;
    //排序的字段
    private String sortName;
    //排序的方式0：升序 1：降序
    private Integer type;
    //过滤的模型实体
    private UserInfo userInfo;

    public PageParam(int pageNum, int pageSize, String sortName, Integer type, UserInfo userInfo) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.sortName = sortName;
        this.type = type;
        this.userInfo = userInfo;
    }

    public PageParam() {
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String toString() {
        return "PageParam{" + "pageNum=" + pageNum + ", pageSize=" + pageSize + ", sortName='" + sortName + '\'' + ", type=" + type + ", userInfo=" + userInfo + '}';
    }
}
