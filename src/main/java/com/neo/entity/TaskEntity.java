package com.neo.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "sys_task_entity")
public class TaskEntity implements Serializable{
    @Id@GeneratedValue
    private Integer id;
    //任务组名
    private String groupName;
    private String className;
    private String methodName;
    private String param;
    private Integer status;
    @JsonIgnoreProperties(value = { "taskEntity" })
    @OneToMany(mappedBy = "taskEntity",fetch = FetchType.EAGER)
    private  List<SyncFilePlan> syncFilePlanList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public List<SyncFilePlan> getSyncFilePlanList() {
        return syncFilePlanList;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setSyncFilePlanList(List<SyncFilePlan> syncFilePlanList) {
        this.syncFilePlanList = syncFilePlanList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskEntity that = (TaskEntity) o;

        if (groupName != null ? !groupName.equals(that.groupName) : that.groupName != null) return false;
        if (className != null ? !className.equals(that.className) : that.className != null) return false;
        if (methodName != null ? !methodName.equals(that.methodName) : that.methodName != null) return false;
        return param != null ? param.equals(that.param) : that.param == null;
    }

    @Override
    public int hashCode() {
        int result = groupName != null ? groupName.hashCode() : 0;
        result = 31 * result + (className != null ? className.hashCode() : 0);
        result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
        result = 31 * result + (param != null ? param.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TaskEntity{" + "id=" + id + ", groupName='" + groupName + '\'' + ", className='" + className + '\'' + ", methodName='" + methodName + '\'' + ", param='" + param + '\'' + ", status=" + status + ", syncFilePlanList=" + syncFilePlanList + '}';
    }
}
