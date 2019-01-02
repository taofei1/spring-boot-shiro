package com.neo.model;

import java.util.List;

public class MenuTree {
    private String id;
    private String name;
    private String url;
    private String icon;
    private Integer state;
    private String parentId;
    private List<MenuTree> childMenus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public List<MenuTree> getChildMenus() {
        return childMenus;
    }

    public void setChildMenus(List<MenuTree> childMenus) {
        this.childMenus = childMenus;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
