package com.neo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sys_date_interval")
public class DateInterval implements Serializable{
    @Id@GeneratedValue
    private Integer id;
    private String singleDate;//单个日期选择
    private String dateRange;//日期区间

    private Integer status;
    @OneToMany(mappedBy = "dateInterval",fetch = FetchType.LAZY)
    @JsonIgnore
    private List<SyncFilePlan> syncFilePlan=new ArrayList<>();
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSingleDate() {
        return singleDate;
    }

    public void setSingleDate(String singleDate) {
        this.singleDate = singleDate;
    }

    public String getDateRange() {
        return dateRange;
    }

    public void setDateRange(String dateRange) {
        this.dateRange = dateRange;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<SyncFilePlan> getSyncFilePlan() {
        return syncFilePlan;
    }

    public void setSyncFilePlan(List<SyncFilePlan> syncFilePlan) {
        this.syncFilePlan = syncFilePlan;
    }

    @Override
    public String toString() {
        return "DateInterval{" + "id=" + id + ", singleDate='" + singleDate + '\'' + ", dateRange='" + dateRange + '\'' + ", status=" + status + ", syncFilePlan=" + syncFilePlan + '}';
    }
}
