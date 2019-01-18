package com.neo.pojo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CloudFile extends BaseEntity {
    //文件id
    private Long fileId;
    //文件名
    private String fileName;
    //文件路径
    private String filePath;
    //大小
    private Long size;
    //文件类型
    private String contentType;
    //文件模块,保留字段
    private String model;
    //文件后缀
    private String suffix;
    //是否是垃圾 0:否 1:是垃圾且可见 2：是垃圾且不可见
    private Integer isTrash = 0;
    //是否共享 0:否 1是
    private Integer isShare = 0;
    //所属用户
    private Long userId;
    //顺序
    private Long fileOrder;
    //是否是目录 0：否 1:是
    private Integer isDirectory;
    //上级目录id
    private Long parentId;

}
