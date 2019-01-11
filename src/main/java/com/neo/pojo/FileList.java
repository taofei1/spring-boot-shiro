package com.neo.pojo;

import com.neo.entity.UserInfo;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FileList extends BaseEntity {
    private Long fileId;
    private String fileName;
    private String filePath;
    private Long size;
    private String contentType;
    private String uploadTime;
    private String model;
    private String suffix;
    private Long fileIsTrash = 0L;
    private Long fileIsShare = 0L;
    private UserInfo user;
    private Directory directory;
}
