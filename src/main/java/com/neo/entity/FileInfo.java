package com.neo.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "sys_file_info")
public class FileInfo implements Serializable{
    @Id@GeneratedValue
    private Integer id;
    private String fileName;
    private String filePath;
    private Long fileSize;
   // @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
   // @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @CreatedDate
    private Date createTime; //文件第一次同步时间
   // @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  //  @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @LastModifiedDate
    private Date updateTime;//文件最近一次同步时间
    private Long lastModifyTime;
    @ManyToOne(cascade={CascadeType.MERGE,CascadeType.REFRESH},optional=false,fetch = FetchType.LAZY)
    @JoinColumn(name="sync_file_plan_id")
    private SyncFilePlan syncFilePlan;
    public FileInfo() {
    }

    public FileInfo(String fileName, String filePath, Long fileSize,  Long lastModifyTime,  SyncFilePlan syncFilePlan) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.lastModifyTime = lastModifyTime;
        this.syncFilePlan = syncFilePlan;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(Long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }




    public SyncFilePlan getSyncFilePlan() {
        return syncFilePlan;
    }

    public void setSyncFilePlan(SyncFilePlan syncFilePlan) {
        this.syncFilePlan = syncFilePlan;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
