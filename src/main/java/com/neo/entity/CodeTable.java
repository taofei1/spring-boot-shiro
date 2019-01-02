package com.neo.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sys_code_table")
public class CodeTable {
    @Id@GeneratedValue
    private Integer id;
    private String code;
    private String  value;
    private String cate;
    private String cateName;
    private Integer status;
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCate() {
        return cate;
    }

    public void setCate(String cate) {
        this.cate = cate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCateName() {
        return cateName;
    }

    public void setCateName(String cateName) {
        this.cateName = cateName;
    }

    @Override
    public String toString() {
        return "CodeTable{" + "id=" + id + ", code='" + code + '\'' + ", value='" + value + '\'' + ", cate='" + cate + '\'' + ", cateName='" + cateName + '\'' + ", status=" + status + '}';
    }
}
