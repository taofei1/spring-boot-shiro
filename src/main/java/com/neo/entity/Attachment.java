package com.neo.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="sys_attachments")
@Data
public class Attachment {

        @Id
        @GeneratedValue
        private Long attachmentId; //附件id


        private String attachmentName;  //附件名字

        private String attachmentPath;  //附件存储路径


        private Long attachmentSize; //附件大小

        private String attachmentType;  //附件类型

        private Date uploadTime;     //附件上传时间

        private String model;          //所属模块 1:email 2:消息

        private String attachmentShuffix; //附件后缀
        public Attachment(Long attachmentId, String attachmentName, String attachmentPath, long attachmentSize,
                          String attachmentType, Date uploadTime, String model, String attachmentShuffix) {
                super();
                this.attachmentId = attachmentId;
                this.attachmentName = attachmentName;
                this.attachmentPath = attachmentPath;
                this.attachmentSize = attachmentSize;
                this.attachmentType = attachmentType;
                this.uploadTime = uploadTime;
                this.model = model;
                this.attachmentShuffix = attachmentShuffix;
        }
        public Attachment(){
        }
}
