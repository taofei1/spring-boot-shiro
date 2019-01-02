package com.neo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "sys_sync_file_plan")
public class SyncFilePlan implements Serializable{
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private Integer  originId;//-1:本地 其它与Server类id关联
    @Transient
    private Server originServer;
    @Transient
    private Server targetServer;
    private String  originPath;//源文件路径
    private Integer targetId;//0：本地 其它与Server类关联
    private String targetPath;//目的文件路径
    private String cronExpression;
    private String nameFilter;
    private Integer isScanSubDir;
    private String dirFilterFormat;
    private Integer jobStatus;
    private Integer isConcurrent;

    //是否验证修改日期
    private Integer isValidDate;
    //是否验证名称日期格式
    private Integer isValidNameDate;
    @Transient
    private String springId;
    @JsonIgnore
    @OneToMany(mappedBy = "syncFilePlan",fetch = FetchType.LAZY)
    private  List<FileInfo> list;
    @ManyToOne(cascade={CascadeType.MERGE,CascadeType.REFRESH})
    @JoinColumn
    private DateInterval dateInterval;
    @JsonIgnoreProperties(value = { "syncFilePlanList" })
    @ManyToOne(cascade={CascadeType.MERGE,CascadeType.REFRESH})
    @JoinColumn
    private  TaskEntity taskEntity;
    private Integer isRegex;
    //是否解压
    private Integer isDeCompress;
    //是否保持目录层次
    private Integer isKeepDirTree;


    //是否目录名_文件名
    private Integer isTransformFilename;
    //是否保留压缩源文件
    private Integer isKeepSource;
    //是否过滤解压后的文件
    private Integer isFilterCompressFile;
    //此项选中不会去判断文件是否修改
    private Integer isCopyAll;
    //触发优先级，仅针对同一时间触发且线程池数量不够
    private Integer priority;
    public TaskEntity getTaskEntity() {
        return taskEntity;
    }

    public void setTaskEntity(TaskEntity taskEntity) {
        this.taskEntity = taskEntity;
    }

    public String getSpringId() {
        return springId;
    }

    public void setSpringId(String springId) {
        this.springId = springId;
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

    public void setName(String name) {
        this.name = name;
    }

    public String getOrginPath() {
        return originPath;
    }

    public void setOrginPath(String originPath) {
        this.originPath = originPath;
    }

    public String getNameFilter() {
        return nameFilter;
    }

    public void setNameFilter(String nameFilter) {
        this.nameFilter = nameFilter;
    }

    public Integer getIsScanSubDir() {
        return isScanSubDir;
    }

    public void setIsScanSubDir(Integer isScanSubDir) {
        this.isScanSubDir = isScanSubDir;
    }

    public String getDirFilterFormat() {
        return dirFilterFormat;
    }

    public void setDirFilterFormat(String dirFilterFormat) {
        this.dirFilterFormat = dirFilterFormat;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public Integer getOriginId() {
        return originId;
    }

    public void setOriginId(Integer originId) {
        this.originId = originId;
    }

    public Integer getTargetId() {
        return targetId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    public String getOriginPath() {
        return originPath;
    }

    public void setOriginPath(String originPath) {
        this.originPath = originPath;
    }

    public Integer getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(Integer jobStatus) {
        this.jobStatus = jobStatus;
    }

    public Integer getIsConcurrent() {
        return isConcurrent;
    }

    public void setIsConcurrent(Integer isConcurrent) {
        this.isConcurrent = isConcurrent;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public Server getOriginServer() {
        return originServer;
    }

    public void setOriginServer(Server originServer) {
        this.originServer = originServer;
    }

    public Server getTargetServer() {
        return targetServer;
    }

    public void setTargetServer(Server targetServer) {
        this.targetServer = targetServer;
    }

    public List<FileInfo> getList() {
        return list;
    }

    public void setList(List<FileInfo> list) {
        this.list = list;
    }

    public DateInterval getDateInterval() {
        return dateInterval;
    }

    public void setDateInterval(DateInterval dateInterval) {
        this.dateInterval = dateInterval;
    }

    public Integer getIsValidDate() {
        return isValidDate;
    }

    public void setIsValidDate(Integer isValidDate) {
        this.isValidDate = isValidDate;
    }

    public Integer getIsValidNameDate() {
        return isValidNameDate;
    }

    public void setIsValidNameDate(Integer isValidNameDate) {
        this.isValidNameDate = isValidNameDate;
    }

    public Integer getIsDeCompress() {
        return isDeCompress;
    }

    public void setIsDeCompress(Integer isDeCompress) {
        this.isDeCompress = isDeCompress;
    }

    public Integer getIsKeepDirTree() {
        return isKeepDirTree;
    }

    public void setIsKeepDirTree(Integer isKeepDirTree) {
        this.isKeepDirTree = isKeepDirTree;
    }

    public Integer getIsTransformFilename() {
        return isTransformFilename;
    }

    public void setIsTransformFilename(Integer isTransformFilename) {
        this.isTransformFilename = isTransformFilename;
    }

    public Integer getIsKeepSource() {
        return isKeepSource;
    }

    public void setIsKeepSource(Integer isKeepSource) {
        this.isKeepSource = isKeepSource;
    }

    public Integer getIsRegex() {
        return isRegex;
    }

    public void setIsRegex(Integer isRegex) {
        this.isRegex = isRegex;
    }
    public Integer getIsFilterCompressFile() {
        return isFilterCompressFile;
    }

    public void setIsFilterCompressFile(Integer isFilterCompressFile) {
        this.isFilterCompressFile = isFilterCompressFile;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getIsCopyAll() {
        return isCopyAll;
    }

    public void setIsCopyAll(Integer isCopyAll) {
        this.isCopyAll = isCopyAll;
    }
}
